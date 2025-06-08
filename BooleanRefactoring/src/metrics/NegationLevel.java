package metrics;

import formula.Formula;

public class NegationLevel implements Metric {
	

	@Override
	public int count(Formula formula) {
		
		if (formula.isNegated()) {
			OperatorNesting operatorNesting = new OperatorNesting();
			return operatorNesting.count(formula) + 1;
		} else {
			return formula.getElements().stream().map(e -> count(e)).max(Integer::compare).orElse(0);
		}
	}

}
