import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

// import Data.CreateVotes;
import Data.Data;
import Entity.ColumnRankers;
import Entity.Entity;
import Experiments.Experiments;
import Greedy.Greedy;
// import STV.MultiRepRegularSTV;
// import STV.RegularSTV;
// import STV.RegularSTV;
import STV.STV;
// import STV.SimilaritySTV;
// import STV.MultiRepSimilaritySTV;
import Similarity.SimilarityMeasures;
import Globals.Globals;

public class Main {
    public static void main(String[] args) {
        
        // Experiments.runTestAccidentsDifferentRound();
        // Experiments.runTestAccidentsDifferentSize();

        // Experiments.runTestDifferentDatasetsTopK(10);
        // Experiments.runTestDifferentDatasetsRandom(10);
        Experiments.runTestDifferentDatasetsSTV(10);

        // Experiments.runTestDifferentDatasetsTopK(100);
        // Experiments.runTestDifferentDatasetsRandom(100);
        Experiments.runTestDifferentDatasetsSTV(100);

        // Experiments.runTestDifferentDatasetsTopK(1000);
        // Experiments.runTestDifferentDatasetsRandom(1000);
        // Experiments.runTestDifferentDatasetsSTV(1000);

        // Experiments.runTestAccidentsDifferentK();
        // Experiments.runTestAccidentsDifferentRound(10);
        // Experiments.runTestAccidentsDifferentRound(100);

        // Experiments.runTestForthDatasetTopK(10);
        // Experiments.runTestForthDatasetTopK(100);
        // Experiments.runTestForthDatasetSTV(10);
        // Experiments.runTestForthDatasetSTV(100);


        // m1(); 
        // args=new String[]{"STV", "0", "100", "10000", "Datasets\\test1", "2", "4", "1", "5", "1"};
	    // m2(args); 

    }
    

   
    private static void m2(String[] args) {
        try {
            
            // boolean printStats=false;
            // boolean printProgress=true;
            // boolean printWinners=true;

            boolean printStats=false;
            boolean printProgress=false;
            // boolean printWinners=false;



            String alg=args[0];
            int round=Integer.parseInt(args[1]);
            int k=Integer.parseInt(args[2]);
            int numOfRecords=Integer.parseInt(args[3]);
            String dataFile=args[4];
            boolean hasGraph=false;
            if(args[5].equals("1")){
                hasGraph=true;
            }
            int numOfColumns=Integer.parseInt(args[6]);
            
            int[] columns= new int[numOfColumns];
            if(hasGraph){
                columns= new int[numOfColumns+1];
                columns[numOfColumns]=-1;
            }
            int[] types=new int[numOfColumns];
            for(int i=0,j=7;i<numOfColumns;i++,j++){
                columns[i]=Integer.parseInt(args[j]);
                j++;
                types[i]=Integer.parseInt(args[j]);
            }
            // Data.epsilon=(float) 0;
            // STV.intervalSize=10;
            
            Globals.start = System.currentTimeMillis();
            // print("round,k,prepTime,execTime,totTime,sim1,sim2,propSim,numCandidates");
            Data.loadRecordsFromCSVFile(dataFile, numOfRecords, columns,types,round,hasGraph,true);
            System.out.print(round+","+k+",");
            // runDymanic(start, printStats, printProgress, printWinners, k);
            
            // long start=0,middle=0,end=0;
            List<Integer> result=null;

            if(alg.equals("STV")){
                if(!hasGraph)
                    Data.getRepsForData();
                Entity[] votes=Data.getDynamicVotes();
                // Entity[] votes;
                // if(hasGraph)
                //     votes=CreateVotes.getDynamicVotes();
                // else
                //     votes=CreateVotes.getMultiRepDynamicVotes();
                Globals.middle= System.currentTimeMillis();
                STV stv;
                // if(hasGraph)
                //     stv=new RegularSTV(votes, printProgress,printStats);
                // else  
                    stv=new STV(votes, printProgress,printStats);
                result = stv.runSTV(k);
                
            // }else if(alg.equals("SimSTV")){
            //     Entity[] votes;
            //     if(hasGraph)
            //         votes=CreateVotes.getDynamicVotes();
            //     else
            //         votes=CreateVotes.getMultiRepDynamicVotes();
            //     Globals.middle= System.currentTimeMillis();
            //     STV stv;
            //     if(hasGraph)
            //         stv=new SimilaritySTV(votes, printProgress,printStats);
            //     else  
            //         stv=new MultiRepSimilaritySTV(votes, printProgress,printStats);
            //     result = stv.runSTV(k);
                
            }else if(alg.equals("TopK")){
               
                Greedy t=new Greedy();
                Globals.middle = System.currentTimeMillis();
                result =t.topK(k);
               
            }else if(alg.equals("GreedySim1")){
               
                Greedy t = new Greedy();
                Globals.middle = System.currentTimeMillis();
                result = t.greedySim1(k);
             
            }
            if(result==null){
                System.out.println("Timed Out");
                return;
            }
            Globals.end = System.currentTimeMillis();
            NumberFormat formatter = new DecimalFormat("#0.00000");
            System.out.print(formatter.format((Globals.middle - Globals.start) / 1000d)+",");
            System.out.print(formatter.format((Globals.end - Globals.middle) / 1000d)+",");
            System.out.print(formatter.format((Globals.end - Globals.start) / 1000d)+",");

            System.out.print(SimilarityMeasures.sim1(result)+","+SimilarityMeasures.sim2(result)+","+SimilarityMeasures.propSim(result,10));
            System.out.print(","+Data.size());
            System.out.println();

        } catch (IOException e) {
            System.out.println("usage: algorithm, round, k, numOfRecords, dataFile, numOfColumns, [column,type]*numOfColumns");
            e.printStackTrace();
        }
    }

