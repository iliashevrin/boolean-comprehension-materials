package metrics;

import java.util.List;

import formula.Formula;
import identities.Identity;

public class ImplicationNesting implements Metric {

	@Override
	public int count(Formula formula) {
		int nesting = Formula.IMPLIES.equals(formula.getOp()) ? 1 : 0;
		return nesting + formula.getElements().stream().map(e -> count(e)).max(Integer::compare).orElse(0);
	}

	@Override
	public boolean shouldApply(Formula rewritten, Identity identity, int oldValue, String previousOp) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int minimal() {
		return 0;
	}

	@Override
	public List<Identity> getRelevantIdentities() {
		return null;
	}
}
