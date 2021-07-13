package Entity;

import java.util.ArrayList;
import java.util.HashSet;
import Data.Data;
import Globals.Globals;
import Similarity.SimilarityMeasures;
public class ThresholdAlg {

    Item v;//item voter currently running TA
    int numOfCol;//number of similarit columns
    /**
     * constructor, initilized the ColumnRankers vith values of current voter 
     * @param v - the voter
     */
    public ThresholdAlg(Item v) {
        this.v=v;
        this.numOfCol=Data.columns.length;
        for(int col=0;col<numOfCol;col++){
            ColumnRankers.rankers[col].init(v.id);
        }
    }
    /**
     * runs the TA to find the top similar candidtes to the current voter amog existinf candidates
     * @return top candidates
     */
    public HashSet<Integer> run(){
        float topValue=0;
        float threshold=numOfCol;
        HashSet<Integer> top=new HashSet<>();
        float[] n2=Data.getNumericRec(v.id);
        while(threshold >0 && threshold>=topValue){
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
        }
        v.lastRetrivedValue=topValue;

        if(top.isEmpty())
            return null;
        return top;
    }
    /**
     * optimized version of TA in case there are only two similarit columns
     * @return top candidates
     */
    public HashSet<Integer> runTwoColumns(){
        float topValue=0;
        float threshold=numOfCol;
        HashSet<Integer> top=new HashSet<>();
        float[] n2=Data.getNumericRec(v.id);
        while(threshold >0 && threshold>=topValue){
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
        }
        v.lastRetrivedValue=topValue;

        if(top.isEmpty())
            return null;
        return top;
    }
}
