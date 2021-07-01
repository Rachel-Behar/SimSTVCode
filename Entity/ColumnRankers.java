package Entity;

import AttributeRanker.AttributeRanker;
import AttributeRanker.BooleanAttRanker;
import AttributeRanker.GraphAttRanker;
import AttributeRanker.NumericAttRanker.NumericAttRanker;
import AttributeRanker.NumericAttRanker.NumericAttRankerTree;
import Data.Data;

public class ColumnRankers {
    public static void initRankers() {
        ColumnRankers.rankers=new AttributeRanker[Data.columns.length];
        for(int i=0;i<Data.columns.length;i++){
            if(Data.columns[i]!=-1){
                if(Data.types[i]==1)
                    //ColumnRankers.rankers[i]=new NumericAttRanker(i);
                    ColumnRankers.rankers[i]=new NumericAttRankerTree(i);
                else{
                    ColumnRankers.rankers[i]=new BooleanAttRanker(i);
                }
            }else{
                ColumnRankers.rankers[i]=new GraphAttRanker(Data.g);
            }
        }
    }
    public static AttributeRanker[] rankers;
}
