package metrics;

import formula.Formula;

public class NegationCount implements Metric {
	
	
	@Override
	public int count(Formula formula) {
		return formula.getElements().stream().map(e -> count(e)).reduce(0, Integer::sum) + (formula.isNegated() ? 1 : 0);
	}
}
