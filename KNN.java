package assignment2;

import java.util.ArrayList;
import java.util.Collections;
import java.text.DecimalFormat;

public class KNN {
	
	public static void KNNClassification(ArrayList<Data> trainingList, ArrayList<Data> testList){
		  
		int K=5;
		
        // KNN for test values
		
		for(Data testValue : testList){
			
			ArrayList<Output> resultList = new ArrayList<Output>();
			
			//Find all the Euclidean distances in the training set
			
			for(Data measurement : trainingList){
				
				double dist = 0.0;
			
				dist= Math.pow((measurement.voltAverage-testValue.voltAverage),2)+Math.pow((measurement.voltAverage-testValue.voltAverage),2);
				
				double distance = Math.sqrt(dist);
		
				resultList.add(new Output(distance, measurement.cluster));
			}
		
			//Sort the distances in ascending order
			Collections.sort(resultList, new DistComparator());

			int cs[] = new int[K];
			
			//Find the k nearest neighbours and the most common label
			
			for(int i=0; i<K; i++){
				int c;
				c=resultList.get(i).cluster;
				cs[c]++;
			
			}
			int max_v = 0;
			int max_id = -1;
			for (int i=0; i<4; i++) {
				if (cs[i] > max_v) {
					max_v = cs[i];
					max_id = i;  //most common label
				}
			}
			testValue.cluster = max_id+1;
			
		}
		
		DecimalFormat df = new DecimalFormat( "0.00000");
		for(int x = 0; x < testList.size(); x++){
			System.out.println("Value: " + df.format(testList.get(x).voltAverage) + "," + df.format(testList.get(x).phaseAverage) + " belongs to " + testList.get(x).cluster);
		}
	}
}
		
		
	
	
	
	