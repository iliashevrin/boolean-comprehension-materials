package identities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import formula.Formula;

public class EquivalenceReverse implements Identity {

	@Override
	public Formula rewrite(Formula formula) {
		
		if (!Formula.AND.equals(formula.getOp())) {
			return null;
		}
		
		List<Formula> impliesSubformulas = formula.getElements().stream().filter(e -> e.getOp() != null && e.getOp().equals(Formula.IMPLIES)).toList();
		if (impliesSubformulas.isEmpty()) {
			return null;
		}
		
		// Find only the first implies pair
		
		for (Formula subformula : impliesSubformulas) {
			
			Formula pair = impliesSubformulas.stream().filter(f -> f.getElements().get(0).equals(subformula.getElements().get(1)) &&
							f.getElements().get(1).equals(subformula.getElements().get(0))).findAny().orElse(null);
			
			if (pair != null) {
				
				if (formula.getElements().size() == 2) {
					return new Formula(formula.isNegated(), Arrays.asList(subformula.getElements().get(0), subformula.getElements().get(1)), Formula.IFF);
				} else {
					
					List<Formula> rest = new ArrayList<>(formula.getElements().stream().filter(e -> !e.equals(subformula) || !e.equals(pair)).toList());
					rest.add(new Formula(false, Arrays.asList(subformula.getElements().get(0), subformula.getElements().get(1)), Formula.IFF));
					return new Formula(formula.isNegated(), rest, Formula.AND);
				}
			}
		}
		
		return null;
	}

}