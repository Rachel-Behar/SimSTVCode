package AttributeRanker;
import java.util.ArrayList;
import java.util.HashSet;

import Data.Graph;

public class GraphAttRanker implements AttributeRanker{
    int canNum;
    Graph g;
    final int maxDist=3;
    int dist;
    HashSet<Integer> returned;
    ArrayList<Integer> prevTop;

    public GraphAttRanker(Graph g){
        this.g=g;
    }
    public int getDist(){
        return dist;
    }
    @Override
    public boolean hasMore() {
        
        return dist>maxDist;
    }

    @Override
    public ArrayList<Integer> getNextCandidates() {
        ArrayList<Integer> top=new ArrayList<Integer>();
        if(dist==1){
            top.addAll(g.getNeighbors(canNum));
        }else{
            for(int n:prevTop){
                for(int i:g.getNeighbors(n)){
                    if(!returned.contains(i)){
                        top.add(i);
                        returned.add(i);
                    }
                }

            }
        }
        prevTop=top;
        dist++;
        return top;
    }

    @Override
    public void init(int can) {
        canNum=can;
        dist=1;
        returned=new HashSet<Integer>();
    }
    
}
