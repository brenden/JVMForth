import org.objectweb.asm.*;
import java.util.*;

//Any non-primitive word (user-defined or library)
public class RoutineWord extends Word {

    private String name;       

    //A helper class which associates an execution token with a Label object
    public static class WordJump {
        
        private int executionToken;
        private Label label;

        WordJump(int executionToken, Label label) {
        
            this.executionToken = executionToken;
            this.label = label;
        }

        int getExecutionToken() {

            return executionToken;
        }

        Label getLabel() {

            return label;
        }
    }

    //These static fields coordinate the assignment of labels and execution tokens across words
    private static Map<String, WordJump> routineLookup;
    private static int executionTokenCounter = 0;

    static {
        routineLookup = new HashMap<String, WordJump>();
    }

    //If the word has been defined, create a new RoutineWord object for that word
    public RoutineWord(String routineName) throws WordException {

        if (routineLookup.keySet().contains(routineName)) {
            this.name = name;
        }
        else {
            throw new WordException("Unknown routine: "+routineName);
        }
    }

    //Define a new word. If the word was already defined, throw a RoutineException
    public static WordJump defineNewRoutine(String routineName) throws RoutineException {

        if (!routineLookup.keySet().contains(routineName)) {

            //Create a new WordJump; add it to routineLookup 
            int executionToken = executionTokenCounter++;
            Label routineLabel = new Label();
            WordJump wordJump = new WordJump(executionToken, routineLabel);
            routineLookup.put(routineName, wordJump);

            //The new Label is passed back to the caller to be written in the word definition
            return wordJump;
        }
        else {
            throw new RoutineException("Word "+routineName+" is already defined.");
        }
    }

    //Write the appropriate jump to the MethodVisitor
    public MethodVisitor write(MethodVisitor mv) {

        return mv;
    }    
}
