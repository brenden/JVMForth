import org.objectweb.asm.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;

//JVMForthCompiler parses a Forth input file and writes JVM bytecode to an output file
public class JVMForthCompiler implements Opcodes {

    private static enum State {NORMAL, COMMENT, WORD_HEADER, WORD_BODY, VARIABLE, CONSTANT}

    //Return the contents of the source file as a string
    private static String readSource(String sourceFile) throws IOException {

        FileInputStream fileStream = new FileInputStream(sourceFile);
        DataInputStream dataStream = new DataInputStream(fileStream);
        InputStreamReader unbufferedReader = new InputStreamReader(dataStream);
        BufferedReader bufferedReader = new BufferedReader(unbufferedReader);

        //Read contents into a StringBuffer
        StringBuffer source = new StringBuffer();
        String line;

        for (;;) {

            line = bufferedReader.readLine();
            if (line==null) break;
            source.append(line);
        }

        bufferedReader.close();
        return source.toString();
    }

    //Parse the source into individual tokens
    private static String[] parse(String raw) {

        Pattern splitWhite = Pattern.compile("\\s+");
        String[] tokens = splitWhite.split(raw);

        //Forth is case-insensitive
        for (int i=0; i<tokens.length; i++) {

            tokens[i] = tokens[i].toLowerCase();
        }

        return tokens;
    }

    //Find the appropriate word for each token; handle parsing word logic for appropriate tokens
    private static Word[] wordify(String[] tokens) throws WordException, RoutineException {

        List<Word> words = new ArrayList<Word>();
        State state = State.NORMAL;

        for (String token : tokens) {

            Word asWord = null;
        
            switch (state) {

                //Not in a comment or word definition
                case NORMAL:

                    if (token.equals(":")) {
                        state = State.WORD_HEADER;
                    }
                    else if (token.equals("(")) {
                        state = State.COMMENT;
                    }
                    else {
                        asWord = handleNormalWord(token);
                    }
                break;

                //Within parentheses: all tokens are ignored
                case COMMENT:

                    if (token.equals(")")) {
                        state = State.NORMAL;
                    }
                break;

                //The last word was a colon. This word is the routine's name.
                case WORD_HEADER:

                    state = State.WORD_BODY;
                    RoutineWord.WordJump wordJump = RoutineWord.defineNewRoutine(token);
                    asWord = new Colon(wordJump.getLabel(), wordJump.getExecutionToken());
                break;

                //The body of a word. No new words can be defined within a word
                case WORD_BODY:

                    if (token.equals(";")) {
                        state = State.NORMAL;
                        asWord = new Semicolon();
                    }
                    else if (token.equals("(")) {
                        state = State.COMMENT;
                    }
                    else if (token.equals(":")) {
                        throw new WordException("Illegal : in word definition");
                    }
                    else {
                        asWord = handleNormalWord(token);
                    }
                break; 
            } 

            if (asWord!=null) words.add(asWord);
        }

        return words.toArray(new Word[words.size()]);
    }

    //Return a Word object for a non-parsing word token
    private static Word handleNormalWord(String token) throws WordException {

        try {

             return new RoutineWord(token);
        }
        catch (WordException _) {

            try {

                return PrimitiveFactory.makePrimitive(token); 
            }
            catch (WordException __) {

                try {

                    return NumberFactory.makeNumber(token);
                }
                catch (WordException ___) {

                    throw new WordException("Undefined word \""+token+"\"");
                }
            }
        }
    }

    //Create byte array of compiled output
    private static byte[] byteify(Word[] words, String outputName) {

        //Set up class for compiled output
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv = prepClass(cw, outputName);

        //Write each word's bytecode to the class
        mv.visitCode();

        for (Word word : words) {

            mv = word.write(mv); 
        }

        //Close out class
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        //Return ClassWriter as a byte array
        byte[] contents = cw.toByteArray();
        return contents;
    }

    //Create the data stack, return stack, and memory fields
    private static MethodVisitor prepClass(ClassWriter cw, String outputName) {

        FieldVisitor fv;
        cw.visit(V1_6, ACC_PUBLIC, outputName, null, "java/lang/Object", null);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC+ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 1);
        String[] staticFields = {"data", "return", "memory"};
        
        for (String name : staticFields) {

            //First create the field
            fv = cw.visitField(ACC_PRIVATE+ACC_STATIC, name, "[I", null, null);
            if (fv!=null) fv.visitEnd();

            //Then initialize it    
            mv.visitIntInsn(SIPUSH, 1024);
            mv.visitIntInsn(NEWARRAY, T_INT);
            mv.visitFieldInsn(PUTSTATIC, outputName, name, "[I");
        }

        return mv;
    }

    //Write the byte array to the specified output file
    private static void writeBytes(byte[] bytes, String outputName) {

        outputName = outputName + ".class"; 

        try {

            FileOutputStream out = new FileOutputStream(outputName);
            out.write(bytes);
            out.close();
        }
        catch (IOException _) {

            System.err.println("Could not write to output file.");
        }
    }

    //From source code to JVM bytecode
    public static void main(String[] args) {

        //Get the input name; determine the compiled output name
        String inputName = args[0];
        String outputName = inputName.split("\\.")[0];
        Word.setOutputName(outputName);

        try {

            String source = readSource(inputName);
            String[] parsed = parse(source);
            Word[] words = wordify(parsed);
            byte[] bytes = byteify(words, outputName);
            writeBytes(bytes, outputName);
        }
        catch (WordException issue) {

            System.err.println(issue.getMessage());
        }
        catch (RoutineException issue) {

            System.err.println(issue.getMessage());
        }
        catch (IOException _) {

            System.err.println("Could not read from source file "+inputName);
        }
    }
}
