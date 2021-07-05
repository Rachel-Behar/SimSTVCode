
import Experiments.Experiments;

public class Main {
    public static void main(String[] args) {
        
        Experiments.runTestDifferentDatasets();
        Experiments.runTestRuntimeDifferentSizeWithAndWithoutMeta();
        Experiments.runTestAccidentsDifferentRound();
    }
}
