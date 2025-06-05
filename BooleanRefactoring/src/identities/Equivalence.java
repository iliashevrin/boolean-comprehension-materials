package identities;

import java.util.Arrays;

import formula.Formula;

public class Equivalence implements Identity {

	@Override
	public Formula rewrite(Formula formula) {
		
		if (!Formula.IFF.equals(formula.getOp())) {
			return null;
		}
		
		return new Formula(formula.isNegated(), Arrays.asList(
				new Formula(false, Arrays.asList(formula.getElements().get(0), formula.getElements().get(1)), Formula.IMPLIES),
				new Formula(false, Arrays.asList(formula.getElements().get(1), formula.getElements().get(0)), Formula.IMPLIES)), 
				Formula.AND);
	}

}