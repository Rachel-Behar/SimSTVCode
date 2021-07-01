package AttributeRanker;

import java.util.ArrayList;

public interface AttributeRanker {
    public boolean hasMore();
    public ArrayList<Integer> getNextCandidates();
    public void init(int can);
}
