public class PrimitiveFactory { 

    public static Word makePrimitive(String wordName) throws WordException {

        try {
            Word primitiveWord = (Word) Class.forName(wordName).newInstance();
            return primitiveWord;
        }
        catch (Throwable _) {
            throw new WordException("Unknown primitive: "+wordName);
        }
    } 
}
