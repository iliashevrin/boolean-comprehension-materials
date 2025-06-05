package metrics;

import java.util.Arrays;
import java.util.List;

import formula.Formula;
import identities.DeMorgan;
import identities.DistributivityReverse;
import identities.Identity;
import identities.Implication;

public class OperatorCount implements Metric {
	
	
	@Override
	public int count(Formula formula) {
		int ops = formula.getElements().isEmpty() ? 0 : (formula.getElements().size() - 1);
		return ops + formula.getElements().stream().map(e -> count(e)).reduce(0, Integer::sum);
	}

	@Override
	public boolean shouldApply(Formula rewritten, Identity identity, int oldValue, String previousOp) {

		if (identity instanceof DeMorgan) {
			return !rewritten.isNegated();
		}
		
		return count(rewritten) < oldValue || identity instanceof Implication;
	}

	@Override
	public int minimal() {
		return 1;
	}

	@Override
	public List<Identity> getRelevantIdentities() {
		return Arrays.asList(new DeMorgan(), new Implication(), new DistributivityReverse());
	}
}
