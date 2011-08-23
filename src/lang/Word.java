import org.objectweb.asm.*;

//Common parent for both primitives and defined words
public abstract class Word {

    //Write the bytecode for the word
    public abstract MethodVisitor write(MethodVisitor mv);
}
