package assignment2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

public class Clustering {
	
	static ArrayList<Double> timeList = new ArrayList<>(); 
	static ArrayList<String> nameValues = new ArrayList<>();
	static ArrayList<Double> values = new ArrayList<>(); 
	static ArrayList<String> subIDs = new ArrayList<>();
	static int namePos,valuePos,timePos,subIDpos;
	static Double maxVolt, minVolt, maxPhase, minPhase;
	static Double centroids[][] = new Double[4][2];
	static ArrayList<ArrayList<Normalization>> clus = new ArrayList<ArrayList<Normalization>>(); 
	
	
	
	public static ArrayList<Data> measurementsCreation (ArrayList<Double> listTime, ArrayList<String> listNames, ArrayList<Double> listValues, ArrayList<String> substations) {
		
		int numberOfValues = 0;
		int numberofMeasurements = 0;
		ArrayList<Data> measurementsList = new ArrayList<Data>();
		
		for (int i=0 ; i<listTime.size() ; i++) {			
			if (!listTime.get(i).equals(listTime.get(i+1))) {
				numberOfValues = i+1;
				break;
			}
		}
		
		for (int i=0 ; i<listTime.size() ; i++) {
			if (listTime.get(i) > numberofMeasurements) {
				numberofMeasurements = listTime.get(i).intValue();
			}
		}
				
		int value = 0;
				
		for (int i=0 ; i<=numberofMeasurements-1 ; i++) {
			
			Double param[] = new Double[numberOfValues];
			String names[] = new String[numberOfValues];
			String sub[] = new String[numberOfValues];
			Double volt[] = new Double[(numberOfValues+1)/2];
			Double phases[] = new Double[(numberOfValues+1)/2];
			int bus = 0;
			
			Double time = listTime.get(value);
			
			for (int j=0 ; j<numberOfValues ; j++) {
				
				param[j] = listValues.get(value);
				names[j] = listNames.get(value);
				sub[j] = substations.get(value);
				
				value++;
				
				if(j>0 && sub[j].equals(sub[j-1])) {
					volt[bus] = param[j-1];
					phases[bus] = param[j];
					
					bus++;					
				}
				
				if (j+1==numberOfValues) { 	
					Data mes = new Data(time, volt, phases, names);		
					measurementsList.add(mes);
				}				
			}			
		}
		
		return measurementsList;
	}
	
    public static void Kmeans_Cluster(ArrayList<Normalization> normList, ArrayList<Data> measurements) {
		
		Double old_centroids[][] = new Double[4][2];
		Double diff[] = new Double[4];
		Double dist[] = new Double[4];
		Double deltaV = 0.00001;
		
		while(true) {
			
			// Determine the new clusters of the different measurements
			for (int i=0; i<4; i++){
				clus.get(i).clear();
			}
			

			for (int i=0 ; i<normList.size() ; i++) {
				
			//Calculate the Euclidean distance from i to each cluster j(1 to 4)
				for (int j=0 ; j<4 ; j++) {
					
					dist[j] = Math.sqrt(((centroids[j][0] - normList.get(i).volt)*(centroids[j][0] - normList.get(i).volt)) + 
							((centroids[j][1] - normList.get(i).phase)*(centroids[j][1] - normList.get(i).phase)));
				}
				Double min_v = 999999999.99;
				int min_id = -1;
			
			//Assign each i to the closest cluster with the least distance
				for (int p=0; p<4; p++) {
					if (dist[p] < min_v) {
						min_v = dist[p];
						min_id = p;
					}
				}
				clus.get(min_id).add(normList.get(i));
				
			}
			
			old_centroids = centroids;
			
			newcentroids(); // Determine new position of the clusters
			
			
		    //Calculate the centroids movement
			for(int j=0 ; j<4 ; j++) {
				
				diff[j] =  Math.sqrt(((centroids[j][0] - old_centroids[j][0])*(centroids[j][0] - old_centroids[j][0])) + 
						((centroids[j][1] - old_centroids[j][1])*(centroids[j][1] - old_centroids[j][1])));
				
			}
			
			//If the movement is less than a threshold, stop
			
			if (diff[0]<=deltaV && diff[1]<=deltaV && diff[2]<=deltaV && diff[3]<=deltaV) {
				break;
			}			
			
		}
		
	
		denormalize();
		
		for (int j = 0; j< 4; j++)
			System.out.println("Cluster "+(j+1)+ " has " + clus.get(j).size());

		
		for(int j=0 ; j<measurements.size() ; j++) {
			for (int c = 0; c < 4; c++) {
				for(int i=0 ; i<clus.get(c).size() ; i++) {
					
					if(measurements.get(j).time == clus.get(c).get(i).time) {
						measurements.get(j).cluster = c;
					break;
					}	
				}
			}
			
		}
		
	}
	
