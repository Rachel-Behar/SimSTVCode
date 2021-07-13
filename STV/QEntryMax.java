package STV;
/**
 * Axillary class for argmax entry
 */
class QEntryMax implements Comparable<QEntryMax>{
    int id;
    float vCount;

    public QEntryMax(int id, float vCount){
        this.id=id;
        this.vCount=vCount;
    }
    
    @Override
    public int compareTo(QEntryMax c2) {
        if(this.vCount==c2.vCount)
            return this.id-c2.id;
        return ((Float)this.vCount).compareTo(c2.vCount);
    }
}
