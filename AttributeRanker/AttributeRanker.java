package AttributeRanker;

import java.util.ArrayList;
/**
 * Interface for rankers that return clossest items to a given column value
 */
public interface AttributeRanker {
    /**
     * returns true if not all values were returnd yet
     * @return
     */
    public boolean hasMore();
    /**
     * returns ids of next clossest items
     * @return
     */
    public ArrayList<Integer> getNextCandidates();
    /**
     * initilizes search for a given item
     * @param can - item id
     */
    public void init(int can);
    
}
