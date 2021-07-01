package Greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.text.html.parser.Entity;

import Globals.Globals;
import Data.Data;
import Similarity.SimilarityMeasures;

public class Greedy {
    public ArrayList<Integer> topK(int k){
        // long start = System.currentTimeMillis();
        long timeout=Globals.start+Globals.timeoutInMili;
        PriorityQueue<QEntry> cand=new PriorityQueue<>(Collections.reverseOrder());
        for(int i=0;i<Data.size();i++){
            float vCount=0;
            for(int j=0;j<Data.size();j++){
                if(System.currentTimeMillis()>timeout){
                    return null;
                }
                // vCount+=SimilarityMeasures.sim(Data.getNumericRec(i),Data.getNumericRec(j));
                vCount+=SimilarityMeasures.simByRec(i,j)*Data.reps.get(j).size();
            }
            cand.add(new QEntry(i, vCount));
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
    // public List<Integer> topK(int k){
    //     // long start = System.currentTimeMillis();
    //     long timeout=Globals.start+Globals.timeoutInMili;
    //     PriorityQueue<QEntry> cand=new PriorityQueue<>(Collections.reverseOrder());
    //     for(int i=0;i<Data.size();i++){
    //         float vCount=0;
    //         for(int j=0;j<Data.size();j++){
    //             if(System.currentTimeMillis()>timeout){
    //                 return null;
    //             }
    //             // vCount+=SimilarityMeasures.sim(Data.getNumericRec(i),Data.getNumericRec(j));
    //             vCount+=SimilarityMeasures.simByRec(i,j);
    //         }
    //         cand.add(new QEntry(i, vCount));
    //     }
    //     List<Integer> result=new ArrayList<>();
    //     for(int i=0;i<k;i++){
    //         result.add(cand.poll().id);
    //     }
        
    //     return result;

    // }

    protected class QEntry implements Comparable<QEntry>{
        int id;
        float vCount;
    
        public QEntry(int id, float vCount){
            this.id=id;
            this.vCount=vCount;
        }
    
        @Override
        public int compareTo(QEntry c2) {
            if(this.vCount==c2.vCount)
                return this.id-c2.id;
            return ((Float)this.vCount).compareTo(c2.vCount);
        }
    }

    public List<Integer> greedySim1(int k){
        // Globls.start = System.currentTimeMillis();
        long timeout=Globals.start+Globals.timeoutInMili;
        float maxScore=0;
        List<Integer> result=new ArrayList<>();
        for (int i=0;i<k;i++){
            int chosen=-1;
            for(int j=0;j<Data.size();j++){
                if(System.currentTimeMillis()>timeout){
                    return null;
                }
                if(!result.contains(j)){
                    result.add(j);
                    float score=SimilarityMeasures.sim1(result);
                    if(score>=maxScore){
                        maxScore=score;
                        chosen=j;
                    }
                    result.remove(result.size()-1);
                }
            }
            result.add(chosen);
        }
        return result;
    }
    // def greedy_sim1(candidates, sim, columns, types, data, k):
    // max_score = 0
    // result = []
    // curr_candidates = copy.deepcopy(candidates)
    // for i in range(0, k):
    //     for c in curr_candidates:
    //         result.append(c)
    //         score = SimilarityMeasures.sim1(candidates, result, sim, columns, types, data)
    //         if score >= max_score:
    //             max_score = score
    //             chosen = c
    //         result.remove(c)
    //     curr_candidates.remove(chosen)
    //     result.append(chosen)
    // return result
}
