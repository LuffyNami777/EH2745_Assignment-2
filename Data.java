package assignment2;

public class Data {
	
	Double volt[];
	Double phase[];
	Double time;
	String names[];
	Double voltAverage;
	Double phaseAverage;
	int	cluster;
	
	public Data(Double timeStep, Double[] voltage, Double[] phases, String[] namesOfValues) {
		this.time = timeStep;
		this.volt = voltage;
		this.phase = phases;
		this.names = namesOfValues;
		this.voltAverage = averageVolt(voltage,phases);
		this.phaseAverage = averagePhase(voltage,phases);
	}
	
	
	public static Double averageVolt(Double[] voltage, Double[] phases) {
		
		Complex finalVolt = new Complex(0,0);
		
		for (int i=0 ; i<voltage.length ; i++) {
			
			Double radPhase = Math.toRadians(phases[i]);
			Double realVolt = voltage[i]*Math.cos(radPhase);
			Double imagVolt = voltage[i]*Math.sin(radPhase);			
			Complex voltBus = new Complex(realVolt,imagVolt);
			
			finalVolt = finalVolt.plus(voltBus);
			
			if(i+1 == voltage.length) {
				Complex buses = new Complex(voltage.length,0);
				finalVolt = finalVolt.divides(buses);
			}
		}
		
		Double Voltage = Math.sqrt((finalVolt.re()*finalVolt.re())+(finalVolt.im()*finalVolt.im()));
		
		return Voltage;
	}
	
	public static Double averagePhase(Double[] voltage, Double[] phases) {
		
		Complex finalVolt = new Complex(0,0);
		
		for (int i=0 ; i<voltage.length ; i++) {
			
			Double radPhase = Math.toRadians(phases[i]);
			Double realVolt = voltage[i]*Math.cos(radPhase);
			Double imagVolt = voltage[i]*Math.sin(radPhase);			
			Complex voltBus = new Complex(realVolt,imagVolt);
			
			finalVolt = finalVolt.plus(voltBus);
			
			if(i+1 == voltage.length) {
				Complex buses = new Complex(voltage.length,0);
				finalVolt = finalVolt.divides(buses);
			}
		}
		
		Double phase = Math.toDegrees(Math.atan(finalVolt.im()/finalVolt.re()));
		
		return phase;
	}

}
