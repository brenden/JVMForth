import java.util.*;

public class PrimitiveFactory { 

    private static Map<String, String> nameReplacements;

    //Initialize the nameReplacements lookup
    static {
        
       nameReplacements = new HashMap<String, String>();

       //Parallel arrays of bad and good names
       String[] badNames = {":", ";", "+", "-", "/", "*", "<", "<>", ">", "=", "@"};
       String[] goodNames = {"Colon", "Semicolon", "Add", "Subtract", "Divide", "Multiply", "LT", "NE", "GT", "EQ", "At"};

       for (int i=0; i<Math.min(badNames.length, goodNames.length); i++) {

            nameReplacements.put(badNames[i], goodNames[i]);
       }
    }

    //Create the primitive corresponding to wordName. Throw an error if no such primitive exists.
    public static Word makePrimitive(String wordName) throws WordException {

        try {
             
            wordName = (nameReplacements.keySet().contains(wordName)) ? nameReplacements.get(wordName) : wordName;
            Word primitiveWord = (Word) Class.forName(wordName).newInstance();
            return primitiveWord;
        }
        catch (Throwable _) {

            throw new WordException("Unknown word: "+wordName);
        }
    } 
}
