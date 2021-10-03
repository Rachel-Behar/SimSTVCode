
import java.io.IOException;
import java.util.ArrayList;

import Basics.KMeans.KMeans;
import Data.Data;
import Item.ColumnRankers;
import Experiments.Experiments;

public class Main {
    public static void main(String[] args) {

        
        Experiments.runKMeans(3, true);
        // Experiments.runTestDifferentDatasets();
        // Experiments.runTestRuntimeDifferentSizeWithAndWithoutMeta();
        // Experiments.runTestAccidentsDifferentRound();
    }
}
