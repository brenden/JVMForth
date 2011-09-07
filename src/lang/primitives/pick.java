import org.objectweb.asm.*;

public class pick extends Word {

    public pick () {}

    //Copy the nth deep element to the top of the stack
    public MethodVisitor write(MethodVisitor mv) {

        String outputName = getOutputName();

        mv.visitFieldInsn(GETSTATIC, outputName, "data", "[I");
        mv.visitInsn(DUP)
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IALOAD);
        mv.visitInsn(IALOAD);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(SWAP);
        mv.visitInsn(IASTORE);

        return mv;
    }
}
