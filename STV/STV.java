package STV;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import AttributeRanker.AttributeRanker;
import AttributeRanker.NumericAttRankerTree;
import Data.Data;
import Entity.Entity;
import Globals.Globals;
import Entity.ColumnRankers;


public class STV {

    protected TreeSet<QEntryMax> argMax;
    protected TreeSet<QEntryMin> argMin;
    boolean printProgress;
    boolean printStats;
    int eliminated=0;
    public static int numOfCurrCan=0;
    public static int intervalSize=10;
    int numOfWinners=0;

    public static int numCandidates() {
        return numOfCurrCan;
    }
   
    public STV(Entity[] entites,  boolean printProgress,  boolean printStats){
        argMax=new TreeSet<>();
        argMin=new TreeSet<>();
        this.printProgress=printProgress;
        this.printStats=printStats;
        init(entites);
        numOfCurrCan=Data.size();
    }

    protected void init(Entity[] entities) {
        Globals.entities=entities;
        
        for (Entity v:entities){
            v.setVoteValue(1);
            for (int c :v.topVote()){
                Entity can=entities[c];
                can.setCount(can.getCount()+v.getVoteValue());
                can.addInTopListOf(v.id);
            }
            if(v.nextVote()!=null)
                for (int c :v.nextVote()){
                    Entity can=entities[c];
                    if(v.nextPartOfTop){
                        can.setCount(can.getCount()+v.getVoteValue());
                    }
                    can.addInNextListOf(v.id);
                }
                if(Globals.topIsSingle(v)){
                    v.setLoss((v.topSim-v.nextSim)*v.getVoteValue());
                }
        }
        
        for (Entity v:entities){
            argMax.add(new QEntryMax(v.id,v.getCount()));
            argMin.add(new QEntryMin(v.id,v.getCount(),v.getLoss()));
        }
    }

    public ArrayList<Integer> runSTV(int k){
        if(this.printStats){
            System.out.println("#winners\t#eliminated\t#candidates\tsec");
        }
        Globals.middle=System.currentTimeMillis();
        long timeout=Globals.start+Globals.timeoutInMili;
        HashMap<Integer,Integer> winners=new HashMap<Integer,Integer>();
        numOfWinners=0;
        eliminated=0;
        float threshold = calcThreshold(numOfWinners,k);
        while(numOfWinners<k && Data.originalValues.size()-eliminated>k){
            if(System.currentTimeMillis()>timeout){
                return null;
            }
            int leader=getLeader(threshold);
            if (leader!=-1){
                if(this.printProgress){
                    System.out.println("elected "+leader + " surplus="+(Globals.entities[leader].getCount()-threshold)+" threshold="+threshold);
                }
                transferSurplus(leader,threshold);
                addWinner(winners, leader);
                if(this.printStats){
                    printStats();
                 }
            }
            else{
                int loser=getLoser();
                Iterator<QEntryMin> it = argMin.iterator();
                it.next();
                // int loser2=it.next().id;
                // if(Globals.entities[loser].getCount()==Globals.entities[loser2].getCount()&&Globals.entities[loser].getLoss()!=Globals.entities[loser2].getLoss()){
                //     System.out.println(loser+":"+Globals.entities[loser].getLoss()+","+loser2+":"+Globals.entities[loser2].getLoss());
                // }
                if(this.printProgress){
                    System.out.println(eliminated+" eliminated " + loser+" votes="+Globals.entities[loser].getCount()+"canRep="+Globals.entities[loser].canRepNum);
                }
                eliminated+=Globals.entities[loser].canRepNum;
                eliminateCandidate(loser);
                // eliminated++;
                    
            }
        }
        if(numOfWinners<k){
            addRestOfCandidates(winners);
            
        }
        ArrayList<Integer> results = getListOfWinnersByOriginalID(winners);
        return results;
    }

    protected float calcThreshold(int n,int k) {
        return (float) (Math.floor(Data.originalValues.size()/(k+1))+1);
    }

