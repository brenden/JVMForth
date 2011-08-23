import org.objectweb.asm.*;

//The start of a colon definition
public class Semicolon extends Word {

    public Semicolon () {}

    public MethodVisitor write(MethodVisitor mv) {

        return mv;  
    }
}
