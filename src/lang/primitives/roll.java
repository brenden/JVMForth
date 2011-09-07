import org.objectweb.asm.*;
import java.util.*;

public class roll extends Word {

    public roll () {}

    public MethodVisitor write(MethodVisitor mv) {

        String outputName = getOutputName();

        mv.visitFieldInsn(GETSTATIC, outputName, "data", "[I");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IALOAD);

        //field top    
        mv.visitInsn(DUP);
        mv.visitVarInsn(ISTORE, 2);

        //field top |2|->top(n)
        mv.visitInsn(IALOAD);
        mv.visitVarInsn(ISTORE, 3);

        //field |2|->top(n) |3|->nth-element
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(ISUB);

        //field n-1
        Label startShift = new Label();
        Label endShift = new Label();
        mv.visitLabel(startShift);
        mv.visitInsn(DUP);
        mv.visitJumpInsn(IFGT, endShift);
        {
            mv.visitInsn(DUP);
            mv.visitInsn(IALOAD);

            //field n-1 n-1th element
            mv.visitInsn(IASTORE);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(ISUB);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ISTORE, 2);

            //field n-1 |2|->n-1 |3|->nth-element
            mv.visitJumpInsn(GOTO, startShift);
        }

        mv.visitLabel(endShift);
        
        //field 0
        mv.visitInsn(POP);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(ISUB);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ISTORE, 0); //decrement stack pointer
        
        //field i-1
        mv.visitVarInsn(ILOAD, 3);
        mv.visitInsn(IASTORE);

        return mv;
    }
}
