package AttributeRanker.NumericAttRanker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import AttributeRanker.AttributeRanker;
import Data.Data;
import Similarity.SimilarityMeasures;

public class NumericAttRanker implements AttributeRanker{
    List<CandidateCol> sortedCand;
    float iterationVal;
    int left;
    int right;
    int colIndex;
    int lastValueReturned;
    public boolean hasMore(){
        if(left>0 || right<sortedCand.size()-1){
            return true;
        }
        return false;
    }
    // int idOfLastCandidateReturned;

    
    public NumericAttRanker(int colIndex){
        this.colIndex=colIndex;
        updateSortedCand(colIndex);  
    }




    // private ArrayList<Integer> getNextCandidates(float val, SearchRange sr){
    //     iterationVal=val;
    //     left=sr.left; right=sr.right;
    //     if(left==0 && right==sortedCand.size()-1){
    //         return null;
    //     }
    //     ArrayList<Integer> top=getNextCandidates();
    //     sr.left=left;
    //     sr.right=right;
    //     return top;
    // }

    public ArrayList<Integer> getNextCandidates(){

        ArrayList<Integer> top=new ArrayList<Integer>();
        if(left==0 && right==sortedCand.size()-1){
            return null;
        }
        if(left==-1){
            firstRankedIteration();
            for(int i=left;i<=right;i++){
                top.add(sortedCand.get(i).id);
            }
        }
        else{
            int prevLeft=left,prevRight=right;
            expandRange();
            if(prevLeft!=left){
                for(int i=left;i<prevLeft;i++){
                    top.add(sortedCand.get(i).id);
                }
            }
            if(prevRight!=right){
                for(int i=prevRight+1;i<=right;i++){
                    top.add(sortedCand.get(i).id);
                }
            }
        }
        if(!top.isEmpty())
            return top;
        return null;
    }

    
    
    void firstRankedIteration(){
        int location=searchValLocation(iterationVal);
        left=location;
        right=location;
        while(left-1>=0 && sortedCand.get(left-1).value==iterationVal)
        left--;
        while(right+1<=sortedCand.size()-1 && sortedCand.get(right+1).value==iterationVal)
        right++;
        if(left==location && right==location){//if value is unique, get next value to add to top vote
            expandRange();
        }

    }

    private int searchValLocation(float iterationVal) {
        return binarySearch(0,sortedCand.size()-1,iterationVal);
    }

    private void expandRange() {
        float rightSim=0,rightVal=0;
        if(right<sortedCand.size()-1){
            rightVal=sortedCand.get(right+1).value;
            rightSim=SimilarityMeasures.numericSim(rightVal,iterationVal);
        }
        float leftSim=0,leftVal=0;
        if(left>0){
            leftVal=sortedCand.get(left-1).value;
            leftSim=SimilarityMeasures.numericSim(leftVal,iterationVal);
        }
        if(rightSim>=leftSim){
            right++;
            while(right+1<=sortedCand.size()-1 && sortedCand.get(right+1).value==rightVal)
                right++;
        }if(rightSim<=leftSim){
            left--;
            while(left-1>=0 && sortedCand.get(left-1).value==leftVal)
                left--;
        }
    }
    

    int binarySearch(int l, int r, float x) 
    { 
        if (r >= l)
        { 
            int mid = l + (r - l) / 2; 
  
            // If the element is present at the 
            // middle itself 
            if (sortedCand.get(mid).value == x) 
                return mid; 
  
            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            if (sortedCand.get(mid).value > x) 
                return binarySearch(l, mid - 1, x); 
  
            // Else the element can only be present 
            // in right subarray 
            return binarySearch(mid + 1, r, x); 
        } 
  
        // We reach here when element is not present 
        // in array 
        return -1; 
    } 

    void updateSortedCand(int colIndex){

        sortedCand=new ArrayList<>();
        for(int i=0;i<Data.size();i++){
            sortedCand.add(new CandidateCol(i,Data.getValueByRecNumCol(i,colIndex)));
        }
        Collections.sort(sortedCand);
    }

    public void init(int can){
        iterationVal=Data.getValueByRecNumCol(can,colIndex);
        left=-1;
        right=-1;
    }
}
