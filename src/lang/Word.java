import org.objectweb.asm.*;

//Common parent for both primitives and defined words
public abstract class Word implements Opcodes {

    private static String outputName;

    //Setter for fileName
    public static void setOutputName(String outputName) {

        Word.outputName = outputName;
    }

    //Getter for fileName
    public static String getOutputName() {

        return outputName;
    }

    //Write the bytecode for the word
    public abstract MethodVisitor write(MethodVisitor mv);
}
