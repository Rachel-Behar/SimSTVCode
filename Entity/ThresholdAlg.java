package Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import Data.Data;
import Globals.Globals;
import Similarity.SimilarityMeasures;
import STV.*;
public class ThresholdAlg {

    Entity v;;
    int numOfCol;
    static int times = 0;
    
    public ThresholdAlg(Entity e) {
        this.v=e;
        this.numOfCol=Data.columns.length;
        for(int col=0;col<numOfCol;col++){
            ColumnRankers.rankers[col].init(e.id);
        }
    }
    public HashSet<Integer> runTwoColumns(){
        times++;
        //if (times % 1000 == 0)
        //    System.out.println("Running threshold algorithm " + times);
        float topValue=0;
        float threshold=numOfCol;
        HashSet<Integer> top=new HashSet<>();
        float[] n2=Data.getNumericRec(v.id);
        int j = 0;
        while(threshold >0 && threshold>=topValue){
            j++;
            float newThreshold=0;
            for(int col=0;col<numOfCol;col++){
                ArrayList<Integer> curTop=ColumnRankers.rankers[col].getNextCandidates();
                if(curTop!=null){

                    float closestOtherDistance = Float.MAX_VALUE;
                    float currentOther = Data.getValueByRecNumCol(v.id, 1-col);
                    HashSet<Integer> bestCurTop=new HashSet<Integer>();
                    for (int id : curTop) {
                        if (Globals.isCandidate(id) && id != v.id) {
                            float otherDistance = Math.abs(Data.getValueByRecNumCol(id, 1-col) - currentOther);
                            if (otherDistance < closestOtherDistance) {
                                closestOtherDistance = otherDistance;
                                // closestOtherId1 = id;
                                // closestOtherId2 = -1;
                                bestCurTop=new HashSet<Integer>();
                                bestCurTop.add(id);
                            }
                            else if (otherDistance == closestOtherDistance) {
                                bestCurTop.add(id);
                            }
                        }
                    }

                    newThreshold+=SimilarityMeasures.numericSim(Data.getValueByRecNumCol(curTop.get(0), col), n2[col]);

                    for(int can:bestCurTop){

                        float curValue=SimilarityMeasures.simByRec(v.id, can);
                        // if(v.lastRetrivedValue !=0 && curValue>=v.lastRetrivedValue){
                        //     continue;
                        // }

                        if(curValue==topValue){
                            top.add(can);
                        }else if(curValue>topValue){
                            topValue=curValue;
                            top=new HashSet<>();
                            top.add(can);

                        }

                    }
                }      
            }
            threshold=newThreshold;
       //     System.out.println(threshold);
        }
        v.lastRetrivedValue=topValue;

        if(top.isEmpty())
            return null;
        return top;
    }
    // public HashSet<Integer> runTwoColumns(){
    //     times++;
    //     //if (times % 1000 == 0)
    //     //    System.out.println("Running threshold algorithm " + times);
    //     float topValue=0;
    //     float threshold=numOfCol;
    //     HashSet<Integer> top=new HashSet<>();

    //     int maximumRuns = Data.size();
    //     if (STV.numCandidates() > 0)
    //         maximumRuns = STV.numCandidates();
    //     float[] n2=Data.getNumericRec(v.id);
    //     int j = 0;
    //     while(threshold >0 && threshold>=topValue){
    //         j++;
    //         float newThreshold=0;
    //         for(int col=0;col<numOfCol;col++){
    //             ArrayList<Integer> curTop=ColumnRankers.rankers[col].getNextCandidates();
    //             if(curTop!=null){

    //                 float closestOtherDistance = Float.MAX_VALUE;
    //                 float currentOther = Data.getValueByRecNumCol(v.id, 1-col);
    //                 int closestOtherId1 = -1;
    //                 int closestOtherId2 = -1;
    //                 for (int id : curTop) {
    //                     if (Globals.isCandidate(id) && id != v.id) {
    //                         float otherDistance = Math.abs(Data.getValueByRecNumCol(id, 1-col) - currentOther);
    //                         if (otherDistance < closestOtherDistance) {
    //                             closestOtherDistance = otherDistance;
    //                             closestOtherId1 = id;
    //                             closestOtherId2 = -1;
    //                         }
    //                         else if (otherDistance == closestOtherDistance) {
    //                             closestOtherId2 = id;
    //                         }
    //                     }
    //                 }
    //                 ArrayList<Integer> bestCurTop = new ArrayList<>();
    //                 if (closestOtherId1 != -1) bestCurTop.add(closestOtherId1);
    //                 if (closestOtherId2 != -1) bestCurTop.add(closestOtherId2);

    //                 newThreshold+=SimilarityMeasures.numericSim(Data.getValueByRecNumCol(curTop.get(0), col), n2[col]);

    //                 for(int can:bestCurTop){

    //                     float curValue=SimilarityMeasures.simByRec(v.id, can);
    //                     if(v.lastRetrivedValue !=0 && curValue>=v.lastRetrivedValue){
    //                         continue;
    //                     }

    //                     if(curValue==topValue){
    //                         top.add(can);
    //                     }else if(curValue>topValue){
    //                         topValue=curValue;
    //                         top=new HashSet<>();
    //                         top.add(can);

    //                     }

    //                 }
    //              }      
    //         }
    //         threshold=newThreshold;
    //    //     System.out.println(threshold);
    //     }
    //     v.lastRetrivedValue=topValue;

    //     if(top.isEmpty())
    //         return null;
    //     return top;
    // }


    public HashSet<Integer> run(){
        // times++;
        // if (times % 1000 == 0)
        //     System.out.println("Running threshold algorithm " + times);
        float topValue=0;
        float threshold=numOfCol;
        HashSet<Integer> top=new HashSet<>();

        int maximumRuns = Data.size();
        if (STV.numCandidates() > 0)
            maximumRuns = STV.numCandidates();
        float[] n2=Data.getNumericRec(v.id);
        int j = 0;
        while(threshold >0 && threshold>=topValue){
            j++;
            float newThreshold=0;
            for(int col=0;col<numOfCol;col++){
                ArrayList<Integer> curTop=ColumnRankers.rankers[col].getNextCandidates();
                if(curTop!=null){
                    newThreshold+=SimilarityMeasures.numericSim(Data.getValueByRecNumCol(curTop.get(0), col), n2[col]);
                    for(int can:curTop){

                        if (!Globals.isCandidate(can) || can == v.id){
                            continue;
                        }
                        float curValue=SimilarityMeasures.simByRec(v.id, can);
                        if(v.lastRetrivedValue !=0 && curValue>=v.lastRetrivedValue){
                            continue;
                        }

                        if(curValue==topValue){
                            top.add(can);
                        }else if(curValue>topValue){
                            topValue=curValue;
                            top=new HashSet<>();
                            top.add(can);

                        }

                    }
                }
            }
            threshold=newThreshold;
            //     System.out.println(threshold);
        }
        v.lastRetrivedValue=topValue;

        if(top.isEmpty())
            return null;
        return top;
    }


    public class QEntry implements Comparable<QEntry>{
        int id;
        float simVal;
    
        public QEntry(int id, float simVal){
            this.id=id;
            this.simVal=simVal;
        }
    
        @Override
        public int compareTo(QEntry c2) {
            if(this.simVal==c2.simVal)
                return this.id-c2.id;
            return ((Float)this.simVal).compareTo(c2.simVal);
        }
    }
}
