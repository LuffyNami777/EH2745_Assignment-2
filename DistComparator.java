package assignment2;

import java.util.Comparator;

public class DistComparator implements Comparator<Output> {

	@Override
    public int compare(Output a, Output b) {
       return a.distance < b.distance ? -1 : a.distance == b.distance ? 0 : 1;
    }

}
