import java.util.*;
import java.util.regex.*;
import java.io.*;

public class JVMForthCompiler {

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

    public static void main(String[] args) {
        
        
    }
}
