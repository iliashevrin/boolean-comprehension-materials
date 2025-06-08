package metrics;

import formula.Formula;

public class OperatorCount implements Metric {
	
	
	@Override
	public int count(Formula formula) {
		int ops = formula.getElements().isEmpty() ? 0 : (formula.getElements().size() - 1);
		return ops + formula.getElements().stream().map(e -> count(e)).reduce(0, Integer::sum);
	}
}
