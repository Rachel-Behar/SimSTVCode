package Data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Entity.Entity;
import Similarity.SimilarityMeasures;

public class Data {
    // private static List<String[]> records;
    public static List<float[]> values;
    public static List<float[]> originalValues;
    public static HashMap<Integer,ArrayList<Integer>> reps;
    public static int [] columns;
    public static int [] types;
    public static float epsilon;
    private static List<HashMap<Integer,String>> hashCodesMap;
    public static Graph g=null;
    static float roundOff(float x, int position)
    {
        float a = x;
        double temp = Math.pow(10.0, position);
        a *= temp;
        a = Math.round(a);
        return (a / (float)temp);
    }
    public static void loadRecordsFromCSVFile(String fileName,int numOfLines,int[] columns, int [] types,int round)throws IOException{
        loadRecordsFromCSVFile(fileName,numOfLines,columns,types,round,false,false);
    }
    
    public static void loadRecordsFromCSVFile(String fileName,int numOfLines,int[] columns, int [] types,int round,boolean hasGraph,boolean hasHeadLine) throws IOException{
        Data.columns=columns;
        Data.types=types;
        // records = new ArrayList<>();
        values= new ArrayList<>();
        originalValues=new ArrayList<>();
        hashCodesMap=new ArrayList<>(columns.length);
        List<HashMap<String,Integer>> stringtoIntMap=new ArrayList<>(columns.length);
        BufferedReader br = new BufferedReader(new FileReader(fileName+".csv"));
        String line="";
        if(hasHeadLine)
            line= br.readLine();
        int[] hashCodes=new int[columns.length];
        for(int i=0;i<columns.length;i++){
            if(columns[i]!=-1){
                if(types[i]==0){
                    stringtoIntMap.add(i,new HashMap<String,Integer>());
                    stringtoIntMap.add(i,new HashMap<String,Integer>());
                    hashCodesMap.add(i,new HashMap<Integer,String>());
                    hashCodes[i]=0;
                }
            }
        }
        while ((line = br.readLine()) != null && numOfLines>0) {
            // String[] dataFields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            String[] dataFields = line.split(",");
            // records.add(dataFields);
            float[] v=new float[columns.length];
            float[] v1=new float[columns.length];
            for(int i=0;i<columns.length;i++){
                if(columns[i]!=-1){
                    if(types[i]==1){
                        String value=dataFields[columns[i]].replaceAll("-", "");
                        if(value.equals("nan")){
                            v[i]=0;
                            v1[i]=0;
                        }else{
                            v[i]=roundOff(Float.parseFloat(value),round);
                            v1[i]=Float.parseFloat(value);
                            if (v[i] == Float.POSITIVE_INFINITY) {
                                v[i] = 0;
                                v1[i] = 0;
                            }
                        }
                        // v[i]=roundOff(Float.parseFloat(dataFields[columns[i]]),round);
                        // v1[i]=Float.parseFloat(dataFields[columns[i]]);
                    }else{
                        String value=dataFields[columns[i]];
                        if(stringtoIntMap.get(i).containsKey(value)){
                            v[i]=stringtoIntMap.get(i).get(value);
                        }
                        else{
                            stringtoIntMap.get(i).put(value, hashCodes[i]);
                            hashCodesMap.get(i).put(hashCodes[i],value);
                            v[i]=hashCodes[i];
                            hashCodes[i]++;
                        }
                        v1[i]=v[i];
                    }
                }
            }
            // System.out.println(v[0]+","+v[1]);
            values.add(v);
            originalValues.add(v1);
            // System.out.println(values[4]);
            numOfLines--;
        }
        br.close();
        if(hasGraph){
            g=new Graph(fileName+".g");
        }
        // for(float[] v :values){
        //     System.out.println(v[0]);
        // }
    //    originalValues=values;
    }
    public static void getRepsForData(){
        
        HashMap<Tuple,Integer> tuples=new HashMap<>();
        HashMap<Integer,ArrayList<Integer>> reps=new HashMap<>();
        List<float[]> tempValues=new ArrayList<>();;
        int numVotesInPractice=0;
        for(int can=0;can<Data.size();can++){

            float[] rec=Data.getNumericRec(can);
            Tuple recT=new Tuple(rec);
            if(tuples.containsKey(recT)){
                int rep=tuples.get(recT);
                reps.get(rep).add(can);
            }else{
                tuples.put(recT, numVotesInPractice);
                tempValues.add(rec);
                reps.put(numVotesInPractice, new ArrayList<>());
                reps.get(numVotesInPractice).add(can);
                numVotesInPractice++;
            }
            
        }
        Data.values=tempValues;
        // System.out.println("Actual num Of candidates: "+Data.size());
        Data.reps=reps;
        //SimilarityMeasures.createSimTable(Data.size());
    }
    // public static float getValue(int recNum, int col){
    //     return Float.parseFloat(Data.records.get(recNum)[col]);
    // }
    public static float getValueByRecNumCol(int recNum, int col){
        return Data.values.get(recNum)[col];
    }
    // public static int getValueByRecNumColBool(int recNum, int col){
    //     return (int) Data.values.get(recNum)[col];
    // }
    // public static float[] getColValues(int col){
    //     float[] columnVals=new float[values.size()];
    //     for(int i=0;i<columnVals.length;i++){
    //         columnVals[i]=Data.values.get(i)[col];
    //     }
    //     return columnVals;
    // }
    public static float[] getNumericRec(int recNum){
        return Data.values.get(recNum);
    }

    public static float[] getOriginalNumericRec(int recNum){
        return Data.originalValues.get(recNum);
    }
    public static List<Object> getOriginalRec(int recNum){
        float[] rec =Data.originalValues.get(recNum);
        List<Object> originalRecord =new ArrayList<Object>();
        for(int i=0;i<columns.length;i++){
            if(columns[i]!=-1){
                if(types[i]==1)
                    originalRecord.add(rec[i]);
                else{
                    originalRecord.add(hashCodesMap.get(i).get((int)rec[i]));
                }
            }
        }
        return originalRecord;
    }

    // public static String[] getRecord(int recNum){
    //     return Data.records.get(recNum);
    // }
    public static int size(){
        return values.size();
    }
    public static Entity[] getDynamicVotes( ){
        // initRankers();
        Entity[] votes=new Entity[Data.size()];
        // Entity[] votes=new DynamicVoteEntity[Data.size()];
        for(int can=0;can<Data.size();can++){
            // votes[can]=new DynamicVoteEntity(can);
            int canRep=1;
            if(Data.reps!=null)
                canRep= Data.reps.get(can).size();
            votes[can]=new Entity(can,canRep);
            // topCount+=votes[can].topVote().size();
        }
        return votes;
    }  
}
