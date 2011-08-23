import org.objectweb.asm.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;

public class JVMForthCompiler implements Opcodes {

    //Return the contents of the source file as a string
    private static String readSource(String sourceFile) throws IOException {

        FileInputStream fileStream = new FileInputStream(sourceFile);
        DataInputStream dataStream = new DataInputStream(fileStream);
        InputStreamReader unbufferedReader = new InputStreamReader(dataStream);
        BufferedReader bufferedReader = new BufferedReader(unbufferedReader);

        //Read contents into a StringBuffer
        StringBuffer source;
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

    //Find the appropriate word for each token; throw an error if there is none
    private static Word[] wordify(String[] tokens) throws WordException {

        Word[] words = new Word[tokens.length];
        int i = 0;

        for (String token : tokens) {
            Word asWord;

            try {
                asWord = PrimitiveFactory.makePrimitive(token);
            }
            catch (WordException _) {
                try {
                    asWord = DefinedFactory.makeDefined(token);
                }
                catch(WordException _) {
                    throw new WordException("Undefined word \""+token+"\" at location "+i);
                }
            }
            finally {
                words[i++] = asWord;
            } 
        }
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

    public static void main(String[] args) {
            
    }
}
