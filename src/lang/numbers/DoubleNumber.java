import org.objectweb.asm.*;

//A 64-bit number
public class DoubleNumber extends Word {

    private int highValue;
    private int lowValue;
    private static final int SINGLE_BITS = 32;

    public DoubleNumber(long value) {
        
       highValue = (int) (value >> SINGLE_BITS); 
       lowValue = (int) value;
    } 

    public MethodVisitor write(MethodVisitor mv) {

        mv.visitIntInsn(SIPUSH, lowValue);
        mv.visitIntInsn(SIPUSH, highValue);
        return mv;
    } 
}
