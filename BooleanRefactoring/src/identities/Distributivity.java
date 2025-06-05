package identities;

import java.util.ArrayList;
import java.util.List;

import formula.Formula;

public class Distributivity implements Identity {

	@Override
	public Formula rewrite(Formula formula) {
		
		if (!formula.isAndOr()) {
			return null;
		}
		
		Formula dualSubformula = formula.getElements()
				.stream()
				.filter(e -> e.getOp() != null && e.getOp().equals(formula.getDualOp()))
				.filter(e -> !e.isNegated())
				.filter(e -> e.getElements().stream().noneMatch(f -> formula.getElements().contains(f)))
				.filter(e -> e.getElements().stream().allMatch(f -> formula.getElements().stream().filter(ee -> ee.getElements().contains(f)).count() == 1))
				.filter(e -> e.getElements().stream().allMatch(f -> f.getLiteral() != null))
				.findFirst()
				.orElse(null);
		
		if (dualSubformula == null) {
			return null;
		}
		
		List<Formula> newElements = new ArrayList<>();
		
		for (Formula element : dualSubformula.getElements()) {
			
			List<Formula> rest = new ArrayList<>(formula.getElements());
			rest.remove(dualSubformula);
			rest.add(element);
			
			Formula restFormula = new Formula(false, rest, formula.getOp());
			newElements.add(restFormula);
		}
		
		return new Formula(formula.isNegated(), newElements, formula.getDualOp());
	}

}
