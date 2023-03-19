package askisi2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Data {
	
	static class Record{
        HashMap<String, Double> record;
        Integer clusterNumber;

        public Record(HashMap<String, Double> record){
            this.record = record;
        }

        public void setClusterNumber(Integer clusterNumber) {
            this.clusterNumber = clusterNumber;
        }

        public HashMap<String, Double> getRecord() {
            return record;
        }
    }
	
	private final LinkedList<String> attrNames = new LinkedList<>();
	private final LinkedList<Record> records = new LinkedList<>();
	private static final Random random = new Random();
	public ArrayList<Integer> idxPicked = new ArrayList<Integer>();
	
	public Data(String csvFileName) throws IOException {

        String row;
        try(BufferedReader csvReader = new BufferedReader(new FileReader(csvFileName))) {
            if((row = csvReader.readLine()) != null){
                String[] data = row.split(",");
                Collections.addAll(attrNames, data);
            }

            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");

                HashMap<String, Double> record = new HashMap<>();

                if(attrNames.size() == data.length) {
                    for (int i = 0; i < attrNames.size(); i++) {
                        String name = attrNames.get(i);
                        double val = Double.parseDouble(data[i]); 
                        // (name)0->x(value) , (name)1->y(value)
                        record.put(name, val);
                    }
                } else{
                    throw new IOException("Incorrectly formatted file.");
                }
                // record : (name)0->x(value) , (name)1->y(value)
                records.add(new Record(record));
            }
        }
    }
	public LinkedList<HashMap<String,Double>> newCentroids(int K){
        LinkedList<HashMap<String,Double>> centroids = new LinkedList<>();
        for(int i=0; i<K; i++){
            
        	centroids.add(calculateCentroid(i));
        }
        return centroids;
    }
	
	public HashMap<String, Double> calculateCentroid(int clusterNumber){
        HashMap<String, Double> centroid = new HashMap<>();
        LinkedList<Integer> recsInCluster = new LinkedList<>();
        for(int i=0; i<records.size(); i++){
            var record = records.get(i);
            if(record.clusterNumber == clusterNumber){
                recsInCluster.add(i);
            }
        }
        for(String name : attrNames){
            centroid.put(name, meanOfAttr(name, recsInCluster));
        }
        return centroid;
    }
	public Double meanOfAttr(String attrName, LinkedList<Integer> indices){
        Double sum = 0.0;
        for(int i : indices){
            if(i<records.size()){ 
                sum += records.get(i).getRecord().get(attrName);
            }
        }
        return sum / indices.size();
    }
	public Double calculateTotalSSE(LinkedList<HashMap<String,Double>> centroids){
        Double SSE = 0.0;
        for(int i=0; i<centroids.size(); i++) {
            SSE += calculateClusterSSE(centroids.get(i), i);
        }
        return SSE;
    }
	public Double calculateClusterSSE(HashMap<String, Double> centroid, int clusterNumber){
        double SSE = 0.0;
        for(int i=0; i<records.size(); i++){
            if(records.get(i).clusterNumber == clusterNumber){
                SSE += Math.pow(euclideanDistance(centroid, records.get(i).getRecord()), 2);
            }
        }
        return SSE;
    }
	
	// a: centroid, b : record
	public static Double euclideanDistance(HashMap<String, Double> a, HashMap<String, Double> b){
        double sum = 0.0;
        // runs 1-1 the attributes and with getter takes the values
        for(String attrName : a.keySet()){
            sum += Math.pow(a.get(attrName) - b.get(attrName), 2);
        }
        double dist = Math.sqrt(sum);
        return dist;
    }
	
	public HashMap<String, Double> randomCentroidFromDataSet(){
        // find random index and assign it as the new index of the centroid
		int index = random.nextInt(records.size());
		// check if our random centroid from the dataset has been already picked
		while(idxPicked.contains(index)) {
			index = random.nextInt(records.size());	
		}
		idxPicked.add(index);
        return records.get(index).getRecord();
    }
	
	public LinkedList<Record> getRecords() {
        return records;
    }
	
	public void createCsvOutput(String outputFileName,LinkedList<HashMap<String,Double>> centroids){
		int idxCentroid = 0;
        try(BufferedWriter csvWriter = new BufferedWriter(new FileWriter(outputFileName))) {
            for(int i=0; i<attrNames.size(); i++){
                csvWriter.write(attrNames.get(i));
                csvWriter.write(",");
            }
            csvWriter.write("ClusterId");
            csvWriter.write(",");
            // for centroids columns
            csvWriter.write("centroidx");
            csvWriter.write(",");
            csvWriter.write("centroidy");
            csvWriter.write("\n");

            for(var record : records){
                for(int i=0; i<attrNames.size(); i++){
                    csvWriter.write(String.valueOf(record.getRecord().get(attrNames.get(i))));
                    csvWriter.write(",");
                }
                csvWriter.write(String.valueOf(record.clusterNumber));
                
                // keep centroids
	            if(idxCentroid < centroids.size()) {
                	for(int i=0; i<attrNames.size(); i++){
                		csvWriter.write(",");
                		csvWriter.write(String.valueOf(centroids.get(idxCentroid).get(attrNames.get(i))));
	                }
                	idxCentroid++;
	            }
                csvWriter.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}