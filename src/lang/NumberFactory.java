public class NumberFactory { 

    public static Word makeNumber(String wordName) throws WordException {

        try {
            String[] parts = wordName.split("\\.");

            switch (parts.length) {

                case 1:
                    String singleString = parts[0];
                    int singleValue = Integer.parseInt(singleString);
                    return new SingleNumber(singleValue);

                case 2:
                    String doubleString = parts[0] + parts[1];
                    long doubleValue = Long.parseLong(doubleString);
                    return new DoubleNumber(doubleValue);

                default:
                    throw new WordException(""); 
            }
        }
        catch (NumberFormatException _) {
            throw new WordException("Unknown word: "+wordName);
        }
        catch (WordException _) {
            throw new WordException("Unknown word: "+wordName);
        }
    } 
}
