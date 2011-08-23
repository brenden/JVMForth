import org.objectweb.asm.*;
import java.util.*;

//The start of a colon definition
public class Colon extends Word {

    private Label label;
    private int executionToken;

    public Colon (Label label, int executionToken) {

        this.label = label;
        this.executionToken = executionToken;
    }

    public MethodVisitor write(MethodVisitor mv) {

        return mv;  
    }
}
