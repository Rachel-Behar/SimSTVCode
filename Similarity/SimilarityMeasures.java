package Similarity;
import java.util.*;

import Data.Data;
import Entity.Entity;

public class SimilarityMeasures{
    static int called = 0;
    //static Hashtable<Integer,Float>[] simTable;

//    public static void createSimTable(int size) {
//
//
//        simTable = new Hashtable[size];
//        for (int i = 0 ; i < size ; i++) simTable[i] = new Hashtable<>();
//    }

     static float sim(float[] c1, float[] c2) {
         called++;
         //if (called % 100000 == 0)
         //    System.out.println("sim " + called);
        float result=0;
        for (int i=0;i<c1.length;i++){
            if(Data.types[i]==0){
                if(c1[i] ==c2[i]) {
                    result += 1;
                }
            }else{
                if (c1[i] < c2[i]) {
                    result += 1 / (1 + c2[i] - c1[i]);
                    if (result == Float.POSITIVE_INFINITY) System.out.println(c1[i] + " " + c2[i] + " " + (c1[i]  - c2[i]));
                }
                else if (c2[i] < c1[i]){
                    result += 1 / (1 + c1[i] - c2[i]);
                    if (result == Float.POSITIVE_INFINITY) System.out.println(c1[i] + " " + c2[i] + " " + (c1[i]  - c2[i]));
                }
                else result += 1;
            }
        }
        return result;
    }

    // public static float numericSim(float[] n1,float[] n2){
    //     //called++;
    //     //if (called % 10000000 == 0) System.out.println(called);
    //     float result=0;
    //     for(int i=0;i<n1.length;i++){
    //         result+=numericSim(n1[i],n2[i]);
    //     }
    //     return result;
    // }

    public static float simByRec(int rec1,int rec2){
//        if (rec1 > rec2) {
//
//            int t = rec1;
//            rec1 = rec2;
//            rec2 = t;
//        }
//        Float sim = simTable[rec1].get(rec2);
//        if (sim != null) return sim;

        float result = (sim(Data.getNumericRec(rec1), Data.getNumericRec(rec2)))/Data.columns.length;
        //simTable[rec1].put(rec2, result);
        return result;
    }

    public static float simByOriginalRec(int rec1,int rec2){
        return (sim(Data.getOriginalNumericRec(rec1), Data.getOriginalNumericRec(rec2)))/Data.columns.length;
    }

    public static float numericSim(float n1,float n2){
        return 1/(1+Math.abs(n1-n2));
    }

    public static float sim1(List<Integer> winners) {
        float result=0;
        for(int i=0;i<Data.originalValues.size();i++){
            // float[] rec=Data.getOriginalNumericRec(i);
            float max=0;
            for(int w:winners){
                // float curr=sim(rec,Data.getOriginalNumericRec(w));
                float curr=simByOriginalRec(i,w);
                if(curr>max){
                    max=curr;
                }
            }
            
            result+=max;
        }
        // return result/(Data.originalValues.size()*Data.columns.length);
        return result/Data.originalValues.size();
    }

    public static float sim2(List<Integer> winners) {
        float result=0;
        for(int i=0;i<Data.originalValues.size();i++){
            // float[] rec=Data.getOriginalNumericRec(i);
            for(int w:winners){
                // result+=sim(rec,Data.getOriginalNumericRec(w));
                result+=simByOriginalRec(i,w);
            }
        }
        // return result/(Data.originalValues.size()*winners.size()*Data.columns.length);
        return result/(Data.originalValues.size()*winners.size());
    }
    public static float propSimOld(List<Integer> winners, Entity[] votes,int numRand){
        float bestReault=0;
        int gSize=(int)Math.floor(Data.originalValues.size()/winners.size());
        int extra = Data.originalValues.size()-gSize*winners.size();
        HashSet<Integer> candidates;
        for (int i=0;i<numRand;i++){
            candidates=new HashSet<Integer>();
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
                Entity v=votes[w];
                int wGroupSize=0;
                v.initialize();
                while(wGroupSize<currGroupSize){
                    HashSet<Integer> top=v.topVote();
                    for (int can:top){
                        if(wGroupSize<currGroupSize && candidates.contains(can)){
                            candidates.remove(can);
                            wGroupSize++;
                            // simSum +=sim(Data.getOriginalNumericRec(w), Data.getOriginalNumericRec(can));
                            simSum +=simByOriginalRec(w,can);
                        }
                    }
                    v.popVote();
                } 
            }
            if(simSum>bestReault){
                bestReault=simSum;
            }
        }
        return bestReault;

    }
    public static float propSim(List<Integer> winners, int numRand){
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
            int j=0;
            for (int w:winners){
                // System.out.println("winner "+j);
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
                    // float sim1= SimilarityMeasures.sim(Data.originalValues.get(c1), Data.originalValues.get(w));
                    // float sim2= SimilarityMeasures.sim(Data.originalValues.get(c2), Data.originalValues.get(w));
                    float sim1= simByOriginalRec(c1, w);
                    float sim2= simByOriginalRec(c2, w);
             
                    //descending order
                    return Float.valueOf(sim1).compareTo(sim2);
                }});
                while(wGroupSize<currGroupSize){
                    int can=candidates.remove(candidates.size()-1);
                    // simSum +=sim(Data.getOriginalNumericRec(w), Data.getOriginalNumericRec(can));
                    simSum +=simByOriginalRec(w, can);
                    wGroupSize++;
                }
                j++;
            }
            


            if(simSum>bestReault){
                bestReault=simSum;
            }
        }
        // return bestReault/(Data.originalValues.size()*Data.columns.length);
        return bestReault/(Data.originalValues.size());
    }
}