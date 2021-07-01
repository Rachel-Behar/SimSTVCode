package Entity;

import java.util.ArrayList;
import java.util.HashSet;

import Data.Data;
import Similarity.SimilarityMeasures;

public class Entity {
    public int id;
    protected float voteValue;   
    protected float votesForMeCount=0;
    public ArrayList<Integer> inTopListOf;
    public ArrayList<Integer> inNextListOf;
    protected boolean isCandidate;    
    HashSet<Integer> top=null;
    HashSet <Integer> next=null;
    public float lastRetrivedValue=0;
    public int voteRepNum;
    public int canRepNum;
    public float lossIfCanEliminated=0;
    public float votePotentialLoss=0;
    public boolean nextPartOfTop;
    public float topSim;
    public float nextSim;
    public float prevSim=0;

    public void setCount(float c){
        votesForMeCount=c;
    }
    public float getCount(){
        return votesForMeCount;
    }
    public void setLoss(float c){
        lossIfCanEliminated=c;
    }
    public float getLoss(){
        return lossIfCanEliminated;
    }
    public Entity(){};
    public Entity(int c, int repNum){
        this.id=c;
        inTopListOf =new ArrayList<>();
        inNextListOf=new ArrayList<>();
        this.voteRepNum=repNum;
        this.canRepNum=repNum;
        isCandidate=true;  
        nextPartOfTop=true; 
        initialize();
    }
    
    public void initialize() {
        top=new HashSet<Integer>();
        top.add(id);
        topSim=1;
        updateNext();
        if(voteRepNum>1){
            nextPartOfTop=false;
            votePotentialLoss=0;
        }else{
            nextPartOfTop=true;
            votePotentialLoss=topSim-nextSim;
        }
        // System.out.println(id+" top="+top+" next="+next);
    }
    public void updateNext() {
        next=getTop();
        prevSim=nextSim;
        if(next!=null)
            nextSim=SimilarityMeasures.simByRec(id, next.iterator().next());
        else
            nextSim=0;
        nextPartOfTop=false;
    }


    public void popVote() {
        if(next!=null){
            top=next;
            topSim=nextSim;
            updateNext();
        }
    }
    
    protected HashSet<Integer> getTop() {
        ThresholdAlg a=new ThresholdAlg(this);
        if (Data.columns.length == 2)
            return a.runTwoColumns();
        else
            return a.run();
    }

    public HashSet<Integer> topVote() {
        return top;
    }

    public HashSet<Integer> nextVote() {
        return next;
    }
    

    public float getVoteValue() {
        return voteValue*voteRepNum;
    }
    
    public boolean removeSingle() {
        canRepNum--;
        if(canRepNum==0){
            isCandidate=false;
            return true;
        }
        return false;
    }
    public void removeMulti() {
        canRepNum=0;
        isCandidate=false;
    }

    public void setVoteValue(float val) {
        voteValue=val;
        
    }
    
    public void addInTopListOf(int voter) {
        inTopListOf.add(voter);
    
    }

    public void addInNextListOf(int voter) {
        inNextListOf.add(voter);
    
    }

    public void removeNextListOf(int voter) {
        int i=inNextListOf.indexOf(voter);
        if(i!=-1)
            inNextListOf.remove(inNextListOf.indexOf(voter));
    }

    public boolean isCandidate() {
        return isCandidate;
    }

    public ArrayList<Integer> inTopList(){
        return inTopListOf;
    }

    public ArrayList<Integer> inNextList(){
        return inNextListOf;
    }
    
}

