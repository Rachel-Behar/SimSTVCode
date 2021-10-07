package Experiments;
import Data.Data;
import Experiments.Experiments;
import Item.ColumnRankers;
import Item.Item;
import STV.SimSTV;
import Similarity.SimilarityMeasures;
import Globals.Globals;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import Basics.TopK;
import Basics.KMeans.KMeans;


public class Experiments {
    /**
     *
     */
    private static final int propIterations = 10;
    // static ArrayList<String> coordinates;
    static ArrayList<String> legend;
    static ArrayList<String> runtimes;
    static ArrayList<Float> bestRepResults;
    static ArrayList<Float> propRepResults;
    static int numOfRecords=3000000;

    public static void runTestDifferentDatasets(){
        runTestDifferentDatasetsSTV(10,true);
        runTestDifferentDatasetsTopK(10,true);
        runTestDifferentDatasetsKMeans(10);
        runTestDifferentDatasetsSTV(100,true);
        runTestDifferentDatasetsTopK(100,true);
        runTestDifferentDatasetsKMeans(100);
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
            Data.loadRecordsFromCSVFile("Datasets\\Zomato", numOfRecords, columns,types,2,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runSTV(k,clacPropMeasures);      
    
            columns= new int[]{0,5};//columns number of columns: Bank Name, State 
            types= new int[]{0,0};
            Data.loadRecordsFromCSVFile("Datasets\\Banks", numOfRecords, columns,types,2,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runSTV(k,clacPropMeasures);
            
            columns= new int[]{4,5};//columns number of columns: Latitude, Longitude
            types= new int[]{1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,true);
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
            Data.loadRecordsFromCSVFile("Datasets\\Zomato", numOfRecords, columns,types,2,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runTopK(k,clacPropMeasures);
            

            columns= new int[]{0,5};//columns number of columns: Bank Name, State 
            types= new int[]{0,0};
            Data.loadRecordsFromCSVFile("Datasets\\Banks", numOfRecords, columns,types,2,true);
            Data.getRepsForData();
            ColumnRankers.initRankers();
            runTopK(k,clacPropMeasures);

            columns= new int[]{4,5};//columns number of columns: Latitude, Longitude
            types= new int[]{1,1};
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,true);
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
                Data.loadRecordsFromCSVFile(dataFile, n, columns,types,1,true);
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
                Data.loadRecordsFromCSVFile(dataFile, n, columns,types,6,true);
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
                Data.loadRecordsFromCSVFile(dataFile, numOfRecords, columns,types,round[i],true);
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
        Globals.items=null;
        Globals.start = System.currentTimeMillis();
        Item[] votes=Data.getDynamicVotes();
        SimSTV stv=new SimSTV(votes);
        ArrayList<Integer> result = stv.runSimSTV(k);
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
            bestRepResults.add(SimilarityMeasures.bestRep(result));
            propRepResults.add(SimilarityMeasures.propRep(result,10));
        }
    }
    public static void runTestDifferentDatasetsKMeans(int k) {
        try {
            System.out.println("k-Means k="+k);
            int numOfRecords=3000000;
            bestRepResults= new ArrayList<Float>();
            propRepResults= new ArrayList<Float>();
            KMeans kmeans = new KMeans();

            int[] columns= new int[141];//number of columns in preproccessed file
            int[] types= new int[141];
            for(int i=0;i<141;i++){
                columns[i]=i;
                types[i]=1;
            }
            //file created by 'preproc.py'
            Data.loadRecordsFromCSVFile("Datasets\\zomato_for_kmeans", numOfRecords, columns,types,1,true);

            ArrayList<Integer> result1=kmeans.kMeans(k);
            ArrayList<Integer> result2=kmeans.kMeans(k);
            ArrayList<Integer> result3=kmeans.kMeans(k);
            columns= new int[]{2,5,12};//columns number of columns: City, Avg. Cost, Rating 
            types= new int[]{0,1,1};
            Data.loadRecordsFromCSVFile("Datasets\\zomato", numOfRecords, columns,types,2,true);
            bestRepResults.add((SimilarityMeasures.bestRep(result1)+SimilarityMeasures.bestRep(result2)+SimilarityMeasures.bestRep(result3))/3);
            propRepResults.add((SimilarityMeasures.propRep(result1,propIterations)+SimilarityMeasures.propRep(result2,propIterations)+SimilarityMeasures.propRep(result3,propIterations))/3);
            

            columns= new int[1415];//number of columns in preproccessed file
            types= new int[1415];
            for(int i=0;i<1415;i++){
                columns[i]=i;
                types[i]=1;
            }
            //file created by 'preproc.py'
            Data.loadRecordsFromCSVFile("Datasets\\Banks_for_kmeans", numOfRecords, columns,types,1,true);
            result1=kmeans.kMeans(k);
            result2=kmeans.kMeans(k);
            result3=kmeans.kMeans(k);

            columns= new int[]{0,5};//columns number of columns: Bank Name, State 
            types= new int[]{0,0};
            Data.loadRecordsFromCSVFile("Datasets\\Banks", numOfRecords, columns,types,2,true);
            bestRepResults.add((SimilarityMeasures.bestRep(result1)+SimilarityMeasures.bestRep(result2)+SimilarityMeasures.bestRep(result3))/3);
            propRepResults.add((SimilarityMeasures.propRep(result1,propIterations)+SimilarityMeasures.propRep(result2,propIterations)+SimilarityMeasures.propRep(result3,propIterations))/3);

            columns= new int[]{4,5};//columns number of columns: Latitude, Longitude
            types= new int[]{1,1};
            
            Data.loadRecordsFromCSVFile("Datasets\\Accidents", numOfRecords, columns,types,1,true);
            Globals.start = System.currentTimeMillis();
            kmeans = new KMeans();
            result1=kmeans.kMeans(k);
            result2=kmeans.kMeans(k);
            result3=kmeans.kMeans(k);
            Globals.end = System.currentTimeMillis();
            bestRepResults.add((SimilarityMeasures.bestRep(result1)+SimilarityMeasures.bestRep(result2)+SimilarityMeasures.bestRep(result3))/3);
            propRepResults.add((SimilarityMeasures.propRep(result1,propIterations)+SimilarityMeasures.propRep(result2,propIterations)+SimilarityMeasures.propRep(result3,propIterations))/3);


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
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private static void runTopK(int k, boolean calcRepMeasures) {
       
        Globals.start = System.currentTimeMillis();

        TopK t=new TopK();
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
            bestRepResults.add(SimilarityMeasures.bestRep(result));
            propRepResults.add(SimilarityMeasures.propRep(result,10));
        }
        
    } 
}
