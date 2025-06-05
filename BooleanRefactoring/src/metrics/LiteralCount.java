package metrics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import formula.Formula;
import identities.Identity;

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

	@Override
	public boolean shouldApply(Formula rewritten, Identity identity, int oldValue, String previousOp) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int minimal() {
		return 1;
	}

	@Override
	public List<Identity> getRelevantIdentities() {
		return null;
	}

}
