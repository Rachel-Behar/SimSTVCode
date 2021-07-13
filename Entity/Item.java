package Entity;

import java.util.ArrayList;
import java.util.HashSet;
import Data.Data;
import Similarity.SimilarityMeasures;
/**
 * Represents an item. as both candidate and voter
 */
public class Item {
    public int id;//serial id number given by the Data load method

    //voter attributes
    protected float voteValue;// value of the voter. initilized to 1 and decreases with transfered votes.
    HashSet<Integer> top=null;// list of current top vote
    HashSet <Integer> next=null;// list of second best vote after top
    public float lastRetrivedValue=0;//last value of similarity found for the voter (for ThresholdAlgorithm use)
    public int voteRepNum;// power of meta voter, number of voters represented
    public float votePotentialLoss=0;// loss of vote if top is eliminated
    public boolean nextPartOfTop;// true if the initial vote has a single non meta top vote, and then the next list should be treated as part of the top.  
    public float topSim;// similarity score of the top vote and the voter
    public float nextSim;// similarity score of the next vote and the voter

    //candidate attributes
    protected float vScore=0;// score of candidate. the total number of votes the candidate is currently awarded
    public ArrayList<Integer> inTopListOf;//list of voters that currently have this candidate in top
    public ArrayList<Integer> inNextListOf;//list of voters that currently have this candidate in next
    protected boolean isCandidate; // indicates is this item currently in candidate pool
    public int canRepNum;// power of meta candidate, number of candidates currently represented.
    public float lossIfCanEliminated=0;// sum loss of all voters that have this candidate in top if candidate is eliminated
    /**
     * constructor
     * @param id - serial id of item
     * @param repNum - for meta items, number of items the meta item represents
     */
    public Item(int id, int repNum){
        this.id=id;
        inTopListOf =new ArrayList<>();
        inNextListOf=new ArrayList<>();
        this.voteRepNum=repNum;
        this.canRepNum=repNum;
        isCandidate=true;  
        nextPartOfTop=true; 
        initialize();
    }
    /**
     * initilazes the voters top and next 
     */
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
    }
    /**
     * updates the next of the vote
     */
    public void updateNext() {
        next=getTop();
        if(next!=null)
            nextSim=SimilarityMeasures.simByRec(id, next.iterator().next());
        else
            nextSim=0;
        nextPartOfTop=false;
    }
    /**
     * moves next to be top, and updates the next
     */
    public void popVote() {
        if(next!=null){
            top=next;
            topSim=nextSim;
            updateNext();
        }
    }
    /**
     * gets current top, using the Treshold algorithm
     * @return
     */
    protected HashSet<Integer> getTop() {
        ThresholdAlg a=new ThresholdAlg(this);
        if (Data.columns.length == 2)// an optimized version of TA if there are only two similarity columns
            return a.runTwoColumns();
        else
            return a.run();
    }
    /**
     * caculates the vote value of the meta voter
     * @return
     */
    public float getVoteValue() {
        return voteValue*voteRepNum;
    }
    /**
     * removes a voter from the next list
     * @param voter - the id of voter to be removed
     */
    public void removeNextListOf(int voter) {
        int i=inNextListOf.indexOf(voter);
        if(i!=-1)
            inNextListOf.remove(inNextListOf.indexOf(voter));
    }
    /**
     * removes a single copy of a meta candidate
     */
    public boolean removeSingle() {
        canRepNum--;
        if(canRepNum==0){
            isCandidate=false;
            return true;
        }
        return false;
    }
    /**
     * removes all copies of meta candidate
     */
    public void removeMulti() {
        canRepNum=0;
        isCandidate=false;
    }
    //getters and setters
    public HashSet<Integer> topVote() {
        return top;
    }
    public HashSet<Integer> nextVote() {
        return next;
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

    public boolean isCandidate() {
        return isCandidate;
    }

    public ArrayList<Integer> inTopList(){
        return inTopListOf;
    }

    public ArrayList<Integer> inNextList(){
        return inNextListOf;
    }

    public void setCount(float c){
        vScore=c;
    }

    public float getCount(){
        return vScore;
    }

    public void setLoss(float c){
        lossIfCanEliminated=c;
    }

    public float getLoss(){
        return lossIfCanEliminated;
    }
}

