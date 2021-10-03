package Basics.KMeans;

import Similarity.SimilarityMeasures;

public class Point {

    public int id;
    protected float[] coordinates;
    private int cluster_number = 0;
    float minDist;

    public Point(int id, float[] coordinates)
    {
        this.id=id;
        this.coordinates=coordinates;
    }
    
    public Point(Point point) {
        this.id=point.id;
        this.coordinates=point.coordinates;
    }

    public void setCluster(int n) {
        this.cluster_number = n;
    }
    
    public int getCluster() {
        return this.cluster_number;
    }
    
    //Calculates the distance between two points.
    protected static float distance(Point p, Point centroid) {
        return SimilarityMeasures.sim(p.coordinates, centroid.coordinates);
    }
    
    // //Creates random point
    // protected static Point createRandomPoint(int min, int max) {
    // 	Random r = new Random();
    // 	double x = min + (max - min) * r.nextDouble();
    // 	double y = min + (max - min) * r.nextDouble();
    // 	return new Point(-1,x,y);
    // }
    
    // protected static List<Point> createRandomPoints(int min, int max, int number) {
    // 	List<Point> points = new ArrayList<Point>(number);
    // 	for(int i = 0; i < number; i++) {
    // 		points.add(createRandomPoint(min,max));
    // 	}
    // 	return points;
    // }
    
    public String toString() {
    	return "("+id+")";
    }
}