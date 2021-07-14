package STV;

import java.util.*;
import AttributeRanker.AttributeRanker;
import AttributeRanker.NumericAttRankerTree;
import Data.Data;
import Entity.Item;
import Globals.Globals;
import Entity.ColumnRankers;
/**
 * runs SimSTV algorithm
 */
public class SimSTV {

    protected TreeSet<QEntryMax> rankMax;//holds all candidates orders by vCount to find leader
    protected TreeSet<QEntryMin> rankMin;//holds all candidates orders by vCount, secondery sort loss, to find looser
    int eliminated=0;//number of candidates eliminated so far/
    int numOfWinners=0;// number of current candidates elected
   /**
    * constructor
    * @param items
    */
    public SimSTV(Item[] items){
        rankMax=new TreeSet<>();
        rankMin=new TreeSet<>();
        init(items);
    }
    /**
     * initilizes items for SimSTV run
     * @param items
     */
    protected void init(Item[] items) {
        Globals.items=items;
        for (Item v:items){
            v.setVoteValue(1);
            for (int c :v.topVote()){
                Item can=items[c];
                can.setCount(can.getCount()+v.getVoteValue());
                can.addInTopListOf(v.id);
            }
            if(v.nextVote()!=null)
                for (int c :v.nextVote()){
                    Item can=items[c];
                    if(v.nextPartOfTop){
                        can.setCount(can.getCount()+v.getVoteValue());
                    }
                    can.addInNextListOf(v.id);
                }
                if(Globals.topIsSingle(v)){
                    v.setLoss((v.topSim-v.nextSim)*v.getVoteValue());
                }
        }
        
        for (Item v:items){
            rankMax.add(new QEntryMax(v.id,v.getCount()));
            rankMin.add(new QEntryMin(v.id,v.getCount(),v.getLoss()));
        }
    }
    /**
     * runs SimSTV algorithm
     * @param k - numer of winners to find
     * @return list of winners ids
     */
    public ArrayList<Integer> runSimSTV(int k){
        Globals.middle=System.currentTimeMillis();
        long timeout=Globals.start+Globals.timeoutInMili;
        HashMap<Integer,Integer> winners=new HashMap<Integer,Integer>();
        numOfWinners=0;
        eliminated=0;
        float threshold = (float) (Math.floor(Data.originalValues.size()/(k+1))+1);
        while(numOfWinners<k && Data.originalValues.size()-eliminated>k){
            if(System.currentTimeMillis()>timeout){
                return null;
            }
            QEntryMax w=rankMax.last();
            if (w.vCount>=threshold){
                transferSurplus(w.id,threshold);
                addWinner(winners, w.id);
            }
            else{
                QEntryMin e=rankMin.first();
                eliminated+=Globals.items[e.id].canRepNum;
                transferVotes(e.id);      
            }
        }
        if(numOfWinners<k){
            addRestOfCandidates(winners);
            
        }
        ArrayList<Integer> results = getListOfWinnersByOriginalID(winners);
        return results;
    }
    /**
     * remove a single copy of a meta cnadidate
     * @param id - the id of candidate to remove
     * @return
     */
    protected boolean removeSingleCandidate(int id) {
        Item e=Globals.items[id];
        if(e.removeSingle()){
            rankMin.remove(new QEntryMin(id, e.getCount(), e.getLoss()));
            rankMax.remove(new QEntryMax(id, e.getCount()));
            AttributeRanker[] rankers = ColumnRankers.rankers;
            for (int i = 0 ; i < Data.types.length ; i++) {
                if (Data.types[i] == 1) {
                    NumericAttRankerTree attRanker = (NumericAttRankerTree) rankers[i];
                    attRanker.removeCandidate(id);
                }
            }
            return true;
        }
        return false;
    }
    /**
     * remove all copies of a meta candidate
     * @param id - the id of candidate to remove
     */
    protected void removeMetaCandidate(int id) {
        Item e=Globals.items[id];
        e.removeMulti();
        rankMin.remove(new QEntryMin(id, e.getCount(), e.getLoss()));
        rankMax.remove(new QEntryMax(id, e.getCount()));
        AttributeRanker[] rankers = ColumnRankers.rankers;
        for (int i = 0 ; i < Data.types.length ; i++) {
            if (Data.types[i] == 1) {
                NumericAttRankerTree attRanker = (NumericAttRankerTree) rankers[i];
                attRanker.removeCandidate(id);
            }
        }
    }
    /**
     * transfers votes of eliminated candidate for all voters that had that candidate in top
     * @param loser - id of eliminated candidate
     */
    protected void transferVotes(int loser) {
        removeMetaCandidate(loser);
        float[] valuesChanged = new float[Data.size()];
        float[] lossChanged = new float[Data.size()];
        HashSet<Integer> candidatesChanged=new HashSet<>();
        List<Integer> loserInNext=Globals.items[loser].inNextList();
        for (int c:loserInNext) {//update all voters that had loset in next list
            Item v=Globals.items[c];
            v.nextVote().remove(loser);
            if(v.nextVote().isEmpty()){
                v.updateNext();
                updateInNextListOf(v);
                if(Globals.topIsSingle(v)){
                    int id=v.topVote().iterator().next();
                    lossChanged[id]-=v.votePotentialLoss*v.getVoteValue();
                    v.votePotentialLoss=v.topSim-v.nextSim;
                    lossChanged[id]+=v.votePotentialLoss*v.getVoteValue();
                }else{
                    v.votePotentialLoss=0;
                }
            } 
        }
        List<Integer> loserInTop=Globals.items[loser].inTopList();
        for (int c:loserInTop) {//update all voters that had loset in top list
            Item v=Globals.items[c];
            v.topVote().remove(loser);
            if (v.topVote().isEmpty()) {
                v.popVote();
                updateInTopListOf(v);
                if(v.topVote()!=null){
                    for (int id: v.topVote()) {
                        valuesChanged[id] += v.getVoteValue();
                        candidatesChanged.add(id);
                    }
                }
            }
            if(!v.topVote().isEmpty()&& Globals.topIsSingle(v)){
                int id=v.topVote().iterator().next();
                v.votePotentialLoss=v.topSim-v.nextSim;
                lossChanged[id]+=v.votePotentialLoss*v.getVoteValue();
            }else{
                v.votePotentialLoss=0;
            }
        }
        updateMinMaxRank(valuesChanged, lossChanged, candidatesChanged);
    }
    /**
     * updates entries of minRank and MaxRank for candidates that had a change in vCount or loss
     * @param valuesChanged - change in vCount values
     * @param lossChanged - change in loss values
     * @param candidatesChanged - set of candidates that needs update
     */
    private void updateMinMaxRank(float[] valuesChanged, float[] lossChanged, HashSet<Integer> candidatesChanged) {
        for (int id:candidatesChanged){
            Item e= Globals.items[id];
            rankMin.remove(new QEntryMin(id, e.getCount(), e.getLoss()));
            rankMax.remove(new QEntryMax(id, e.getCount()));
            e.setCount(e.getCount()+valuesChanged[id]);
            e.setLoss(e.getLoss()+lossChanged[id]);
            rankMin.add(new QEntryMin(id, e.getCount(),e.getLoss()));
            rankMax.add(new QEntryMax(id,e.getCount()));
        }
    }
    /**
     * transpers surplus votes of choosen candidate for all voters that had that candidate in top
     * @param leader
     * @param threshold
     */
    protected void transferSurplus(int leader, float threshold) {
        boolean deleted=removeSingleCandidate(leader);
        float[] valuesChanged = new float[Data.size()];
        float[] lossChanged = new float[Data.size()];
        HashSet<Integer> changedCandidates = new HashSet<>();
        List<Integer> LeaderInTop=Globals.items[leader].inTopList();
        List<Integer> leaderInNext=Globals.items[leader].inNextList();
        float fraction = calcFrac(leader, threshold, LeaderInTop,leaderInNext);
        for (int c:leaderInNext) {//update all voters that had leader in next list
            Item v=Globals.items[c];
            if(deleted){
                v.nextVote().remove(leader);
            }
            boolean updateVoteValue=v.nextPartOfTop;
            if(updateVoteValue){ 
                for(int id : v.nextVote()){
                    valuesChanged[id] -= v.getVoteValue();
                    changedCandidates.add(id);
                }
                for(int id : v.topVote()){
                    valuesChanged[id] -= v.getVoteValue();
                    lossChanged[id]-=v.votePotentialLoss*v.getVoteValue();
                    changedCandidates.add(id);
                }
            }
            if(v.nextVote().isEmpty()){
                v.updateNext();
                if(Globals.topIsSingle(v)){
                    v.votePotentialLoss=v.topSim-v.nextSim;
                }else{
                    v.votePotentialLoss=0;
                }
                updateInNextListOf(v);
            }
            if(updateVoteValue){ 
                v.setVoteValue(fraction);
                if(v.nextPartOfTop)
                    for(int id : v.nextVote()){
                        valuesChanged[id] += v.getVoteValue();
                        changedCandidates.add(id);
                    }
                for(int id : v.topVote()){
                    valuesChanged[id] += v.getVoteValue();
                    lossChanged[id]+=v.votePotentialLoss*v.getVoteValue();
                    changedCandidates.add(id);
                }
            }
            
        }
        
        for (int c:LeaderInTop) {//update all voters that had leader in top list
            Item v=Globals.items[c];
            if(deleted){
                v.topVote().remove(leader);
            }
            for(int id : v.topVote()){
                valuesChanged[id] -= v.getVoteValue();
                lossChanged[id]-=v.votePotentialLoss*v.getVoteValue();
                changedCandidates.add(id);
            }
            if(v.topVote().isEmpty()){
                v.popVote();
                if(!v.topVote().isEmpty()&&Globals.topIsSingle(v)){
                    v.votePotentialLoss=v.topSim-v.nextSim;
                }else{
                    v.votePotentialLoss=0;
                }
                updateInTopListOf(v);
            }
            v.setVoteValue(fraction);
            for(int id:v.topVote()){
                valuesChanged[id] += v.getVoteValue();
                lossChanged[id]+=v.votePotentialLoss*v.getVoteValue();
                changedCandidates.add(id);
            }
        }
        updateMinMaxRank(valuesChanged, lossChanged, changedCandidates);
    }
    /**
     * returns a list of winners by original item id. for each meta item winner, according to the number of coies of the winner randomly picks items fro represented list 
     * @param winners - map of meta winners id and the number of copies they have
     * @return
     */
    private ArrayList<Integer> getListOfWinnersByOriginalID(HashMap<Integer, Integer> winners) {
        ArrayList<Integer> results=new ArrayList<>();
        for(int w:winners.keySet()){
            int repNum=(winners.get(w));
            if(Data.reps==null){
                results.add(w);
            }else{
                ArrayList<Integer> repList=new ArrayList<Integer>(Data.reps.get(w));
                for(int i=0;i<repNum;i++){
                    int index=getRandomNumber(0,repList.size());
                    results.add(repList.remove(index));
                    
                }
            }
            
        }
        return results;
    }
    /**
     * adds all candidates left in the candidate pool to winners set
     * @param winners - map of meta winners id and the number of copies they have
     */
    private void addRestOfCandidates(HashMap<Integer, Integer> winners) {
        for(Item e:Globals.items){
            if(e.isCandidate()){
                if(winners.containsKey(e.id))
                    winners.put(e.id, winners.get(e.id)+e.canRepNum);
                else{
                    winners.put(e.id,e.canRepNum);
                }
                numOfWinners++;
            }
        }
    }
    /**
     * addes leader id to winners map
     * @param winners - map of meta winners id and the number of copies they have
     * @param leader - id of leader candidate to add to winners
     */
    private void addWinner(HashMap<Integer, Integer> winners, int leader) {
        numOfWinners++;
        if(winners.containsKey(leader)){
            winners.put(leader, winners.get(leader)+1);
        }else{
            winners.put(leader,1);
        }
    }
    /**
     * axillary mthod: returns a random number between min and max
     * @param min
     * @param max
     * @return
     */
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    /**
     * goes over all candidates c in top of voter v, and updates that v voted for c
     * @param v - the voter
     */
    private void updateInTopListOf(Item v) {
        if(v.topVote()!=null)
            for(int cand :v.topVote()){
                Globals.items[cand].addInTopListOf(v.id);
                Globals.items[cand].removeNextListOf(v.id);
            }
            updateInNextListOf(v);
    }
    /**
     * goes over all candidates c in next of voter v, and updates that v voted for c in next
     * @param v - the voter
     */
    private void updateInNextListOf(Item v) {
        if(v.nextVote()!=null)
            for(int cand :v.nextVote()){
                Globals.items[cand].addInNextListOf(v.id);
            }
    }
    /**
     * calculates the fraction of vote transfered for each voter that voted for leader
     * @param leader - the leader candidate bing elected
     * @param threshold - the vote count threshold
     * @param leaderInTop - list of voters that have leader in top
     * @param leaderInNext - list of voters that have leader in next
     * @return fraction vote value
     */
    protected float calcFrac(int leader, float threshold, List<Integer> leaderInTop, List<Integer> leaderInNext) {
        int numVoters=0;
        for (int c:leaderInTop) {
            Item v=Globals.items[c];
            numVoters+=v.voteRepNum;
        }
        for (int c:leaderInNext) {
            Item v=Globals.items[c];
            if(v.nextPartOfTop)
                numVoters+=v.voteRepNum;
        }
        float fraction=(Globals.items[leader].getCount()-threshold)/numVoters;
        return fraction;
    }  

}




