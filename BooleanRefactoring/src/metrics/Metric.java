package metrics;


import java.util.List;

import formula.Formula;
import formula.RefactorTool;
import identities.Identity;

public interface Metric {

	
	int count(Formula formula);
	
	int minimal();
	
	List<Identity> getRelevantIdentities();
	
	boolean shouldApply(Formula rewritten, Identity identity, int oldValue, String previousOp);
	
	default Formula process(Formula formula) {
		return new RefactorTool(this).naive(formula);
	}
}
