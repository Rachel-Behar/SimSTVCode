package AttributeRanker.NumericAttRanker;

import AttributeRanker.AttributeRanker;
import Data.Data;
import Similarity.SimilarityMeasures;

import java.util.*;

public class NumericAttRankerTree implements AttributeRanker {
    TreeSet<CandidateCol> sortedCand;
    float iterationVal;
    Iterator<CandidateCol> leftIterator;
    Iterator<CandidateCol> rightIterator;
    CandidateCol leftVal = null;
    CandidateCol rightVal = null;

    int colIndex;
    int lastValueReturned;

    public void removeCandidate(int id) {
        CandidateCol toRemove = new CandidateCol(id, Data.getValueByRecNumCol(id, colIndex));
        boolean success = sortedCand.remove(toRemove);
        if (!success) System.out.println("Did not remove col = " + colIndex + " id = " + id);
    }

    public boolean hasMore() {
        if (leftVal == null && rightVal == null) return false;

        return true;
    }
    // int idOfLastCandidateReturned;


    public NumericAttRankerTree(int colIndex) {
        this.colIndex = colIndex;
        updateSortedCand(colIndex);
    }

    private void moveLeft(ArrayList<Integer> top) {
        float oldVal = leftVal.value;
        top.add(leftVal.id);
        boolean addedLast = true;
        while (leftIterator.hasNext()) {
            leftVal = leftIterator.next();
            if (leftVal.value == oldVal) top.add(leftVal.id);
            else {
                addedLast = false;
                break;
            }
        }
        if (addedLast) leftVal = null;
    }

    private void moveRight(ArrayList<Integer> top) {
        float oldVal = rightVal.value;
        top.add(rightVal.id);
        boolean addedLast = true;
        while (rightIterator.hasNext()) {
            rightVal = rightIterator.next();
            if (rightVal.value == oldVal) top.add(rightVal.id);
            else {
                addedLast = false;
                break;
            }
        }
        if (addedLast) rightVal = null;
    }

    public ArrayList<Integer> getNextCandidates(){

        ArrayList<Integer> top=new ArrayList<Integer>();

        if (!hasMore()) {
            return null;
        }

        float leftDist = Float.MAX_VALUE;
        if (leftVal != null) leftDist = Math.abs(leftVal.value - iterationVal);
        float rightDist = Float.MAX_VALUE;
        if (rightVal != null) rightDist = Math.abs(rightVal.value - iterationVal);

        if (leftDist < rightDist) {
            moveLeft(top);
        }
        else if (rightDist < leftDist) {
            moveRight(top);
        }
        else {
            moveLeft(top);
            moveRight(top);
        }

        if(!top.isEmpty())
            return top;
        return null;
    }




    void updateSortedCand(int colIndex){

        sortedCand=new TreeSet<>();
        for(int i=0;i<Data.size();i++){
            sortedCand.add(new CandidateCol(i,Data.getValueByRecNumCol(i,colIndex)));
        }
    }

    public void init(int can){
        iterationVal=Data.getValueByRecNumCol(can,colIndex);
        leftIterator = sortedCand.headSet(new CandidateCol(can,iterationVal), false).descendingIterator();
        rightIterator = sortedCand.tailSet(new CandidateCol(can,iterationVal), false).iterator();
        if (leftIterator.hasNext())
            leftVal = leftIterator.next();
        if (rightIterator.hasNext())
            rightVal = rightIterator.next();
    }
}
