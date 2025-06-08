package metrics;

import java.util.HashSet;
import java.util.Set;

import formula.Formula;

public class DistinctOperatorCount implements Metric {

	
	public Set<String> operators = new HashSet<>();
	
	@Override
	public int count(Formula formula) {
		operators.clear();
		return innerCount(formula);
	}

	public int innerCount(Formula formula) {
		boolean newOp = false;
		
		if (formula.getOp() != null && !operators.contains(formula.getOp())) {
			newOp = true;
			operators.add(formula.getOp());
		}
		return formula.getElements().stream().map(e -> innerCount(e)).reduce(0, Integer::sum) + (newOp ? 1 : 0);
	}

}
