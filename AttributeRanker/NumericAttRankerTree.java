package AttributeRanker;

import Data.Data;
import java.util.*;
/**
 * Ranker for numeric columns
 */
public class NumericAttRankerTree implements AttributeRanker {
    TreeSet<CandidateCol> sortedCand;//balanced tree holding all the item ids sorted by the value of the rankers column
    float iterationVal;//value searched in cutterent iteration
    Iterator<CandidateCol> leftIterator;//iterator pointing to the left of the search range
    Iterator<CandidateCol> rightIterator;//iterator pointing to the right of the search range
    CandidateCol leftVal = null;//value of item leftIterator is pointing at
    CandidateCol rightVal = null;//value of item rightIterator is pointing at
    int colIndex;// index of rankers column
    int lastValueReturned;// value return last from ranker
    /**
     * constructor
     * @param colIndex - number of column
     */
    public NumericAttRankerTree(int colIndex) {
        this.colIndex = colIndex;
        updateSortedCand(colIndex);
    }
    @Override
    public boolean hasMore() {
        if (leftVal == null && rightVal == null) return false;

        return true;
    }
    @Override
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
    @Override
    public void init(int can){
        iterationVal=Data.getValueByRecNumCol(can,colIndex);
        leftIterator = sortedCand.headSet(new CandidateCol(can,iterationVal), false).descendingIterator();
        rightIterator = sortedCand.tailSet(new CandidateCol(can,iterationVal), false).iterator();
        if (leftIterator.hasNext())
            leftVal = leftIterator.next();
        if (rightIterator.hasNext())
            rightVal = rightIterator.next();
    }
    /**
     * removed item from ranker by id
     * @param id - id of item to remove
     */
    public void removeCandidate(int id) {
        CandidateCol toRemove = new CandidateCol(id, Data.getValueByRecNumCol(id, colIndex));
        boolean success = sortedCand.remove(toRemove);
        if (!success) System.out.println("Did not remove col = " + colIndex + " id = " + id);
    }
    /**
     * move left ierator left while the value is equal and ad ids to top.
     * update left val
     * @param top - list of found ids
     */
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
    /**
     * move right ierator right while the value is equal and ad ids to top. 
     * update right val
     * @param top - list of found ids
     */
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
    /**
     * update Treeset with items sorted by the value of colIndex
     * @param colIndex
     */
    void updateSortedCand(int colIndex){
        sortedCand=new TreeSet<>();
        for(int i=0;i<Data.size();i++){
            sortedCand.add(new CandidateCol(i,Data.getValueByRecNumCol(i,colIndex)));
        }
    }
    
    /**
     * Axillary class to hold id of item and the vlue of the rankers column, comparable by value
     */
    public class CandidateCol implements Comparable<CandidateCol>{
        int id;//id of item
        float value;//value of the item in column colIndex
        public CandidateCol(int id,float value){
            this.id=id;
            this.value=value;
        }
    
        @Override
        public int compareTo(CandidateCol o) {
            if(value==o.value){
                return id-o.id;
            }
            if(value>o.value)
                return 1;
            else
                return -1;
        }
    }
}
