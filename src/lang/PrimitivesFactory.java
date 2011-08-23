public static class PrimitivesFactory { 

    private Word makePrimitive(String wordName) throws WordException {

        try {
            Word primitiveWord = Class.forname(wordName).newInstance();
            return primitiveWord;
        }
        catch (Throwable _) {
            throw new WordException();
        }
    } 
}
