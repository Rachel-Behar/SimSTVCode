package Data;

import java.io.*;
import java.util.*;

public class Graph {

    private HashSet<Integer> nodes;
    private HashMap<Integer, HashSet<Integer>> edges;
    private HashMap<Integer, HashSet<Integer>> secondN;
    private HashMap<Integer, HashSet<Integer>> thirdN;
    int numEdges = 0;

    public Graph(HashSet<Integer> nodes, HashMap<Integer, HashSet<Integer>>  edges) {

        this.nodes = nodes;
        this.edges = edges;
        numEdges = 0;
        for (Integer x : nodes) {
            numEdges += edges.get(x).size();
        }
        numEdges = numEdges / 2;
    }

    public Graph(String filename) throws IOException {
	     
		nodes = new HashSet<>();
        edges = new HashMap<>();
        String[] edge;

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        while (line != null) {
            edge = line.split("\\s+");
            int x = Integer.parseInt(edge[0]);
            int y = Integer.parseInt(edge[1]);

            //assigning edge to null ensures garbage collection, and has been proven important in practice
            edge = null;

            if (x != y) {

                HashSet<Integer> xneighbors = edges.get(x);
                HashSet<Integer> yneighbors = edges.get(y);
                if (xneighbors == null) {
                    nodes.add(x);
                    xneighbors = new HashSet<>();
                    edges.put(x, xneighbors);
                }
                if (yneighbors == null) {
                    nodes.add(y);
                    yneighbors = new HashSet<>();
                    edges.put(y, yneighbors);
                }
                xneighbors.add(y);
                yneighbors.add(x);
            }
            line = reader.readLine();
        }
        reader.close();
        for (Integer x: nodes) {
            numEdges += edges.get(x).size();
        }
        numEdges = numEdges / 2;
        fillSecN();
        // for(int n:secondN.keySet()){
        //     System.out.println(n+":"+secondN.get(n));
        // }
        fillthirdN();
        // for(int n:thirdN.keySet()){
        //     System.out.println(n+":"+thirdN.get(n));
        // }
   }

 
    private void fillSecN() {
        secondN=new HashMap<Integer, HashSet<Integer>>();
        for(int n:nodes){
            HashSet<Integer> secN=new HashSet<Integer>();
            for(int n2:edges.get(n)){
                for(int n3:edges.get(n2)){
                    if(n!=n3 && !edges.get(n).contains(n3)){
                        secN.add(n3);
                    }
                }
            }
            secondN.put(n, secN);
        }
    }

    private void fillthirdN() {
        thirdN=new HashMap<Integer, HashSet<Integer>>();
        for(int n:nodes){
            HashSet<Integer> thN=new HashSet<Integer>();
            for(int n2:secondN.get(n)){
                for(int n3:edges.get(n2)){
                    if(n!=n3 && !edges.get(n).contains(n3) && !secondN.get(n).contains(n3)){
                        thN.add(n3);
                    }
                }
            }
            thirdN.put(n, thN);
        }
    }

    public int getNumEdges() {
        return numEdges;
    }

    public int getNumNodes() {
        return nodes.size();
    }

 
    public HashSet<Integer> getNeighbors(int node) {
        HashSet<Integer > neighbors=edges.get(node);
        if(neighbors==null)
            return new HashSet<Integer>();
        return neighbors;
    }

    public HashSet<Integer> getNodes() {

        return new HashSet<>(nodes);
    }
    // public int getDist(int x,int y){
    //     if(x==y)
    //         return 0;
    //     HashSet<Integer> gotTo=new HashSet<Integer>();
    //     gotTo.add(x);
    //     for(int dist=1;dist<=3;dist++){
    //         HashSet<Integer> newGotTo=new HashSet<Integer>();
    //         for(int n:gotTo){
    //             if(getNeighbors(n)!=null)
    //                 newGotTo.addAll(getNeighbors(n));
    //         }
    //         if(newGotTo.contains(y))
    //             return dist;
    //         gotTo=newGotTo;
    //     }
    //     return -1;
    // }
    public int getDist(int x,int y){
        if(edges.get(x) == null){
            return -1;
        }
        if(x==y)
            return 0;
        if(edges.get(x).contains(y))
            return 1;
        if(secondN.get(x).contains(y))
            return 2;
        if(thirdN.get(x).contains(y))
            return 3;
        return -1;
    }
    @Override
    public String toString() {
        return "Graph{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                '}';
    }
}