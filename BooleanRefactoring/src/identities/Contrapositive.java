package identities;

import java.util.ArrayList;
import java.util.List;

import formula.Formula;

public class Contrapositive implements Identity {

	@Override
	public Formula rewrite(Formula formula) {
		
		if (!Formula.IMPLIES.equals(formula.getOp())) {
			return null;
		}
		List<Formula> newElements = new ArrayList<>();
		newElements.add(formula.getElements().get(1).negate());
		newElements.add(formula.getElements().get(0).negate());

		return new Formula(formula.isNegated(), newElements, Formula.IMPLIES);
	}

}
