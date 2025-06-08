package metrics;

import formula.Formula;

public class OrCount implements Metric {
	
	@Override
	public int count(Formula formula) {
		int addition = Formula.OR.equals(formula.getOp()) ? 1 : 0;
		return formula.getElements().stream().map(e -> count(e)).reduce(0, Integer::sum) + addition;
	}

}
