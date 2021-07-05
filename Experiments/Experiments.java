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
    static ArrayList<Float> bestRepResults;
    static ArrayList<Float> propRepResults;
    // static int numOfRecords=3000000;
    static int numOfRecords=3000;

    public static void runTestDifferentDatasets(){
        runTestDifferentDatasetsSTV(10,true);
        runTestDifferentDatasetsTopK(10,true);
        runTestDifferentDatasetsSTV(100,true);
        runTestDifferentDatasetsTopK(100,true);
        runTestDifferentDatasetsSTV(1000,false);
    }
    private static void runTestDifferentDatasetsSTV(int k, boolean clacPropMeasures){
        try {
            runtimes=new ArrayList<String>();
            bestRepResults= new ArrayList<Float>();
            propRepResults= new ArrayList<Float>();

            System.out.println("STV k="+k);

            int[] columns= {2,5,12};//columns number of columns: City, Avg. Cost, Rating 
            int[] types= {0,1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Zomato", numOfRecords, columns,types,2,false,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runSTV(k,clacPropMeasures);      
    
            columns= new int[]{0,5};//columns number of columns: Bank Name, State 
            types= new int[]{0,0};
            Data.loadRecordsFromCSVFile("Datasets\\Banks", numOfRecords, columns,types,2,false,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runSTV(k,clacPropMeasures);
            
            columns= new int[]{4,5};//columns number of columns: Latitude, Longitude
            types= new int[]{1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,false,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runSTV (k,clacPropMeasures);

            System.out.println("runtime:");
            System.out.print("(zomato,"+runtimes.get(0)+")");
            System.out.print("(banks,"+runtimes.get(1)+")");
            System.out.print("(accidents,"+runtimes.get(2)+")");
            System.out.println();
            if(clacPropMeasures){
                System.out.println("bestRep:");
                System.out.print("(zomato,"+bestRepResults.get(0)+")");
                System.out.print("(banks,"+bestRepResults.get(1)+")");
                System.out.print("(accidents,"+bestRepResults.get(2)+")");
                System.out.println();
                System.out.println("propRep:");
                System.out.print("(zomato,"+propRepResults.get(0)+")");
                System.out.print("(banks,"+propRepResults.get(1)+")");
                System.out.print("(accidents,"+propRepResults.get(2)+")");
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void runTestDifferentDatasetsTopK(int k, boolean clacPropMeasures){
        try {
            runtimes=new ArrayList<String>();
            bestRepResults= new ArrayList<Float>();
            propRepResults= new ArrayList<Float>();

            System.out.println("TopK k="+k);

            int[] columns= {2,5,12};//columns number of columns: City, Avg. Cost, Rating 
            int[] types= {0,1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Zomato", numOfRecords, columns,types,2,false,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runTopK(k,clacPropMeasures);
            

            columns= new int[]{0,5};//columns number of columns: Bank Name, State 
            types= new int[]{0,0};
            Data.loadRecordsFromCSVFile("Datasets\\Banks", numOfRecords, columns,types,2,false,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runTopK(k,clacPropMeasures);

            columns= new int[]{4,5};//columns number of columns: Latitude, Longitude
            types= new int[]{1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,false,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runTopK(k,clacPropMeasures);

            System.out.println("runtime:");
            System.out.print("(zomato,"+runtimes.get(0)+")");
            System.out.print("(banks,"+runtimes.get(1)+")");
            System.out.print("(accidents,"+runtimes.get(2)+")");
            System.out.println();
            if(clacPropMeasures){
                System.out.println("bestRep:");
                System.out.print("(zomato,"+bestRepResults.get(0)+")");
                System.out.print("(banks,"+bestRepResults.get(1)+")");
                System.out.print("(accidents,"+bestRepResults.get(2)+")");
                System.out.println();
                System.out.println("propRep:");
                System.out.print("(zomato,"+propRepResults.get(0)+")");
                System.out.print("(banks,"+propRepResults.get(1)+")");
                System.out.print("(accidents,"+propRepResults.get(2)+")");
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void runTestRuntimeDifferentSizeWithAndWithoutMeta(){
        try {
            
           
            int k=100;
            String dataFile="Datasets\\Accidents";
            int[] columns= {4,5};
            int[] types= {1,1};
            int[] numOfRecords={1000,10000,100000,1000000};

            System.out.println("STV k="+k+" with Meta");
            runtimes=new ArrayList<String>();
            for(int n:numOfRecords){
                Data.loadRecordsFromCSVFile(dataFile, n, columns,types,1,false,true);
                Data.getRepsForData();
                ColumnRankers.initRankers();
                runSTV(k,false);
                
            }
            System.out.println("runtime:");
            for(int i=0;i<runtimes.size();i++){
                System.out.print("("+numOfRecords[i]+","+runtimes.get(i)+")");

            } 
            System.out.println();
        
            runtimes=new ArrayList<String>();

            System.out.println("STV k="+k+" without Meta");
            runtimes=new ArrayList<String>();
            for(int n:numOfRecords){
                Data.loadRecordsFromCSVFile(dataFile, n, columns,types,6,false,true);
                Data.reps=null;
                ColumnRankers.initRankers();
                runSTV(k,false);
                
            }
            System.out.println("runtime:");
            for(int i=0;i<runtimes.size();i++){
                System.out.print("("+numOfRecords[i]+","+runtimes.get(i)+")");

            } 
            System.out.println();
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void runTestAccidentsDifferentRound(){
        runTestAccidentsDifferentRound(10);
        runTestAccidentsDifferentRound(100);
    }
    private static void runTestAccidentsDifferentRound(int k){
        try {
            System.out.println("STV k="+k);
            runtimes=new ArrayList<String>();
            bestRepResults= new ArrayList<Float>();
            propRepResults= new ArrayList<Float>();
            int [] round={0,1,2};
            String dataFile="Datasets\\Accidents";
            int[] columns= {4,5};
            int[] types= {1,1};
  
            for(int i=0;i<round.length;i++){
                Data.loadRecordsFromCSVFile(dataFile, numOfRecords, columns,types,round[i],false,true);
                Data.getRepsForData();
                ColumnRankers.initRankers();
                runSTV( k,true);
            }
            System.out.println();
            System.out.println("runtime:");
            for(int i=0;i<runtimes.size();i++){
                System.out.print("("+round[i]+","+runtimes.get(i)+")");

            } 
            System.out.println();
            System.out.println("bestRep:");
            for(int i=0;i<bestRepResults.size();i++){
                System.out.print("("+round[i]+","+bestRepResults.get(i)+")");
            } 
            System.out.println();
            System.out.println("propRep:");
            for(int i=0;i<propRepResults.size();i++){
                System.out.print("("+round[i]+","+propRepResults.get(i)+")");

            } 
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    private static void runSTV(int k, boolean calcRepMeasures) {
        Globals.entities=null;
        STV.numOfCurrCan=0;

        Globals.start = System.currentTimeMillis();
        Entity[] votes=Data.getDynamicVotes();
        STV stv=new STV(votes, false,false);
        ArrayList<Integer> result = stv.runSTV(k);
        if(result==null){
            runtimes.add("Timed Out");
            if(calcRepMeasures){
                bestRepResults.add(0f);
                propRepResults.add(0f);
            }
            return;
        }
        Globals.end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        runtimes.add(formatter.format((Globals.end - Globals.start) / 1000d));
        if(calcRepMeasures){
            bestRepResults.add(SimilarityMeasures.sim1(result));
            propRepResults.add(SimilarityMeasures.propSim(result,10));
        }
    }
    private static void runTopK(int k, boolean calcRepMeasures) {
       
        Globals.start = System.currentTimeMillis();

        Greedy t=new Greedy();
        ArrayList<Integer> result =t.topK(k);
        if(result==null){
            runtimes.add("Timed Out");
            if(calcRepMeasures){
                bestRepResults.add(0f);
                propRepResults.add(0f);
            }
            return;
        }
        Globals.end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        runtimes.add(formatter.format((Globals.end - Globals.start) / 1000d));
        if(calcRepMeasures){
            bestRepResults.add(SimilarityMeasures.sim1(result));
            propRepResults.add(SimilarityMeasures.propSim(result,10));
        }
        
    } 
}
