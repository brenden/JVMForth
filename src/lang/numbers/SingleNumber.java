import org.objectweb.asm.*;

//A 32-bit number
public class SingleNumber extends Word {

    private int value; 

    public SingleNumber(int value) {
        
        this.value = value;
    } 

    public MethodVisitor write(MethodVisitor mv) {

        return mv;
    }    
}
