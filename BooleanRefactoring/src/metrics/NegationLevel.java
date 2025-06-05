package metrics;

import java.util.Arrays;
import java.util.List;

import formula.Formula;
import identities.DeMorgan;
import identities.Identity;
import identities.Implication;

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

	@Override
	public boolean shouldApply(Formula rewritten, Identity identity, int oldValue, String previousOp) {

		return count(rewritten) < oldValue || (rewritten.isNegated() && identity instanceof Implication);
//				|| identity instanceof Equivalence));
	}
	
	@Override
	public int minimal() {
		return 1;
	}

	@Override
	public List<Identity> getRelevantIdentities() {
		return Arrays.asList(new DeMorgan());
	}

}