    protected boolean removeSingleCandidate(int id) {
        if(Globals.entities[id].removeSingle()){
            numOfCurrCan--;
            argMin.remove(new QEntryMin(id, Globals.entities[id].getCount(), Globals.entities[id].getLoss()));
            argMax.remove(new QEntryMax(id, Globals.entities[id].getCount()));
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
    protected void removeMultiCandidate(int id) {
        Entity e=Globals.entities[id];
        e.removeMulti();
        numOfCurrCan--;
        argMin.remove(new QEntryMin(id, e.getCount(), e.getLoss()));
        argMax.remove(new QEntryMax(id, e.getCount()));
        AttributeRanker[] rankers = ColumnRankers.rankers;
        for (int i = 0 ; i < Data.types.length ; i++) {
            if (Data.types[i] == 1) {
                NumericAttRankerTree attRanker = (NumericAttRankerTree) rankers[i];
                attRanker.removeCandidate(id);
            }
        }
    }
    protected void eliminateCandidate(int loser) {
        removeMultiCandidate(loser);
        float[] valuesChanged = new float[Data.size()];
        float[] lossChanged = new float[Data.size()];
        HashSet<Integer> candidatesChanged=new HashSet<>();
        List<Integer> loserInNext=Globals.entities[loser].inNextList();
        for (int c:loserInNext) {
            Entity v=Globals.entities[c];
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
        List<Integer> loserInTop=Globals.entities[loser].inTopList();
        for (int c:loserInTop) {
            Entity v=Globals.entities[c];
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
        for (int id:candidatesChanged){
            Entity e= Globals.entities[id];
            argMin.remove(new QEntryMin(id, e.getCount(), e.getLoss()));
            argMax.remove(new QEntryMax(id, e.getCount()));
            e.setCount(e.getCount()+valuesChanged[id]);
            e.setLoss(e.getLoss()+lossChanged[id]);
            argMin.add(new QEntryMin(id, e.getCount(),e.getLoss()));
            argMax.add(new QEntryMax(id,e.getCount()));
        }
    }
    // protected void eliminateCandidate(int loser) {
    //     if(removeSingleCandidate(loser)){
    //         float[] valuesChanged = new float[Data.size()];
    //         float[] lossChanged = new float[Data.size()];
    //         HashSet<Integer> candidatesChanged=new HashSet<>();
    //         List<Integer> loserInNext=Globals.entities[loser].inNextList();
    //         for (int c:loserInNext) {
    //             Entity v=Globals.entities[c];
    //             v.nextVote().remove(loser);
    //             if(v.nextVote().isEmpty()){
    //                 v.updateNext();
    //                 updateInNextListOf(v);
    //                 if(Globals.topIsSingle(v)){
    //                     int id=v.topVote().iterator().next();
    //                     lossChanged[id]-=v.votePotentialLoss*v.getVoteValue();
    //                     v.votePotentialLoss=v.topSim-v.nextSim;
    //                     lossChanged[id]+=v.votePotentialLoss*v.getVoteValue();
    //                 }else{
    //                     v.votePotentialLoss=0;
    //                 }
    //             } 
    //         }
    //         List<Integer> loserInTop=Globals.entities[loser].inTopList();
    //         for (int c:loserInTop) {
    //             Entity v=Globals.entities[c];
    //             v.topVote().remove(loser);
    //             if (v.topVote().isEmpty()) {
    //                 v.popVote();
    //                 updateInTopListOf(v);
    //                 if(v.topVote()!=null){
    //                     for (int id: v.topVote()) {
    //                         valuesChanged[id] += v.getVoteValue();
    //                         candidatesChanged.add(id);
    //                     }
    //                 }
    //             }
    //             if(!v.topVote().isEmpty()&& Globals.topIsSingle(v)){
    //                 int id=v.topVote().iterator().next();
    //                 v.votePotentialLoss=v.topSim-v.nextSim;
    //                 lossChanged[id]+=v.votePotentialLoss*v.getVoteValue();
    //             }else{
    //                 v.votePotentialLoss=0;
    //             }
    //         }
    //         for (int id:candidatesChanged){
    //             Entity e= Globals.entities[id];
    //             argMin.remove(new QEntryMin(id, e.getCount(), e.getLoss()));
    //             argMax.remove(new QEntryMax(id, e.getCount()));
    //             e.setCount(e.getCount()+valuesChanged[id]);
    //             e.setLoss(e.getLoss()+lossChanged[id]);
    //             argMin.add(new QEntryMin(id, e.getCount(),e.getLoss()));
    //             argMax.add(new QEntryMax(id,e.getCount()));
    //         }
    //     }
    // }

    protected void transferSurplus(int leader, float threshold) {
        boolean deleted=removeSingleCandidate(leader);
        float[] valuesChanged = new float[Data.size()];
        float[] lossChanged = new float[Data.size()];
        HashSet<Integer> changedCandidates = new HashSet<>();
        List<Integer> LeaderInTop=Globals.entities[leader].inTopList();
        List<Integer> leaderInNext=Globals.entities[leader].inNextList();
        float fraction = calcFrac(leader, threshold, LeaderInTop,leaderInNext);
        for (int c:leaderInNext) {
            Entity v=Globals.entities[c];
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
        
        for (int c:LeaderInTop) {
            Entity v=Globals.entities[c];
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
        for (int id: changedCandidates){
            Entity e= Globals.entities[id];
            argMin.remove(new QEntryMin(id, e.getCount(),e.getLoss()));
            argMax.remove(new QEntryMax(id, e.getCount()));
            e.setCount(e.getCount()+valuesChanged[id]);
            e.setLoss(e.getLoss()+lossChanged[id]);
            argMin.add(new QEntryMin(id, e.getCount(),e.getLoss()));
            argMax.add(new QEntryMax(id,e.getCount()));
        }
    }
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

    private void addRestOfCandidates(HashMap<Integer, Integer> winners) {
        if(this.printProgress){
            System.out.println("added rest of candidates ");
        }
        for(Entity e:Globals.entities){
            if(e.isCandidate()){
                if(winners.containsKey(e.id))
                    winners.put(e.id, winners.get(e.id)+e.canRepNum);
                else{
                    winners.put(e.id,e.canRepNum);
                }
                numOfWinners++;
                if(this.printProgress){
                    System.out.print(e.id+":"+e.getCount()+",");
                }
            }
        }
        if(this.printStats){
            Globals.end = System.currentTimeMillis();
            NumberFormat formatter = new DecimalFormat("#0.00000");
            System.out.println(numOfWinners +" \t\t"+eliminated+ "\t\t" + (Data.originalValues.size()-eliminated-numOfWinners)+"\t\t"+formatter.format((Globals.end - Globals.middle) / 1000d));
        }
        if(this.printProgress){
            System.out.println();
        }
    }

    private void printStats() {
        if(numOfWinners%intervalSize==0){
            Globals.end = System.currentTimeMillis();
            NumberFormat formatter = new DecimalFormat("#0.00000");
            System.out.println(numOfWinners +" \t\t"+eliminated+ "\t\t" + (Data.originalValues.size()-eliminated-numOfWinners)+"\t\t"+formatter.format((Globals.end - Globals.middle) / 1000d));
            Globals.middle = System.currentTimeMillis();
        }
    }

    private void addWinner(HashMap<Integer, Integer> winners, int leader) {
        numOfWinners++;
        if(winners.containsKey(leader)){
            winners.put(leader, winners.get(leader)+1);
        }else{
            winners.put(leader,1);
        }
    }
    
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
  
    private int getLoser() {
        QEntryMin loser=argMin.first();
        return loser.id;
    }
    /**
     * return candidate id with maximal vCount if the maximal vCount passes the threshold, else, returns -1
     */
    protected int getLeader(float threshold){
        QEntryMax leader=argMax.last();
        if (leader.vCount>=threshold) {
            return leader.id;
        }
        return -1;
    }

    private void updateInTopListOf(Entity v) {
        if(v.topVote()!=null)
            for(int cand :v.topVote()){
                Globals.entities[cand].addInTopListOf(v.id);
                Globals.entities[cand].removeNextListOf(v.id);
            }
            updateInNextListOf(v);
    }

    private void updateInNextListOf(Entity v) {
        if(v.nextVote()!=null)
            for(int cand :v.nextVote()){
                Globals.entities[cand].addInNextListOf(v.id);
            }
    }

    protected float calcFrac(int leader, float threshold, List<Integer> leaderInTop, List<Integer> leaderInNext) {
        int numVoters=0;
        for (int c:leaderInTop) {
            Entity v=Globals.entities[c];
            numVoters+=v.voteRepNum;
        }
        for (int c:leaderInNext) {
            Entity v=Globals.entities[c];
            if(v.nextPartOfTop)
                numVoters+=v.voteRepNum;
        }
        float fraction=(Globals.entities[leader].getCount()-threshold)/numVoters;
        return fraction;
    }  

}




