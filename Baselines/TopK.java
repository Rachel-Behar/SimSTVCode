package Baselines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import Globals.Globals;
import Data.Data;
import Similarity.SimilarityMeasures;
/**
 * reuns topK greedy algorithm
 */
public class TopK {
    /**
     * finds k items with lagrest sum of similarities to all other items
     * @param k - number of items to return
     * @return
     */
    public ArrayList<Integer> topK(int k){
        long timeout=Globals.start+Globals.timeoutInMili;
        PriorityQueue<QEntry> cand=new PriorityQueue<>(Collections.reverseOrder());
        for(int i=0;i<Data.size();i++){
            float simCount=0;
            for(int j=0;j<Data.size();j++){
                if(System.currentTimeMillis()>timeout){
                    return null;
                }
                simCount+=SimilarityMeasures.simByRec(i,j)*Data.reps.get(j).size();
            }
            cand.add(new QEntry(i, simCount));
        }
        ArrayList<Integer> results=new  ArrayList<Integer>();
        for(int i=0;i<k;){
            ArrayList<Integer> repList=new ArrayList<Integer>(Data.reps.get(cand.poll().id));
            while(repList.size()>0 && i<k){
                int index=getRandomNumber(0,repList.size());
                    results.add(repList.remove(index));
                i++;
            }
            
        }
        return results;

    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    /**
     * Axillary class for queue entries
     */
    protected class QEntry implements Comparable<QEntry>{
        int id;//id of item
        float simCount;//sum of similarites to all other items
    
        public QEntry(int id, float simCount){
            this.id=id;
            this.simCount=simCount;
        }
        @Override
        public int compareTo(QEntry c2) {
            if(this.simCount==c2.simCount)//tei breaker by id
                return this.id-c2.id;
            return ((Float)this.simCount).compareTo(c2.simCount);//caompares by simCount
        }
    }
}
