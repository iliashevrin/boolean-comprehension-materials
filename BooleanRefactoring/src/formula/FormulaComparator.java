package formula;

import java.util.Comparator;

import metrics.Metric;
import metrics.NegationCount;
import metrics.NegationLevel;
import metrics.OperatorCount;
import metrics.OperatorNesting;

public class FormulaComparator implements Comparator<Formula> {
	
	private Metric metric;
	
	public FormulaComparator(Metric metric) {
		this.metric = metric;
	}

	@Override
	public int compare(Formula o1, Formula o2) {
		
		int compare = Integer.compare(metric.count(o1), metric.count(o2));
		
		if (compare != 0) return compare;
		
		int negationLevelCompare = Integer.compare(new NegationLevel().count(o1), new NegationLevel().count(o2));
		
		if (negationLevelCompare != 0) return negationLevelCompare;
		
		int negationCountCompare = Integer.compare(new NegationCount().count(o1), new NegationCount().count(o2));
		
		if (negationCountCompare != 0) return negationCountCompare;
			
		int operatorCountCompare = Integer.compare(new OperatorCount().count(o1), new OperatorCount().count(o2));
			
		if (operatorCountCompare != 0) return operatorCountCompare;
		
		int operatorNestingCompare = Integer.compare(new OperatorNesting().count(o1), new OperatorNesting().count(o2));
		
		if (operatorNestingCompare != 0) return operatorNestingCompare;
		
//		int implicationCountCompare = Integer.compare(new ImplicationCount().count(o1), new ImplicationCount().count(o2));
//		
//		if (implicationCountCompare != 0) return implicationCountCompare;
//		
//		int iffCountCompare = Integer.compare(new IffCount().count(o1), new IffCount().count(o2));
//		
//		if (iffCountCompare != 0) return iffCountCompare;
		
		return 0;

	}

}
