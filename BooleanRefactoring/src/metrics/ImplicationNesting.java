package metrics;

import formula.Formula;

public class ImplicationNesting implements Metric {

	@Override
	public int count(Formula formula) {
		int nesting = Formula.IMPLIES.equals(formula.getOp()) ? 1 : 0;
		return nesting + formula.getElements().stream().map(e -> count(e)).max(Integer::compare).orElse(0);
	}
}
