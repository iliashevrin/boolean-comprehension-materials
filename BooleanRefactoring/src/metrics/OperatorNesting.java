package metrics;

import formula.Formula;

public class OperatorNesting implements Metric {

	
	@Override
	public int count(Formula formula) {
		int nesting = formula.getElements().isEmpty() ? 0 : 1;
		return nesting + formula.getElements().stream().map(e -> count(e)).max(Integer::compare).orElse(0);
	}

}