	public static ArrayList<Normalization> normalize(ArrayList<Data> measurements) {
		
		//Set the boundaries
		maxVolt = 0.0;
		minVolt =  1.0;
		maxPhase = -180.0;
		minPhase = 180.0;
		
		ArrayList<Normalization> normList = new ArrayList<>();
		
		//Bound the limits
		for(int i=0 ; i<measurements.size() ; i++) {
			if (measurements.get(i).voltAverage < minVolt) {
				minVolt = measurements.get(i).voltAverage;
			}
			
			if (measurements.get(i).voltAverage > maxVolt) {
				maxVolt = measurements.get(i).voltAverage;	
			}
			
			if (measurements.get(i).phaseAverage < minPhase) {
				minPhase = measurements.get(i).phaseAverage;
			}
			
			if (measurements.get(i).phaseAverage > maxPhase) {
				maxPhase = measurements.get(i).phaseAverage;
			}
		}
		
		//Normalize the phase, voltage values to[0,1]
		for(int i=0 ; i<measurements.size() ; i++) {
			Double normPhase = (measurements.get(i).phaseAverage - minPhase)/(maxPhase - minPhase);
			Double normVolt = (measurements.get(i).voltAverage - minVolt)/(maxVolt - minVolt);
			
			Normalization meas = new Normalization(measurements.get(i).time,normVolt,normPhase);
			normList.add(meas);
		}
		
		return normList;
		
	}	
	 
	
	//Initialize the 4 centroids
	public static void intialize(ArrayList<Normalization> normList) {
		
		Double dist[] = new Double[4];
		Double distance = 0.0;

		
		// The 1st centroid for the Clusters 
		centroids[0][0] = 0.5;
		centroids[0][1] = 0.5;
		
		for (int i=0 ; i<normList.size() ; i++) {
			
			Double newdistance = Math.sqrt(((centroids[0][0] - normList.get(i).volt)*(centroids[0][0] - normList.get(i).volt)) + 
					((centroids[0][1] - normList.get(i).phase)*(centroids[0][1] - normList.get(i).phase)));
		
		//The 2nd centroid: the furtherest point from the 1st centroid
			
			if (newdistance>distance) {
				distance = newdistance;
				centroids[1][0] = normList.get(i).volt;
				centroids[1][1] = normList.get(i).phase;				
			}			
		}
		
		distance = 0.0;
		
		for (int i=0 ; i<normList.size() ; i++) {
			
			Double newdistance0 = Math.sqrt(((centroids[0][0] - normList.get(i).volt)*(centroids[0][0] - normList.get(i).volt)) + 
					((centroids[0][1] - normList.get(i).phase)*(centroids[0][1] - normList.get(i).phase)));
			Double newdistance1 = Math.sqrt(((centroids[1][0] - normList.get(i).volt)*(centroids[1][0] - normList.get(i).volt)) + 
					((centroids[1][1] - normList.get(i).phase)*(centroids[1][1] - normList.get(i).phase)));
			Double newdistance = (newdistance0 + newdistance1)/2; 
		
		//The 3rd centroid: Further from the 1st and 2nd centroids
			if (newdistance>distance) {
				distance = newdistance;
				centroids[2][0] = normList.get(i).volt;
				centroids[2][1] = normList.get(i).phase;				
			}			
		}
		
		distance = 0.0;
		
		for (int i=0 ; i<normList.size() ; i++) {
			
			Double newdistance0 = Math.sqrt(((centroids[0][0] - normList.get(i).volt)*(centroids[0][0] - normList.get(i).volt)) + 
					((centroids[0][1] - normList.get(i).phase)*(centroids[0][1] - normList.get(i).phase)));
			Double newdistance1 = Math.sqrt(((centroids[1][0] - normList.get(i).volt)*(centroids[1][0] - normList.get(i).volt)) + 
					((centroids[1][1] - normList.get(i).phase)*(centroids[1][1] - normList.get(i).phase)));
			Double newdistance2 = Math.sqrt(((centroids[2][0] - normList.get(i).volt)*(centroids[2][0] - normList.get(i).volt)) + 
					((centroids[2][1] - normList.get(i).phase)*(centroids[2][1] - normList.get(i).phase)));
			Double newdistance = (newdistance0 + newdistance1 + newdistance2)/3; 
		
		//The 4th centroid:Further from the 3 centroids above
			if (newdistance>distance) {
				distance = newdistance;
				centroids[3][0] = normList.get(i).volt;
				centroids[3][1] = normList.get(i).phase;				
			}			
		}
		
		for (int i=0 ; i<normList.size() ; i++) {
			
		//Calculate the Euclidean distance from i to each cluster j
			for (int j=0 ; j<4 ; j++) {
				
				dist[j] = Math.sqrt(((centroids[j][0] - normList.get(i).volt)*(centroids[j][0] - normList.get(i).volt)) + 
						((centroids[j][1] - normList.get(i).phase)*(centroids[j][1] - normList.get(i).phase)));
			}
			
		//Assign the i to the closet cluster j
			Double min_v = 999999999.99;
			int min_id = -1;
			for (int p=0; p<4; p++) {
				if (dist[p] < min_v) {
					min_v = dist[p];
					min_id = p;
				}
			}
			clus.get(min_id).add(normList.get(i));
				
		}
		for (int i =0; i <4; i++)
			System.out.println("Initial Cluster "+(i+1) + " has " + clus.get(i).size());
		
	}
	
