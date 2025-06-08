package metrics;

import java.util.HashSet;
import java.util.Set;

import formula.Formula;

public class LiteralCount implements Metric {

	
	public Set<String> literals = new HashSet<>();

	@Override
	public int count(Formula formula) {
		boolean newLiteral = false;
		
		if (formula.getLiteral() != null && !literals.contains(formula.getLiteral())) {
			newLiteral = true;
			literals.add(formula.getLiteral());
		}
		return formula.getElements().stream().map(e -> count(e)).reduce(0, Integer::sum) + (newLiteral ? 1 : 0);
	}

}
