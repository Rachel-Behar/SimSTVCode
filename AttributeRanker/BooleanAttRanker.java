package AttributeRanker;

import java.util.ArrayList;
import java.util.HashMap;

import Data.Data;

public class BooleanAttRanker implements AttributeRanker{
    HashMap<Integer,ArrayList<Integer>> equivClass;
    int hashVal;
    int colIndex;
    int next;
    // boolean gotTop;
    ArrayList<Integer> top=null;
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
        // gotTop=false;
        top=equivClass.get(hashVal);
        next=0;
    }
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
