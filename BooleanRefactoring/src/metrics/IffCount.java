package metrics;

import java.util.List;

import formula.Formula;
import identities.DeMorgan;
import identities.Identity;

public class IffCount implements Metric {
	
	@Override
	public int count(Formula formula) {
		int addition = Formula.IFF.equals(formula.getOp()) ? 1 : 0;
		return formula.getElements().stream().map(e -> count(e)).reduce(0, Integer::sum) + addition;
	}

	@Override
	public boolean shouldApply(Formula rewritten, Identity identity, int oldValue, String previousOp) {
		
		if (identity instanceof DeMorgan) {
			return !rewritten.isNegated();
		}
		
		return count(rewritten) < oldValue;
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
