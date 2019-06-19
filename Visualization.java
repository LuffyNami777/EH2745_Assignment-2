package assignment2;

import java.util.ArrayList;
import java.text.DecimalFormat;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

// The code to create the scatter plot followed the procedure in https://www.boraji.com/jfreechart-scatter-chart-example	

public class Visualization extends JFrame {
	  private static final long serialVersionUID = 6294689542092367723L;

	  public Visualization(String title, ArrayList<Data> measurementsList, ArrayList<Data> testList, Double[][] centroids, int CID) {
	    super(title);

	    // Create dataset
	    XYDataset dataset = createDataset(measurementsList, testList, centroids, CID);

	    // Create chart
	    JFreeChart chart = ChartFactory.createScatterPlot(
	        "Data - K-means & KNN, Cluster "+CID, 
	        "Average Phase", "Average Voltage", dataset, PlotOrientation.VERTICAL, true, true, false);

	    
	    //Changes background color
	    XYPlot plot = (XYPlot)chart.getPlot();
	    plot.setBackgroundPaint(Color.gray);
	    
	   
	    // Create Panel
	    ChartPanel panel = new ChartPanel(chart);
	    setContentPane(panel);
	  }

	  private XYDataset createDataset(ArrayList<Data> measurementsList, ArrayList<Data> testList, Double[][] centroids, int CID) {
	    XYSeriesCollection dataset = new XYSeriesCollection();

	    //Boys (Age,weight) series
	    XYSeries series1 = new XYSeries("Cluster 1");
	    XYSeries series2 = new XYSeries("Cluster 2");
	    XYSeries series3 = new XYSeries("Cluster 3");
	    XYSeries series4 = new XYSeries("Cluster 4");
	    XYSeries series5 = new XYSeries("Cluster 1 test");
	    XYSeries series6 = new XYSeries("Cluster 2 test");
	    XYSeries series7 = new XYSeries("Cluster 3 test");
	    XYSeries series8 = new XYSeries("Cluster 4 test");
	    XYSeries series9 = new XYSeries("Centroid 1");
	    XYSeries series10 = new XYSeries("Centroid 2");
	    XYSeries series11 = new XYSeries("Centroid 3");
	    XYSeries series12 = new XYSeries("Centroid 4");
	    
	    
	    for (Data measurement: measurementsList){
	    	switch(measurement.cluster){
			case 0:
				series1.add(measurement.phaseAverage, measurement.voltAverage);
				break;
			case 1:
				series2.add(measurement.phaseAverage, measurement.voltAverage);
				break;
			case 2:
				series3.add(measurement.phaseAverage, measurement.voltAverage);
				break;
			case 3:
				series4.add(measurement.phaseAverage, measurement.voltAverage);
				break;
			}
	    }
	    if (CID==1) {
	    	dataset.addSeries(series1);
	    }
	    if (CID==2) 
	    dataset.addSeries(series2);
	    if (CID==3) 
	    dataset.addSeries(series3);
	    if (CID==4) 
	    dataset.addSeries(series4);
	   
	    for (Data testValue: testList){
	    	switch(testValue.cluster){
			case 1:
				series5.add(testValue.phaseAverage, testValue.voltAverage);
				break;
			case 2:
				series6.add(testValue.phaseAverage, testValue.voltAverage);
				break;
			case 3:
				series7.add(testValue.phaseAverage, testValue.voltAverage);
				break;
			case 4:
				series8.add(testValue.phaseAverage, testValue.voltAverage);
				break;
			}
	    }
	    if (CID==1) 
	    	dataset.addSeries(series5);
	    if (CID==2) 
	    dataset.addSeries(series6);
	    if (CID==3) 
	    dataset.addSeries(series7);
	    if (CID==4) 
	    dataset.addSeries(series8);
	    
	    series9.add(centroids[0][1], centroids[0][0]);
	    series10.add(centroids[1][1], centroids[1][0]);
	    series11.add(centroids[2][1], centroids[2][0]);
	    series12.add(centroids[3][1], centroids[3][0]);
	    if (CID==1) 
	    dataset.addSeries(series9);
	    if (CID==2) 
	    dataset.addSeries(series10);
	    if (CID==3) 
	    dataset.addSeries(series11);
	    if (CID==4) 
	    dataset.addSeries(series12);

	    return dataset;
	  }

	  public static void printScatterPlot(String title, ArrayList<Data> measurementsList, ArrayList<Data> testList, Double[][] centroids) {
		  JFrame jf = new JFrame("KNN-Kmeans Clustering");
		  jf.setLayout(new FlowLayout());
		  jf.setVisible(true);
		  jf.setSize(500, 700);
		 
		  //Button 1
		  JButton jb = new JButton("Show Cluster 1");
		  JTextArea textArea = new JTextArea(30,35);
		 
		  DecimalFormat df = new DecimalFormat( "0.00000");
		  jf.add(textArea);
		  textArea.setText("Centroids of 4 clusters:\nCluster 1 center is "+ df.format(centroids[0][0])+" " +df.format(centroids[0][1]) +"\n");
		  textArea.append("Cluster 2 center is "+ df.format(centroids[1][0])+" " +df.format(centroids[1][1]) +"\n");
		  textArea.append("Cluster 3 center is "+ df.format(centroids[2][0])+" " +df.format(centroids[2][1]) +"\n");
		  textArea.append("Cluster 4 center is "+ df.format(centroids[3][0])+" " +df.format(centroids[3][1]) +"\n");
		  for (int i = 0; i < testList.size(); i++) {
			  textArea.append("Test "+ (i+1) + " belongs to cluster " + testList.get(i).cluster+"\n");
			  
		  }
		  jf.add(jb);

			jb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				    SwingUtilities.invokeLater(() -> {
				      Visualization example = new Visualization(title, measurementsList, testList, centroids, 1);
				      example.setSize(500, 300);
				      example.setLocationRelativeTo(null);
				      example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				      example.setVisible(true);
				      
				    });
				}
			});
			
			//Button 2
			JButton jb2 = new JButton("Show Cluster 2");
			jf.add(jb2);
			jb2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				    SwingUtilities.invokeLater(() -> {
				      Visualization example = new Visualization(title, measurementsList, testList, centroids, 2);
				      example.setSize(500, 300);
				      example.setLocationRelativeTo(null);
				      example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				      example.setVisible(true);
				      
				    });
				}
			});
			
			//Button 3
			JButton jb3 = new JButton("Show Cluster 3");
			jf.add(jb3);
			jb3.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				    SwingUtilities.invokeLater(() -> {
				      Visualization example = new Visualization(title, measurementsList, testList, centroids, 3);
				      example.setSize(500, 300);
				      example.setLocationRelativeTo(null);
				      example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				      example.setVisible(true);
				      
				    });
				}
			});

			//Button 4
			JButton jb4 = new JButton("Show Cluster 4");
			jf.add(jb4);
			jb4.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				    SwingUtilities.invokeLater(() -> {
				      Visualization example = new Visualization(title, measurementsList, testList, centroids, 4);
				      example.setSize(500, 300);
				      example.setLocationRelativeTo(null);
				      example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				      example.setVisible(true);
				      
				    });
				}
			});

		  
	  }
}
	
	

