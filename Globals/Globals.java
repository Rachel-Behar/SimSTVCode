package Globals;

import Entity.Entity;

public class Globals {
    public static int timeoutInMili=60*60000;
    public static long start;
    public static long middle;
    public static long end;
    public static Entity[] entities;
    public static boolean isCandidate(int candidate) {
        if(entities==null)
            return true;
        return Globals.entities[candidate].isCandidate();
    }
    public static boolean topIsSingle(Entity e){
        if(e.topVote().size()>1){
            return false;
        }
        Entity topE=getC1(e);
        if(topE.canRepNum>1){
            return false;
        }
        return true;
    }
    private static Entity getC1(Entity e) {
        return entities[e.topVote().iterator().next()];
    }
}
