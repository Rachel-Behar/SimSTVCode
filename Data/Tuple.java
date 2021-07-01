package Data;

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
//        float sum = 0;
//        for (float i:t) sum += i;
//
//        return ((Float)sum).hashCode();
//        final int prime = 31;
//        float result = 1;
//        for(float i:t)
//            result = prime * result + i;
//        return (int) result;
    }
}
