import org.objectweb.asm.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;

public class JVMForthCompiler implements Opcodes {

    private static enum State {
        NORMAL, COMMENT, WORD_HEADER, WORD_BODY
    }

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

        for (int i=0; i<tokens.length; i++) {
            tokens[i] = tokens[i].toLowerCase();
        }

        return tokens;
    }

    //Find the appropriate word for each token; handle parsing word logic for appropriate tokens
    private static Word[] wordify(String[] tokens) throws WordException, RoutineException {

        Word[] words = new Word[tokens.length];
        State state = State.NORMAL;
        int i = 0;

        for (String token : tokens) {
            Word asWord;
        
            switch (state) {

                case NORMAL:
                    if (token.equals(":")) {
                        state = State.WORD_HEADER;
                    }
                    else if (token.equals("(")) {
                        state = State.COMMENT;
                    }
                    else {
                        asWord = handleNormalWord(token);
                        words[i++] = asWord; 
                    }
                break;

                case COMMENT:
                    if (token.equals(")")) {
                        state = State.NORMAL;
                    }
                break;

                case WORD_HEADER:
                    state = State.WORD_BODY;
                    RoutineWord.WordJump wordJump = RoutineWord.defineNewRoutine(token);
                    asWord = new Colon(wordJump.getLabel(), wordJump.getExecutionToken());
                    words[i++] = asWord; 
                break;

                case WORD_BODY:
                    if (token.equals(";")) {
                        state = State.NORMAL;
                        asWord = new Semicolon();
                        words[i++] = asWord; 
                    }
                    else if (token.equals("(")) {
                        state = State.COMMENT;
                    }
                    else if (token.equals(":")) {
                        throw new WordException("Illegal : in word definition");
                    }
                    else {
                        asWord = handleNormalWord(token);
                        words[i++] = asWord; 
                    }
                break;
            } 
        }

        return words;
    }

    //Create byte array of compiled output
    private static byte[] byteify(Word[] words, String outputName) {

        //Set up class for compiled output
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_6, ACC_PUBLIC, outputName, null, "java/lang/Object", null);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC+ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
     
        //Write each word's bytecode to the given class file
        mv.visitCode();

        for (Word word : words) {
            mv = word.write(mv); 
        }

        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        //Return ClassWriter as a byte array
        byte[] contents = cw.toByteArray();
        return contents;
    }

    //Return a Word object for a non-parsing word token
    private static Word handleNormalWord(String token) throws WordException {

        try {
            return PrimitiveFactory.makePrimitive(token);
        }
        catch (WordException _) {
            try {
                return new RoutineWord(token);
            }
            catch(WordException __) {
                throw new WordException("Undefined word \""+token+"\"");
            }
        }
    }

    public static void main(String[] args) {
            
    }
}
