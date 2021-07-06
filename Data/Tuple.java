package Data;
/**
 * Axillary calss for finding duplicate teuples.
 */
class Tuple {
    float[] t;
    Tuple(float[] t){
        this.t=t;
    }
    @Override
    public boolean equals (Object other) {
        for(int i=0;i<this.t.length;i++){
            if(this.t[i]!=((Tuple)other).t[i])
                return false;
        }
        return true;
    }
    @Override
    public int hashCode () {
        return java.util.Arrays.hashCode(t);
    }
}
