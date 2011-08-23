public static class PrimitivesFactory {

    private static Set<String> primitiveNames;

    static {
        String[] primitiveNamesArr = {"push", "swap", "do", "loop", "+loop", "begin", "until", ">r", "r>"};
        primitiveNames = new HashSet<String>(Arrays.asList(primitiveNamesArr));
    }

    private Word makePrimitive(String wordName) throws WordException {

        if (primitiveNames.contains(wordName)) {

            try {
                Word primitiveWord = Class.forname(wordName).newInstance();
                return primitiveWord;
            }
            catch (Throwable _) {
                throw new WordException();
            }
        }
        else {
            throw new WordException();
        }
    }
}
