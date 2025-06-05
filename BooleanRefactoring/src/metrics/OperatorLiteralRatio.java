package metrics;

import java.util.Arrays;
import java.util.List;

import formula.Formula;
import identities.DeMorgan;
import identities.DistributivityReverse;
import identities.Identity;
import identities.Implication;

public class OperatorLiteralRatio implements Metric {
	
	
	@Override
	public int count(Formula formula) {
		int opCount = new OperatorCount().count(formula);
		int litCount = new LiteralCount().count(formula);
		return (int) Math.ceil((opCount + 1) / litCount);
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
