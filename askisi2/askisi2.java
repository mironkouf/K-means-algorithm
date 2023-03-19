package askisi2;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class askisi2 {
    private static final Double PRECISION = 0.0;
    private static final int M = 12;// omades/kentra

    private static LinkedList<HashMap<String,Double>> centroids = new LinkedList<>();
    
    public static double kmeans(Data data, int K){
    	Double SSE = Double.MAX_VALUE; // Initialize error with Java max value 
    	// select K initial centroids
    	for(int i=0; i<K;i++) {
    		centroids.add(data.randomCentroidFromDataSet()); //initializes K centroids from data
    	}
        while (true) {
            // assign the LinkedList with all the objects Records
            var records = data.getRecords();
            // for each record (in the LinkedList)
            for(var record : records){
                Double minDist = Double.MAX_VALUE;
                for(int i=0; i<centroids.size(); i++){
                	// find the minimum Euclidean distance from every centroid
                    Double dist = Data.euclideanDistance(centroids.get(i), record.getRecord());
                    if(dist<minDist){
                        minDist = dist;
                        // Then assign the record to the cluster it belongs
                        record.setClusterNumber(i);
                    }
                }
            }
            // New centroids after new cluster assignments 
            centroids = data.newCentroids(K);
            Double newSSE = data.calculateTotalSSE(centroids);
            if(SSE-newSSE <= PRECISION){
            	System.out.println("error = "+SSE);
            	break;
            }
            // update error with the new error
            SSE = newSSE;
        }
        return SSE;
    }
	public static void main(String[] args){
		try {
            // read data
            Data data = new Data("sample_2_cols.csv");
            kmeans(data, M);
            data.createCsvOutput("sampleClustered.csv",centroids);

        } catch (IOException e){
            e.printStackTrace();
        }
	}
}