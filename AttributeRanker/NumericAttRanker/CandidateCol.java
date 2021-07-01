package AttributeRanker.NumericAttRanker;

public class CandidateCol implements Comparable<CandidateCol>{
    int id;
    float value;
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