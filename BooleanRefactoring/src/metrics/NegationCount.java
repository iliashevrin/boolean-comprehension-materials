package metrics;

import java.util.Arrays;
import java.util.List;

import formula.Formula;
import identities.Contrapositive;
import identities.DeMorgan;
import identities.Identity;
import identities.Implication;
import identities.ImplicationReverse;

public class NegationCount implements Metric {
	
	
	
	@Override
	public int count(Formula formula) {
		return formula.getElements().stream().map(e -> count(e)).reduce(0, Integer::sum) + (formula.isNegated() ? 1 : 0);
	}

	@Override
	public boolean shouldApply(Formula rewritten, Identity identity, int oldValue, String previousOp) {
		
		return count(rewritten) < oldValue
				|| identity instanceof Implication;
//				|| identity instanceof Equivalence;
	}

	@Override
	public int minimal() {
		return 0;
	}

	@Override
	public List<Identity> getRelevantIdentities() {
		return Arrays.asList(new Contrapositive(), new DeMorgan(), new Implication(), new ImplicationReverse());
	}
}
