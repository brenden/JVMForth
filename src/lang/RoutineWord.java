import org.objectweb.asm.*;

public class RoutineWord extends Word {

    private String name;       
    private static Map<String, Map<int, Label>> routineLabels;
    private static int executionTokenCounter = 0;

    static {
        routineLabels = new HashMap<String, Map<int, Label>>();
    }

    public RoutineWord(String routineName) throws WordException {

        if (rountineNames.contains(routineName)) {
            this.name = name;
        }
        else {
            throw new WordException();
        }
    }

    public static Label defineNewRoutine(String routineName) throws RoutineException {

        if (!routineLabels.keySet().contains(routineName) {
            Label routineLabel = new Label(routineName);
            routineLabels.set(routineName, routineLabel);

            return routineLabel;
        }
        else {
            throw new RoutineException();
        }
    }

    public MethodVisitor write(MethodVisitor mv) {
    }    
}
