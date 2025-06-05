package identities;

import java.util.ArrayList;

import formula.Formula;

public class DeMorgan implements Identity {

	@Override
	public Formula rewrite(Formula formula) {
		
		if (!formula.isAndOr() || formula.getElements().size() < 2) {
			return null;
		}

		return new Formula(!formula.isNegated(), new ArrayList<>(formula.getElements().stream().map(e -> e.negate()).toList()), formula.getDualOp());
	}

}
