package Similarity;

import java.util.*;
import Data.Data;
/**
 * class for all similarity caculations
 */
public class SimilarityMeasures{
    /**
     * calculates similarty between two items represented as float arrays of the similarit columns values
     * @param c1 - first item
     * @param c2 - second item
     * @return
     */
     public static float sim(float[] c1, float[] c2) {
        float result=0;
        for (int i=0;i<c1.length;i++){
            if(Data.types[i]==0){//for boolean columns
                if(c1[i] ==c2[i]) {
                    result += 1;
                }
            }else{//for numeric columns
                if (c1[i] < c2[i]) {
                    result += 1 / (1 + c2[i] - c1[i]);
                }
                else if (c2[i] < c1[i]){
                    result += 1 / (1 + c1[i] - c2[i]);
                }
                else result += 1;
            }
        }
        return result;
    }
    /**
     * calculates similarit between tow muneric values based on distance
     * @param n1 - first number
     * @param n2 - second number
     * @return
     */
    public static float numericSim(float n1,float n2){
        return 1/(1+Math.abs(n1-n2));
    }
    /**
     * return similarity of two items by serial id
     * @param rec1 - first item id
     * @param rec2 - second item id
     * @return
     */
    public static float simByRec(int rec1,int rec2){
        float result = (sim(Data.getNumericRec(rec1), Data.getNumericRec(rec2)))/Data.columns.length;
        return result;
    }
    /**
     * return similarity of two items by thies original values
     * @param rec1 - first item id
     * @param rec2 - second item id
     * @return
     */
    public static float simByOriginalRec(int rec1,int rec2){
        return (sim(Data.getOriginalNumericRec(rec1), Data.getOriginalNumericRec(rec2)))/Data.columns.length;
    }
    /**
     * caculates the bestRep measure of the winners given
     * @param winners - list of winners id
     * @return
     */
    public static float bestRep(List<Integer> winners) {
        float result=0;
        for(int i=0;i<Data.originalValues.size();i++){
            float max=0;
            for(int w:winners){
                float curr=simByOriginalRec(i,w);
                if(curr>max){
                    max=curr;
                }
            }
            result+=max;
        }
        return result/Data.originalValues.size();
    }
    /**
     * caculates the propRep measure of the winners given
     * @param winners - list of winners id
     * @param numRand - number of rounds to run the calculation
     * @return
     */
    public static float propRep(List<Integer> winners, int numRand){
        float bestReault=0;
        int gSize=(int)Math.floor(Data.originalValues.size()/winners.size());
        int extra = Data.originalValues.size()-gSize*winners.size();
        ArrayList<Integer> candidates;
        for (int i=0;i<numRand;i++){
            candidates=new ArrayList<Integer>();
            for(int can=0;can<Data.originalValues.size();can++)
                candidates.add(can);
            float simSum=0;
            Collections.shuffle(winners);
            for (int w:winners){
                int currGroupSize;
                if (extra == 0)
                    currGroupSize=gSize;
                else{
                    currGroupSize=gSize+1;
                    extra--;
                }
                int wGroupSize=0;
                candidates.sort(new Comparator<Integer>(){
                public int compare(Integer c1, Integer c2) {
                    float sim1= simByOriginalRec(c1, w);
                    float sim2= simByOriginalRec(c2, w);
                    return Float.valueOf(sim1).compareTo(sim2);
                }});
                while(wGroupSize<currGroupSize){
                    int can=candidates.remove(candidates.size()-1);
                    simSum +=simByOriginalRec(w, can);
                    wGroupSize++;
                }
            }
            if(simSum>bestReault){
                bestReault=simSum;
            }
        }
        return bestReault/(Data.originalValues.size());
    }
}