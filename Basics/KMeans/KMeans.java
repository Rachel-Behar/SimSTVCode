package Basics.KMeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeans {
    private int NUM_CLUSTERS;   
    private int MAX_ITERATIONS=10; 
    
    private List<Point> points;
    private List<Cluster> clusters;
    
    public ArrayList<Integer> kMeans(int k){
        this.points = new ArrayList<Point>();
    	this.clusters = new ArrayList<Cluster>(); 
        init(k);
        calculate();
        return getRepsFromClusters();
    }
    // public static void main(String[] args) {
    	
    // 	KMeans kmeans = new KMeans();
    // 	kmeans.init(10);
    // 	kmeans.calculate();
    // }
    private void init(int k) {
        this.NUM_CLUSTERS=k;
        getPointsFromData();
        initClusters();
    }
    //Initializes the process
    // public void init() {
    // 	//Create Points
    // 	points = Point.createRandomPoints(MIN_COORDINATE,MAX_COORDINATE,NUM_POINTS);
    	
    // 	//Create Clusters
    // 	//Set Random Centroids
    // 	for (int i = 0; i < NUM_CLUSTERS; i++) {
    // 		Cluster cluster = new Cluster(i);
    // 		Point centroid = Point.createRandomPoint(MIN_COORDINATE,MAX_COORDINATE);
    // 		cluster.setCentroid(centroid);
    // 		clusters.add(cluster);
    // 	}
    	
    // 	//Print Initial state
    // 	plotClusters();
    // }

	private void initClusters() {
        for (int i = 0; i < NUM_CLUSTERS; i++) {
     		Cluster cluster = new Cluster(i);
     		Point centroid = new Point(points.get(getRandomIndex()));
    		cluster.setCentroid(centroid);
    		clusters.add(cluster);
    	}
    }
    protected static int getRandomIndex() {
        Random random = new Random();
        return random.nextInt(Data.Data.size());
    }
    private void getPointsFromData() {
        for(int i=0;i<Data.Data.values.size();i++){
            points.add(new Point(i,Data.Data.values.get(i)));
        }
    }

    private void plotClusters() {
    	for (int i = 0; i < NUM_CLUSTERS; i++) {
    		Cluster c = clusters.get(i);
    		c.plotCluster();
    	}
    }
    
	//The process to calculate the K Means, with iterating method.
    private void calculate() {
        boolean finish = false;
        int iteration = 0;
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(!finish && iteration<MAX_ITERATIONS) {
        	//Clear cluster state
        	clearClusters();
        	
        	List<Point> lastCentroids = getCentroids();
        	
        	//Assign points to the closer cluster
        	assignCluster();
            // plotClusters();
            //Calculate new centroids.
        	calculateCentroids();
        	
        	iteration++;
        	
        	List<Point> currentCentroids = getCentroids();
        	
        	//Calculates total distance between new and old Centroids
        	double distance = 0;
        	for(int i = 0; i < lastCentroids.size(); i++) {
        		distance += Point.distance(lastCentroids.get(i),currentCentroids.get(i));
        	}
        	System.out.println("#################");
        	System.out.println("Iteration: " + iteration);
        	// System.out.println("Centroid distances: " + distance);
        	// plotClusters();
        	        	
        	if(distance == 0) {
        		finish = true;
        	}
        }
        getRepsFromClusters();
    }
    
    private ArrayList<Integer> getRepsFromClusters() {
        ArrayList<Integer> result=new ArrayList<Integer>();
        for(Cluster cluster : clusters) {
            float minDist=Float.MAX_VALUE;
            int id=-1;
            for(Point p:cluster.points){
                float currDist=0;
                for(Point p2:cluster.points){
                    currDist+=Point.distance(p, p2);
                }
                if (currDist<minDist){
                    minDist=currDist;
                    id=p.id;
                }
            }
            if(id!=-1){
                result.add(id);
            }else{
                System.out.println("Empty Cluster "+cluster.id);
            }
        }
        return result;
    }

    private void clearClusters() {
    	for(Cluster cluster : clusters) {
    		cluster.clear();
    	}
    }
    
    private List<Point> getCentroids() {
    	List<Point> centroids = new ArrayList<Point>(NUM_CLUSTERS);
    	for(Cluster cluster : clusters) {
    		Point aux = cluster.getCentroid();
    		Point point = new Point(aux.id,aux.coordinates);
    		centroids.add(point);
    	}
    	return centroids;
    }
    
    private void assignCluster() {
        float min = Float.MAX_VALUE; 
        int cluster = 0;                 
        for(Point point : points) {
        	min =Float.MAX_VALUE; 
            for(int i = 0; i < NUM_CLUSTERS; i++) {
            	Cluster c = clusters.get(i);
                float distance = Point.distance(point, c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            point.minDist=min;
            clusters.get(cluster).addPoint(point);
        }
    }
    
    private void calculateCentroids() {
        for(Cluster cluster : clusters) {
            List<Point> list = cluster.getPoints();
            int n_points = list.size();
            if(n_points > 0) {
                float[] center=new float[Data.Data.columns.length];
                for(int i=0;i<Data.Data.columns.length;i++){
                    float sumX=0;
                    for(Point point : list) {
                        sumX += point.coordinates[i];
                    }
                    center[i]=sumX/n_points;
                }
                
                cluster.centroid=new Point(-1,center);
            }
            else{
                System.out.println("empty");
                float maxDist = -1; 
                Point newCenter=null;
                for(Point p:points){
                    if(p.minDist>maxDist){
                        newCenter=p;
                        maxDist=newCenter.minDist;
                    }
                }
                cluster.centroid=newCenter;
                newCenter.minDist=0;
                newCenter.setCluster(cluster.id);
                cluster.points.add(newCenter);
            }
        }
    }
}
