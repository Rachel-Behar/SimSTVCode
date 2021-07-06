package Entity;

import AttributeRanker.AttributeRanker;
import AttributeRanker.BooleanAttRanker;
import AttributeRanker.NumericAttRankerTree;
import Data.Data;
/**
 * class to hold rankers for all similarit columns
 */
public class ColumnRankers {
    /**
     * array of all similarity column rankers
     */
    public static AttributeRanker[] rankers;
    /**
     * initilizes rankers according to Data
     */
    public static void initRankers() {
        ColumnRankers.rankers=new AttributeRanker[Data.columns.length];
        for(int i=0;i<Data.columns.length;i++){
            if(Data.types[i]==1)
                ColumnRankers.rankers[i]=new NumericAttRankerTree(i);
            else{
                ColumnRankers.rankers[i]=new BooleanAttRanker(i);
            }
        }
    }
    
}
