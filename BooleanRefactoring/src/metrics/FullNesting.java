package metrics;

import java.util.Arrays;
import java.util.List;

import formula.Formula;
import identities.DeMorgan;
import identities.Distributivity;
import identities.DistributivityReverse;
import identities.Equivalence;
import identities.Identity;
import identities.Implication;

public class FullNesting implements Metric {

	
	@Override
	public int count(Formula formula) {
		int nesting = formula.getElements().isEmpty() ? 0 : 1;
		if (formula.isNegated()) { // Address negation in the nesting computation
			nesting++;
		}
		return nesting + formula.getElements().stream().map(e -> count(e)).max(Integer::compare).orElse(0);
	}

	@Override
	public boolean shouldApply(Formula rewritten, Identity identity, int oldValue, String previousOp) {
		
		if (identity instanceof DeMorgan) {
			return !rewritten.isNegated();
		}
		
		return (rewritten.getOp().equals(previousOp) && (identity instanceof Distributivity || identity instanceof DistributivityReverse))
				|| identity instanceof Equivalence
				|| identity instanceof Implication;
	}
	
	@Override
	public int minimal() {
		return 1;
	}

	@Override
	public List<Identity> getRelevantIdentities() {
		return Arrays.asList(new DeMorgan(), new Implication());
	}

}
