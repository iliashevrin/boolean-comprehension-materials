package identities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import formula.Formula;

public class DistributivityReverse implements Identity {

	@Override
	public Formula rewrite(Formula formula) {
		
		if (!formula.isAndOr()) {
			return null;
		}
		
		List<Formula> dualSubformulas = formula.getElements().stream().filter(e -> !e.isNegated() && e.getOp() != null && e.getOp().equals(formula.getDualOp())).toList();
		
		if (dualSubformulas.isEmpty()) {
			return null;
		}
		
		Map<Formula, List<Formula>> elementsMap = new HashMap<>();
		
		Formula maxShared = null;
		int max = 1;
		
		// In the map - each subsubformula keeps reference to all the subformulas it belongs to
		
		for (Formula subformula : dualSubformulas) {
			
			for (Formula subsubformula : subformula.getElements()) {
				
				if (!elementsMap.containsKey(subsubformula)) {
					elementsMap.put(subsubformula, new ArrayList<>());
				}
				elementsMap.get(subsubformula).add(subformula);
				
				if (elementsMap.get(subsubformula).size() > max) {
					maxShared = subsubformula;
					max = elementsMap.get(subsubformula).size();
				}
			}
		}
		
		if (maxShared == null) {
			return null;
		}
		
		List<Formula> participatingSubformulas = elementsMap.get(maxShared);
		
		
		// Find all subsubformulas that participate in the same subformulas
		
		List<Formula> allMaxShared = new ArrayList<>();
		for (Formula shared : elementsMap.keySet()) {
			if (elementsMap.get(shared).containsAll(participatingSubformulas) && 
					participatingSubformulas.containsAll(elementsMap.get(maxShared))) {
				allMaxShared.add(shared);
			}
		}
		
		List<Formula> newSubfomulas = new ArrayList<>();
		for (Formula subformula : participatingSubformulas) {
			Formula temp = new Formula(subformula);
			for (Formula f : allMaxShared) {
				temp.getElements().remove(f);
			}
			newSubfomulas.add(temp);
		}
		
		Formula inner = new Formula(false, newSubfomulas, formula.getOp());
		
		List<Formula> newElements = new ArrayList<>();
		newElements.addAll(allMaxShared);
		newElements.add(inner);
		
		// All the subformulas participate in the distributivity
		if (participatingSubformulas.size() == formula.getElements().size()) {
			return new Formula(formula.isNegated(), newElements, formula.getDualOp());	
		} else {
			List<Formula> rest = new ArrayList<>(formula.getElements().stream().filter(e -> !participatingSubformulas.contains(e)).toList());
			rest.add(new Formula(false, newElements, formula.getDualOp()));
			return new Formula(formula.isNegated(), rest, formula.getOp());
		}
	}

}