	//Obtain the 4 new centroids
	public static void newcentroids() {
		
		Double new_centroids[][] = { {0.0,0.0},{0.0,0.0},{0.0,0.0},{0.0,0.0} };
		for (int c =0; c<4; c++) {
			for (int i=0 ; i<clus.get(c).size() ; i++) {
				
				new_centroids[c][0] += clus.get(c).get(i).volt/clus.get(c).size();
				new_centroids[c][1] += clus.get(c).get(i).phase/clus.get(c).size();
				
			}
		}
		
		centroids = new_centroids;		
	}
	
	//Transfer the normalized per unit values to real values
	public static void denormalize() {
		
		Double final_centroids[][] = { {0.0,0.0},{0.0,0.0},{0.0,0.0},{0.0,0.0} };
		
		
		for (int i=0; i<4; i++){
			final_centroids[i][0] = centroids[i][0]*(maxVolt-minVolt) + minVolt;
			final_centroids[i][1] = centroids[i][1]*(maxPhase-minPhase) + minPhase;
		}
		centroids = final_centroids;
	}
	
	
	
	
	public static void read_data(String dataFile) {
		
		nameValues.clear();
		timeList.clear();
		values.clear();
		subIDs.clear();
		
		BufferedReader br = null;
		String line = "";
		String SplitBy = ",";
	
		try {
		
			br = new BufferedReader(new FileReader(dataFile));
			while ((line = br.readLine()) != null) {
 	     
			String[] measurements = line.split(SplitBy);
			
			if(!measurements[0].equals("rdfid")) { 
				
				nameValues.add(measurements[namePos]);
				timeList.add(Double.parseDouble(measurements[timePos]));
				values.add(Double.parseDouble(measurements[valuePos]));
				subIDs.add(measurements[subIDpos]);
				
			}	else {
				
				for(int i = 0 ; i<measurements.length ; i++) {
					
					switch(measurements[i]) {
					case "name" :
						namePos = i;
						break;
					case "time" :
						timePos = i;
						break;
					case "value" :
						valuePos = i;
						break;
					case "sub_rdfid" :
						subIDpos = i;
						break;						
					}
					
				}
					
			}
	
		}
		} catch (FileNotFoundException e) {
		e.printStackTrace();
	
		} catch (IOException e) {
		e.printStackTrace();
		}		
	}
	
	
	public static void main(String[] args) {
		for(int i = 0;i<4; i++)
			clus.add(new ArrayList<Normalization>());
		// Reading Data (Measurements)
		String inputFile = "measurements.csv";
		read_data(inputFile); //It can be used to read the test data
		
		//List of measurements
		ArrayList<Data> measurementsList = measurementsCreation(timeList,nameValues,values,subIDs);
	
		// Normalizing
		ArrayList<Normalization> normList = normalize(measurementsList); //It can be used to read the test data
		
		// K Means
		intialize(normList);				
		newcentroids();				
		Kmeans_Cluster(normList,measurementsList);
						
		//Reading analog_values data
		String testFile = "analog_values.csv";
		read_data(testFile); //It can be used to read the test data
		
		//Creation of the arraylist from analog_values
		ArrayList<Data> testList = measurementsCreation(timeList,nameValues,values,subIDs);
		
		//Running the KNN Classification
		KNN.KNNClassification(measurementsList, testList);
		
		//Plot the clustering
		Visualization.printScatterPlot("Scatter graph", measurementsList, testList, centroids);
		
	}
}

