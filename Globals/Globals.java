package Globals;

import Item.Item;
/**
 * Global variables
 */
public class Globals {
    public static int timeoutInMili=60*60000;//timeout is set to 60 min
    public static long start;//start time in miliseconds
    public static long middle;//time after preproccess
    public static long end;//end time in miliseconds
    public static Item[] items;//array of all items
    /**
     * returns true if given candidate id was not choosen of eliminates yet, else returns false
     * @param candidate - id of candidate
     * @return
     */
    public static boolean isCandidate(int candidate) {
        if(items==null)
            return true;
        return Globals.items[candidate].isCandidate();
    }
    /**
     * given an item e, returns true if top vote is a single non meta candidate
     * @param e - the voter
     * @return
     */
    public static boolean topIsSingle(Item e){
        if(e.topVote().size()>1){
            return false;
        }
        Item topE=getC1(e);
        if(topE.canRepNum>1){
            return false;
        }
        return true;
    }
    /**
     * gets the item in top list of voter e
     * @param e - the voter
     * @return
     */
    private static Item getC1(Item e) {
        return items[e.topVote().iterator().next()];
    }
}
