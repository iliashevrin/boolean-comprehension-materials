package identities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import formula.Formula;

public class ImplicationReverse implements Identity {

	@Override
	public Formula rewrite(Formula formula) {
		
		if (!Formula.OR.equals(formula.getOp())) {
			return null;
		}
		
		List<Formula> allNegated = formula.getElements().stream().filter(e -> e.isNegated()).toList();
		List<Formula> allNonNegated = formula.getElements().stream().filter(e -> !e.isNegated()).toList();
		
		if (allNegated.isEmpty() || allNonNegated.isEmpty()) {
			return null;
		}
		
		Formula left = new Formula(false, new ArrayList<>(allNegated.stream().map(e -> e.negate()).toList()), Formula.AND);
		Formula right = new Formula(false, allNonNegated, Formula.OR);
		
		return new Formula(formula.isNegated(), Arrays.asList(left, right), Formula.IMPLIES);
	}

}
