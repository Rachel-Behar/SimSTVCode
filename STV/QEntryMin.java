package STV;

class QEntryMin implements Comparable<QEntryMin>{
    int id;
    float vCount;
    float loss;

    public QEntryMin(int id, float vCount, float loss){
        this.id=id;
        this.vCount=vCount;
        this.loss=loss;
    }

    @Override
    public int compareTo(QEntryMin c2) {
        if(this.vCount==c2.vCount){
            if(this.loss==c2.loss)
                return this.id-c2.id;
            return ((Float)this.loss).compareTo(c2.loss);
        }
        return ((Float)this.vCount).compareTo(c2.vCount);
    }

    // @Override
    // public int compareTo(QEntryMin c2) {
    //     // if(this.vCount==c2.vCount){
    //         if(this.loss==c2.loss){
    //             if(this.vCount==c2.vCount){
    //                 return this.id-c2.id;
    //             }
    //             return ((Float)this.vCount).compareTo(c2.vCount);
    //         }
    //     return ((Float)this.loss).compareTo(c2.loss);
    //     // }
                
    // }
}
