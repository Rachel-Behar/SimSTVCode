package Data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import Entity.Item;

public class Data {
    public static List<float[]> values;//list of meta items without duplicate tuples. each item is an array of floats. Non numeric values are assigned with hash-codes.
    public static List<float[]> originalValues;//list of original tuples including duplicates 
    public static HashMap<Integer,ArrayList<Integer>> reps;//hash table for matching meta items with original items they represent by id.
    public static int [] columns;//similarity colums indexes 
    public static int [] types;//types of similarity inexes. 1 for numeric, 0 for boolean.
    private static List<HashMap<Integer,String>> hashCodesMap;//list of hash tables for boolean columns. hash table matched general values to hash-codes.
    /**
     * rounds x to the y decimal place
     * @param x
     * @param y
     * @return rounded x
     */
    static float roundOff(float x, int y)//
    {
        float a = x;
        double temp = Math.pow(10.0, y);
        a *= temp;
        a = Math.round(a);
        return (a / (float)temp);
    }
    /**
     * loads items from csv file
     * @param fileName - name of dataset file (including path)
     * @param numOfLines - number of items to read from file
     * @param columns - similarity columns
     * @param types - similarity columns types
     * @param round - number of digits to round
     * @param hasHeadLine - true if file as headline.
     * @throws IOException
     */
    public static void loadRecordsFromCSVFile(String fileName,int numOfLines,int[] columns, int [] types,int round,boolean hasHeadLine) throws IOException{
        Data.columns=columns;
        Data.types=types;
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
            if(types[i]==0){
                stringtoIntMap.add(i,new HashMap<String,Integer>());
                stringtoIntMap.add(i,new HashMap<String,Integer>());
                hashCodesMap.add(i,new HashMap<Integer,String>());
                hashCodes[i]=0;
            }
        }
        while ((line = br.readLine()) != null && numOfLines>0) {
            String[] dataFields = line.split(",");
            float[] v=new float[columns.length];
            float[] v1=new float[columns.length];
            for(int i=0;i<columns.length;i++){
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
            values.add(v);
            originalValues.add(v1);
            numOfLines--;
        }
        br.close();
    }
    /**
     * create meta items in values and save representatives lists in reps
     */
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
        Data.reps=reps;
    }
    /**
     * returns values of specific item in specific columns
     * @param recNum - id of item
     * @param col - column number
     * @return
     */
    public static float getValueByRecNumCol(int recNum, int col){
        return Data.values.get(recNum)[col];
    }
    /**
     * get item from values by id
     * @param recNum - id of item
     * @return
     */
    public static float[] getNumericRec(int recNum){
        return Data.values.get(recNum);
    }
    /**
     * get item as numeric tuple from originalValues by id (with hash-codes instead of boolean columns)
     * @param recNum - id of item
     * @return
     */
    public static float[] getOriginalNumericRec(int recNum){
        return Data.originalValues.get(recNum);
    }
    /**
     * get original item tuple from originalValues by id
     * @param recNum
     * @return
     */
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

    /**
     * number of imtens in values
     * @return
     */
    public static int size(){
        return values.size();
    }
    /**
     * get meta voters enteties
     */
    public static Item[] getDynamicVotes( ){
        Item[] votes=new Item[Data.size()];
        for(int can=0;can<Data.size();can++){
            int canRep=1;
            if(Data.reps!=null)
                canRep= Data.reps.get(can).size();
            votes[can]=new Item(can,canRep);
        }
        return votes;
    }  
}