    private static void m1() {
        try {
            
            // boolean printStats=false;
            // boolean printProgress=true;
            // boolean printWinners=true;

            boolean printStats=true;
            // boolean printProgress=true;
            boolean printProgress=false;
            boolean printWinners=false;
            // boolean printWinners=true;
            NumberFormat formatter = new DecimalFormat("#0.00000");
           
            int numOfRecords=100;
            int k=10;

            String dataFile="Datasets\\Accidents";
            // int[] columns= {4,5};
            // int[] types= {1,1};
            int[] columns= {4,5};
            int[] types= {1,1};
            boolean hasGraph=false;

            // String dataFile="Datasets\\Banks";
            // int[] columns= {4,5};
            // int[] types= {1,1};
            // boolean hasGraph=false;

            // String dataFile="Datasets\\Zomato";
            // int[] columns= {3,4};
            // int[] types= {1,1};
            // boolean hasGraph=false;

            // String dataFile="Datasets\\dblp_small";
            // // int[] columns= {-1};
            // // int[] types= {};
            // int[] columns= {1,2,-1};
            // int[] types= {1,1};
            // boolean hasGraph=true;

            // int numOfRecords=10000;
            // int k=3;
            // String dataFile="Datasets\\test1";
            // int[] columns= {2,3};
            // int[] types= {1,1};
            // boolean hasGraph=false;

            // String dataFile="Datasets\\dblp_small";
            // int[] columns= {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,-1};
            // int[] types= {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
            // boolean hasGraph=true;

            Data.epsilon=(float) 0;
            STV.intervalSize=10;
            
            
            // Globals.start = System.currentTimeMillis();
            // Data.loadRecordsFromCSVFile(dataFile, numOfRecords, columns,types,0,hasGraph,true);
            // Globals.end = System.currentTimeMillis();
            // System.out.println("Data Load time is " + formatter.format((Globals.end - Globals.start) / 1000d) + " seconds");
            // if(!hasGraph)
            //     Data.getRepsForData();
            // ColumnRankers.initRankers();

            // // runTopK(k,printWinners);
            // // runGreedy(k,printWinners);
            // runSTV(printStats, printProgress, printWinners, k,hasGraph);
            // // runSTV(printStats, printProgress, printWinners, k,hasGraph);
            // // runSimSTV(printStats, printProgress, printWinners, k,hasGraph);

            Globals.start = System.currentTimeMillis();
            Data.loadRecordsFromCSVFile(dataFile, numOfRecords*10, columns,types,0,hasGraph,true);
            Globals.end = System.currentTimeMillis();
            System.out.println("Data Load time is " + formatter.format((Globals.end - Globals.start) / 1000d) + " seconds");
            if(!hasGraph)
                Data.getRepsForData();
            ColumnRankers.initRankers();

            // runTopK(k,printWi nners);
            // runGreedy(k,printWinners);
            runSTV(printStats, printProgress, printWinners, k,hasGraph);
            // runSTV(printStats, printProgress, printWinners, k,hasGraph);
            // runSimSTV(printStats, printProgress, printWinners, k,hasGraph);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runTopK(int k, boolean printWinners) {
        
        System.out.println("--------------------------------");
        System.out.println("Running topK");
        System.out.println("--------------------------------");
        Globals.start = System.currentTimeMillis();

        Greedy t=new Greedy();
        List<Integer> result =t.topK(k);
        if(result==null){
            System.out.println("Timed Out");
            return;
        }
        Globals.end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.println("Execution time is " + formatter.format((Globals.end - Globals.start) / 1000d) + " seconds");

        System.out.println("sim1="+SimilarityMeasures.sim1(result));
        System.out.println("sim2="+SimilarityMeasures.sim2(result));
        System.out.println("propSim="+SimilarityMeasures.propSim(result,10));
        if(printWinners){
            for(int c :result){
                System.out.print(c + ":"+Data.getOriginalRec(c));
                // for(int col = 0 ; col < Data.columns.length; col++){
                //     System.out.print(" "+Data.getOriginalNumericRec(c)[col]);
                // }
                System.out.println();
            }
        }
        
        
    }

    private static void runGreedy(int k, boolean printWinners) {
        
        System.out.println("--------------------------------");
        System.out.println("Running GreedySim1");
        System.out.println("--------------------------------");
        Globals.start = System.currentTimeMillis();

        Greedy t=new Greedy();
        List<Integer> result =t.greedySim1(k);
        if(result==null){
            System.out.println("Timed Out");
            return;
        }
        Globals.end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.println("Execution time is " + formatter.format((Globals.end - Globals.start) / 1000d) + " seconds");
        
        System.out.println("sim1="+SimilarityMeasures.sim1(result));
        System.out.println("sim2="+SimilarityMeasures.sim2(result));
        System.out.println("propSim="+SimilarityMeasures.propSim(result,10));
        if(printWinners){
            for(int c :result){
                System.out.print(c + ":"+Data.getOriginalRec(c));
                // for(int col = 0 ; col < Data.columns.length; col++){
                //     System.out.print(" "+Data.getOriginalNumericRec(c)[col]);
                // }
                System.out.println();
            }
        }
    }

    
    private static void runSTV(boolean printStats, boolean printProgress, boolean printWinners, int k,boolean hasGraph) {
        Globals.entities=null;
        STV.numOfCurrCan=0;
        System.out.println("--------------------------------");
        System.out.println("Running STV");
        System.out.println("--------------------------------");

        Globals.start = System.currentTimeMillis();
        Entity[] votes=Data.getDynamicVotes();
        Globals.end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.println("Preperation time is " + formatter.format((Globals.end - Globals.start) / 1000d) + " seconds");
        
        System.out.println("Accutal num Of candidates: "+Data.size());
        // start = System.currentTimeMillis();
        STV stv;
        stv=new STV(votes, printProgress,printStats);
        List<Integer> result = stv.runSTV(k);
        if(result==null){
            System.out.println("Timed Out");
            return;
        }
        Globals.end = System.currentTimeMillis();
        formatter = new DecimalFormat("#0.00000");
        System.out.println("Execution time is " + formatter.format((Globals.end - Globals.start) / 1000d) + " seconds");
        if(printWinners){
            for(int c :result){
                System.out.print(c + ":"+Data.getOriginalRec(c));
                // for(int col = 0 ; col < Data.columns.length; col++){
                //     System.out.print(" "+Data.getOriginalNumericRec(c)[col]);
                // }
                System.out.println();
            }
        }
        System.out.println("sim1="+SimilarityMeasures.sim1(result));
        System.out.println("sim2="+SimilarityMeasures.sim2(result));
        System.out.println("propSim="+SimilarityMeasures.propSim(result,1));
        // if(printWinners){
        //     for(int c :result){
        //         System.out.print(c + ":"+Data.getOriginalRec(c));
        //         // for(int col = 0 ; col < Data.columns.length; col++){
        //         //     System.out.print(" "+Data.getOriginalNumericRec(c)[col]);
        //         // }
        //         System.out.println();
        //     }
        // }
    }
    // private static void runSimSTV(boolean printStats, boolean printProgress, boolean printWinners, int k,boolean hasGraph) {
    //     System.out.println("--------------------------------");
    //     System.out.println("Running Similarity STV");
    //     System.out.println("--------------------------------");

    //     Globals.start = System.currentTimeMillis();
    //     Entity[] votes;
    //     if(hasGraph){
    //         votes=CreateVotes.getDynamicVotes();
    //     }else
    //         votes=CreateVotes.getMultiRepDynamicVotes();

    //     // long end = System.currentTimeMillis();
    //     NumberFormat formatter = new DecimalFormat("#0.00000");
    //     // System.out.println("Preperation time is " + formatter.format((end - start) / 1000d) + " seconds");
        
    //     System.out.println("Accutal num Of candidates: "+Data.size());
    //     // start = System.currentTimeMillis();
    //     STV stv;
    //     if(hasGraph){
    //         stv=new SimilaritySTV(votes, printProgress,printStats);
    //     }
    //     else
    //         stv=new MultiRepSimilaritySTV(votes, printProgress,printStats);
    //     List<Integer> result = stv.runSTV(k);
    //     if(result==null){
    //         System.out.println("Timed Out");
    //         return;
    //     }
    //     Globals.end = System.currentTimeMillis();
    //     formatter = new DecimalFormat("#0.00000");
    //     System.out.println("Execution time is " + formatter.format((Globals.end - Globals.start) / 1000d) + " seconds");
        
    //     System.out.println("sim1="+SimilarityMeasures.sim1(result));
    //     System.out.println("sim2="+SimilarityMeasures.sim2(result));
    //     System.out.println("propSim="+SimilarityMeasures.propSim(result,10));
    //     if(printWinners){
    //         for(int c :result){
    //             System.out.print(c + ":"+Data.getOriginalRec(c));
    //             // for(int col = 0 ; col < Data.columns.length; col++){
    //             //     System.out.print(" "+Data.getOriginalNumericRec(c)[col]);
    //             // }
    //             System.out.println();
    //         }
    //     }
    // }
    static void print(String s){
        System.out.println(s);
    }
    // private static void runDymanic(long start, boolean printStats, boolean printProgress, boolean printWinners, int k) {
    //     // Entity[] votes=CreateRestVotes.getMultiRepDynamicVotes();
    //     Entity[] votes=CreateRestVotes.getDynamicVotes();
    //     // Entity[] votes=CreateRestVotes.getVotes();
        
    //     // for(Vote v:votes){
    //     //     print(v.c+":");
    //     //     while(v.top()!=null){
    //     //         print(v.top().toString());
    //     //         v.pop();
    //     //     }
    //     // }


    //     long end = System.currentTimeMillis();
    //     NumberFormat formatter = new DecimalFormat("#0.00000");
    //     System.out.println("Preperation time is " + formatter.format((end - start) / 1000d) + " seconds");
        
        
    //     start = System.currentTimeMillis();

    //     STV stv=new RegularSTV(votes, printProgress,printStats);
    //     List<Integer> result = stv.runSTV(k);

    //     // STV stv=new MultiRepSTV(votes, printProgress,printStats);
    //     // List<Integer> result = stv.runMultiSTV(k);

    //     end = System.currentTimeMillis();
    //     formatter = new DecimalFormat("#0.00000");
    //     System.out.println("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");
        
    //     System.out.println("sim1="+SimilarityMeasures.sim1(result));
    //     System.out.println("sim2="+SimilarityMeasures.sim2(result));
    //     // System.out.println("propSim="+SimilarityMeasures.propSim(result,votes,100));
    //     if(printWinners){
    //         for(int c :result){
    //             System.out.print(c + ":");
    //             for(int col = 0 ; col < Data.columns.length; col++){
    //                 System.out.print(" "+Data.getOriginalNumericRec(c)[col]);
    //             }
    //             System.out.println();
    //         }
    //     }
    // }
}
