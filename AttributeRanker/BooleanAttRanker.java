package AttributeRanker;

import java.util.ArrayList;
import java.util.HashMap;
import Data.Data;
/**
 * Ranker for boolean columns
 */
public class BooleanAttRanker implements AttributeRanker{

    HashMap<Integer,ArrayList<Integer>> equivClass;//hash table holding all hashvalues of the given attribute and lists of items with that hashvalue.
    int hashVal;// current search hashvlue
    int colIndex;// index of rankers column
    int next;//the next value to be returnd
    ArrayList<Integer> top=null;//list of items in the top
    /**
     * constructor
     * @param colIndex - number of column
     */
    public BooleanAttRanker(int colIndex){
        this.colIndex=colIndex;
        updateHashMap(colIndex);
    }

    @Override
    public boolean hasMore() {
        if(top!=null)
            return next<top.size();
        return true;
    }

    @Override
    public ArrayList<Integer> getNextCandidates() {
        if(next<top.size()){
            ArrayList<Integer> toReturn=new ArrayList<Integer>();
            toReturn.add(top.get(next));
            next++;
            return toReturn;
        }
        return null;
    }

    @Override
    public void init(int can) {
        hashVal=(int)Data.getValueByRecNumCol(can,colIndex);
        top=equivClass.get(hashVal);
        next=0;
    }
    /**
     * update the equivClass hashMap to hold all hashcodes of items in column colIndex and the items that have that value
     * @param colIndex - index of column
     */
    private void updateHashMap(int colIndex) {
        equivClass=new HashMap<>();
        for(int i=0;i<Data.size();i++){
            int key=(int)Data.getValueByRecNumCol(i,colIndex);

            if(!equivClass.containsKey(key)){
                equivClass.put(key, new ArrayList<Integer>());
            }
            equivClass.get(key).add(i);
        }
    }
    
}
