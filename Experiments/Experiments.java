package Experiments;
import Data.Data;
import Entity.ColumnRankers;
import Entity.Entity;
import Experiments.Experiments;
import Greedy.Greedy;
import STV.STV;
import Similarity.SimilarityMeasures;
import Globals.Globals;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;


public class Experiments {
    static ArrayList<String> coordinates;
    static ArrayList<String> legend;
    static ArrayList<String> runtimes;
    static ArrayList<Float> sim1Results;
    static ArrayList<Float> propSimResults;
    // static booleancalcPropSim=false;
    public static void runTestAccidentsDifferentSize(){
        try {
            runtimes=new ArrayList<String>();
            sim1Results= new ArrayList<Float>();
            propSimResults= new ArrayList<Float>();
            int k=100;
            String dataFile="Datasets\\Accidents";
            int[] columns= {4,5};
            int[] types= {1,1};
            boolean hasGraph=false;
            Data.epsilon=(float) 0;
            STV.intervalSize=10;
            int[] numOfRecords={1000,10000,100000,1000000};
            System.out.println("STV k="+k+" with Multi");
            for(int n:numOfRecords){
                Data.loadRecordsFromCSVFile(dataFile, n, columns,types,1,hasGraph,true);
                Data.getRepsForData();
                ColumnRankers.initRankers();
                runSTV(k,false);
                
            }
            System.out.println("runtime:");
            for(int i=0;i<runtimes.size();i++){
                System.out.print("("+numOfRecords[i]+","+runtimes.get(i)+")");

            } 
            System.out.println();
            System.out.println("sim1:");
            for(int i=0;i<sim1Results.size();i++){
                System.out.print("("+numOfRecords[i]+","+sim1Results.get(i)+")");

            } 
            System.out.println();
            // System.out.println("propSim:");
            // for(int i=0;i<propSimResults.size();i++){
            //     System.out.print("("+numOfRecords[i]+","+propSimResults.get(i)+")");

            // } 
            // System.out.println();
            runtimes=new ArrayList<String>();
            sim1Results= new ArrayList<Float>();
            propSimResults= new ArrayList<Float>();
            System.out.println("STV k="+k+" without Multi");
            for(int n:numOfRecords){
                Data.loadRecordsFromCSVFile(dataFile, n, columns,types,6,hasGraph,true);
                Data.reps=null;
                ColumnRankers.initRankers();
                System.out.print(n+" : ");
                System.out.println(runSTV(k,false));
                
            }
            System.out.println("runtime:");
            for(int i=0;i<runtimes.size();i++){
                System.out.print("("+numOfRecords[i]+","+runtimes.get(i)+")");

            } 
            System.out.println();
            System.out.println("sim1:");
            for(int i=0;i<sim1Results.size();i++){
                System.out.print("("+numOfRecords[i]+","+sim1Results.get(i)+")");

            } 
            System.out.println();
            // System.out.println("propSim:");
            // for(int i=0;i<propSimResults.size();i++){
            //     System.out.print("("+numOfRecords[i]+","+propSimResults.get(i)+")");

            // } 
            // System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void runTestAccidentsDifferentK(){
        try {
            runtimes=new ArrayList<String>();
            sim1Results= new ArrayList<Float>();
            propSimResults= new ArrayList<Float>();
            boolean printStats=false;
            boolean printProgress=false;
            boolean printWinners=false;
            int[] k={100};
            String dataFile="Datasets\\Accidents";
            //int[] columns= {1,16};
            int[] columns= {4,5};
            int[] types= {1,1};
            boolean hasGraph=false;
            Data.epsilon=(float) 0;
            STV.intervalSize=10;
            int numOfRecords=3000000;
            Data.loadRecordsFromCSVFile(dataFile, numOfRecords, columns,types,2,hasGraph,true);
                if(!hasGraph)
                    Data.getRepsForData();
                ColumnRankers.initRankers();
            for(int k1:k){
                runSTV(k1,false);
                
            }
            System.out.println("runtime:");
            for(int i=0;i<runtimes.size();i++){
                System.out.print("("+k[i]+","+runtimes.get(i)+")");

            } 
            System.out.println();
            System.out.println("sim1:");
            for(int i=0;i<sim1Results.size();i++){
                System.out.print("("+k[i]+","+sim1Results.get(i)+")");

            } 
            // System.out.println();
            // System.out.println("propSim:");
            // for(int i=0;i<propSimResults.size();i++){
            //     System.out.print("("+k[i]+","+propSimResults.get(i)+")");

            // } 
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void runTestAccidentsDifferentRound(int k){
        try {
            System.out.println("STV k="+k);
            runtimes=new ArrayList<String>();
            sim1Results= new ArrayList<Float>();
            propSimResults= new ArrayList<Float>();
            boolean printStats=false;
            boolean printProgress=false;
            boolean printWinners=true;
            // int k=100;
            int [] round={0,1,2};
            String dataFile="Datasets\\Accidents";
            int[] columns= {4,5};
            int[] types= {1,1};
            boolean hasGraph=false;
            Data.epsilon=(float) 0;
            STV.intervalSize=10;
            int numOfRecords=3000000;
            // Data.loadRecordsFromCSVFile(dataFile, numOfRecords, columns,types,0,hasGraph,true);
            //     if(!hasGraph)
            //         Data.getRepsForData();
            //     ColumnRankers.initRankers();
            for(int i=0;i<round.length;i++){
                Data.loadRecordsFromCSVFile(dataFile, numOfRecords, columns,types,round[i],hasGraph,true);
                Data.getRepsForData();
                ColumnRankers.initRankers();
                runSTV( k,true);
                // System.out.print("runtime: ("+round[i]+","+runtimes.get(i)+")");
                // System.out.print(" sim1: ("+round[i]+","+sim1Results.get(i)+")");
                // System.out.println();
            }
            System.out.println();
            System.out.println("runtime:");
            for(int i=0;i<runtimes.size();i++){
                System.out.print("("+round[i]+","+runtimes.get(i)+")");

            } 
            System.out.println();
            System.out.println("sim1:");
            for(int i=0;i<sim1Results.size();i++){
                System.out.print("("+round[i]+","+sim1Results.get(i)+")");
            } 
            System.out.println();
            System.out.println("propSim:");
            for(int i=0;i<propSimResults.size();i++){
                System.out.print("("+round[i]+","+propSimResults.get(i)+")");

            } 
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void runTestDifferentDatasetsSTV(int k){
        try {
            runtimes=new ArrayList<String>();
            sim1Results= new ArrayList<Float>();
            propSimResults= new ArrayList<Float>();
            boolean printStats=false;
            boolean printProgress=false;
            boolean printWinners=false;
            // int k=1000;
            System.out.println("STV k="+k);
            // String[] dataFiles={"Datasets\\Accidents","Datasets\\Banks","Datasets\\Zomato"};
            int[] columns= {0,1,2};
            int[] types= {0,1,1};
            boolean hasGraph=false;
            Data.epsilon=(float) 0;
            STV.intervalSize=10;
            int numOfRecords=3000000;
            Data.loadRecordsFromCSVFile("Datasets\\Zomato1", numOfRecords, columns,types,2,hasGraph,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runSTV(k,false);
            System.out.println("runtime:");
            System.out.print("(zomato,"+runtimes.get(0)+")");
            

            columns= new int[]{0,1};
            types= new int[]{0,0};
            Data.loadRecordsFromCSVFile("Datasets\\Banks1", numOfRecords, columns,types,2,hasGraph,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runSTV(k,false);
            System.out.print("(banks,"+runtimes.get(1)+")");

            columns= new int[]{4,5};
            types= new int[]{1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,hasGraph,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runSTV (k,false);
            System.out.print("(accidents1,"+runtimes.get(2)+")");

            // columns= new int[]{1,16};
            // types= new int[]{1,1};
            // Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,hasGraph,true);
            // Data.getRepsForData();
            // ColumnRankers.initRankers();
            // runSTV (k,false);
            // System.out.println("(accidents2,"+runtimes.get(3)+")");
            
            System.out.println("sim1:");
            System.out.print("(zomato,"+sim1Results.get(0)+")");
            System.out.print("(banks,"+sim1Results.get(1)+")");
            System.out.print("(accidents1,"+sim1Results.get(2)+")");
            // System.out.println("(accidents2,"+sim1Results.get(3)+")");

           
            System.out.println("propSim:");
            // System.out.print("(zomato,"+propSimResults.get(0)+")");
            // System.out.print("(banks,"+propSimResults.get(1)+")");
            // System.out.print("(accidents1,"+propSimResults.get(2)+")");
            // System.out.println("(accidents2,"+propSimResults.get(3)+")");
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void runTestDifferentDatasetsTopK(int k){
        try {
            runtimes=new ArrayList<String>();
            sim1Results= new ArrayList<Float>();
            propSimResults= new ArrayList<Float>();
            boolean printStats=false;
            boolean printProgress=false;
            boolean printWinners=false;
            // int k=10;
            System.out.println("TopK k="+k);
            // String[] dataFiles={"Datasets\\Accidents","Datasets\\Banks","Datasets\\Zomato"};
            int[] columns= {0,1,2};
            int[] types= {0,1,1};
            boolean hasGraph=false;
            Data.epsilon=(float) 0;
            STV.intervalSize=10;
            int numOfRecords=3000000;
            Data.loadRecordsFromCSVFile("Datasets\\Zomato1", numOfRecords, columns,types,2,hasGraph,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runTopK(k,true);
            System.out.println("runtime:");
            System.out.print("(zomato,"+runtimes.get(0)+")");
            

            columns= new int[]{0,1};
            types= new int[]{0,0};
            Data.loadRecordsFromCSVFile("Datasets\\Banks1", numOfRecords, columns,types,2,hasGraph,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runTopK( k,true);
            System.out.print("(banks,"+runtimes.get(1)+")");

            columns= new int[]{4,5};
            types= new int[]{1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,hasGraph,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runTopK(k,true);
            System.out.print("(accidents1,"+runtimes.get(2)+")");

            // columns= new int[]{1,16};
            // types= new int[]{1,1};
            // Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,hasGraph,true);
            // Data.getRepsForData();
            // ColumnRankers.initRankers();
            // runTopK (k,true);
            // System.out.println("(accidents2,"+runtimes.get(3)+")");
            
            System.out.println("sim1:");
            System.out.print("(zomato,"+sim1Results.get(0)+")");
            System.out.print("(banks,"+sim1Results.get(1)+")");
            System.out.print("(accidents1,"+sim1Results.get(2)+")");
            // System.out.println("(accidents2,"+sim1Results.get(3)+")");

           
            System.out.println("propSim:");
            System.out.print("(zomato,"+propSimResults.get(0)+")");
            System.out.print("(banks,"+propSimResults.get(1)+")");
            System.out.println("(accidents1,"+propSimResults.get(2)+")");
            // System.out.println("(accidents2,"+propSimResults.get(3)+")");
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void runTestForthDatasetTopK(int k){
        try {
            runtimes=new ArrayList<String>();
            sim1Results= new ArrayList<Float>();
            propSimResults= new ArrayList<Float>();
            boolean printStats=false;
            boolean printProgress=false;
            boolean printWinners=false;
            // int k=10;
            // System.out.println("TopK k="+k);
            boolean hasGraph=false;
            Data.epsilon=(float) 0;
            STV.intervalSize=10;
            int numOfRecords=300000;
            int[] columns= new int[]{4,5};
            int[] types= new int[]{1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,hasGraph,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            System.out.println("TopK k="+k);
            runTopK(k,true);
            System.out.println("runtime: (accidents1,"+runtimes.get(0)+")");
            System.out.println("sim1: (accidents1,"+sim1Results.get(0)+")");
            System.out.println("propSim: (accidents1,"+propSimResults.get(0)+")");

            columns= new int[]{1,16};
            types= new int[]{1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,hasGraph,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runTopK(k,true);
            System.out.println("runtime: (accidents2,"+runtimes.get(1)+")");
            System.out.println("sim1: (accidents2,"+sim1Results.get(1)+")");
            System.out.println("propSim: (accidents2,"+propSimResults.get(1)+")");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runTestForthDatasetSTV(int k){
        try {
            runtimes=new ArrayList<String>();
            sim1Results= new ArrayList<Float>();
            propSimResults= new ArrayList<Float>();
            boolean printStats=false;
            boolean printProgress=false;
            boolean printWinners=false;
            // int k=10;
            // System.out.println("TopK k="+k);
            boolean hasGraph=false;
            Data.epsilon=(float) 0;
            STV.intervalSize=10;
            int numOfRecords=300000;
            int[] columns= new int[]{4,5};
            int[] types= new int[]{1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,hasGraph,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            System.out.println("STV k="+k);
            runSTV(k,true);
            System.out.println("runtime: (accidents1,"+runtimes.get(0)+")");
            System.out.println("sim1: (accidents1,"+sim1Results.get(0)+")");
            System.out.println("propSim: (accidents1,"+propSimResults.get(0)+")");

            columns= new int[]{1,16};
            types= new int[]{1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,hasGraph,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runSTV(k,true);
            System.out.println("runtime: (accidents2,"+runtimes.get(1)+")");
            System.out.println("sim1: (accidents2,"+sim1Results.get(1)+")");
            System.out.println("propSim: (accidents2,"+propSimResults.get(1)+")");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // public static void runTestDifferentDatasetsRandom(int k){
    //     try {
    //         runtimes=new ArrayList<String>();
    //         sim1Results= new ArrayList<Float>();
    //         propSimResults= new ArrayList<Float>();
    //         boolean printStats=false;
    //         boolean printProgress=false;
    //         boolean printWinners=false;
            
    //         System.out.println("Random k="+k);
    //         // String[] dataFiles={"Datasets\\Accidents","Datasets\\Banks","Datasets\\Zomato"};
    //         int[] columns= {0,1,2};
    //         int[] types= {0,1,1};
    //         boolean hasGraph=false;
    //         Data.epsilon=(float) 0;
    //         STV.intervalSize=10;
    //         int numOfRecords=3000000;
    //         Data.loadRecordsFromCSVFile("Datasets\\Zomato1", numOfRecords, columns,types,2,hasGraph,true);
    //         Data.getRepsForData();
    //         ColumnRankers.initRankers();
    //         runRandom(k,true);
    //         System.out.println("runtime:");
    //         System.out.print("(zomato,"+runtimes.get(0)+")");
            

    //         columns= new int[]{0,1};
    //         types= new int[]{0,0};
    //         Data.loadRecordsFromCSVFile("Datasets\\Banks1", numOfRecords, columns,types,2,hasGraph,true);
    //         Data.getRepsForData();
    //         ColumnRankers.initRankers();
    //         runRandom(k,true);
    //         System.out.print("(banks,"+runtimes.get(1)+")");

    //         columns= new int[]{4,5};
    //         types= new int[]{1,1};
    //         Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,hasGraph,true);
    //         Data.getRepsForData();
    //         ColumnRankers.initRankers();
    //         runRandom(k,true);
    //         System.out.println("(accidents,"+runtimes.get(2)+")");
            
    //         System.out.println("sim1:");
    //         System.out.print("(zomato,"+sim1Results.get(0)+")");
    //         System.out.print("(banks,"+sim1Results.get(1)+")");
    //         System.out.println("(accidents,"+sim1Results.get(2)+")");

           
    //         System.out.println("propSim:");
    //         System.out.print("(zomato,"+propSimResults.get(0)+")");
    //         System.out.print("(banks,"+propSimResults.get(1)+")");
    //         System.out.println("(accidents,"+propSimResults.get(2)+")");
            

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
    
    private static String runSTV(int k, boolean calcPropSim) {
        Globals.entities=null;
        STV.numOfCurrCan=0;

        Globals.start = System.currentTimeMillis();
        Entity[] votes=Data.getDynamicVotes();
        STV stv=new STV(votes, false,false);
        ArrayList<Integer> result = stv.runSTV(k);
        if(result==null){
            System.out.println("Timed Out");
            return null;
        }
        Globals.end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        runtimes.add(formatter.format((Globals.end - Globals.start) / 1000d));
        sim1Results.add(SimilarityMeasures.sim1(result));
        if(calcPropSim)
            propSimResults.add(SimilarityMeasures.propSim(result,10));
        return formatter.format((Globals.end - Globals.start) / 1000d);
    }
    private static void runTopK(int k, boolean calcPropSim) {
       
        Globals.start = System.currentTimeMillis();

        Greedy t=new Greedy();
        ArrayList<Integer> result =t.topK(k);
        if(result==null){
            System.out.println("Timed Out");
            return;
        }
        Globals.end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        runtimes.add(formatter.format((Globals.end - Globals.start) / 1000d));
        sim1Results.add(SimilarityMeasures.sim1(result));
        if(calcPropSim)
            propSimResults.add(SimilarityMeasures.propSim(result,10));
        
    }
    private static void runRandom(int k, boolean calcPropSim) {
       
        Globals.start = System.currentTimeMillis();

        ArrayList<Integer> result =new ArrayList<Integer>();
        int[] items=new int[Data.originalValues.size()];
        for(int i=0;i<items.length;i++){
            items[i]=i;
        }
        for(int i=0;i<k;i++){
            int rand=getRandomNumber(0,items.length-1-i);
            result.add(items[rand]);
            int last=items[items.length-1-i];
            items[rand]=last;
        }
        Globals.end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        runtimes.add(formatter.format((Globals.end - Globals.start) / 1000d));
        sim1Results.add(SimilarityMeasures.sim1(result));
        if(calcPropSim)
            propSimResults.add(SimilarityMeasures.propSim(result,10));
        
    }
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    
    
}
