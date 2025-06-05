package metrics;


import java.util.List;

import formula.Formula;
import identities.Identity;

public class LiteralCountForFilter implements Metric {


	
	@Override
	public int count(Formula formula) {
		throw new UnsupportedOperationException();
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
