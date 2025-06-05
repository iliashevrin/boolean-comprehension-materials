package formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import identities.Contrapositive;
import identities.DeMorgan;
import identities.Distributivity;
import identities.DistributivityReverse;
import identities.Equivalence;
import identities.EquivalenceReverse;
import identities.Identity;
import identities.Implication;
import identities.ImplicationReverse;
import metrics.Metric;

public class FormulaProcessor {
	
	private static final List<Identity> IDENTITIES = Arrays.asList(
			new Contrapositive(), 
			new DeMorgan(), 
			new Distributivity(), 
			new DistributivityReverse(),
			new Equivalence(),
			new EquivalenceReverse(), 
			new Implication(), 
			new ImplicationReverse());
	
	private Map<Formula, List<Formula>> results = new HashMap<>();
	private Metric metric;
	private Formula original;
	
	private int processCount;
	
	public FormulaProcessor(Metric metric, Formula original) {
		this.metric = metric;
		this.original = original;
		this.processCount = 0;
	}
	
	public List<Formula> getSortedResults(Formula formula) {

		return results.get(formula)
				.stream()
				
				.sorted(new FormulaComparator(metric))
				.filter(f -> !f.equals(formula))
				.filter(f -> metric.count(f) < metric.count(formula))
				
				.limit(10)
				.toList();
	}
	
	private List<Formula> getRepresentations(Formula formula) {
		for (Formula key : results.keySet()) {
			if (key.semanticEquals(formula)) {
				return results.get(key);
			}
		}
		
		System.out.println(formula);
		return null;
	}
	
	private void addToMap(Formula formula) {
		
		boolean found = false;
		for (Formula key : results.keySet()) {
			if (key.semanticEquals(formula)) {
				results.get(key).add(formula);
				found = true;
				break;
			}
		}
		if (!found) {
			results.put(formula, new ArrayList<>(Arrays.asList(formula)));
		}
	}
	
	private boolean representationExists(Formula formula) {
		for (Formula key : results.keySet()) {
			if (key.semanticEquals(formula)) {
				return results.get(key).contains(formula);
			}
		}
		return false;
	}
	
	public void process() {
		process(original, null);
	}


	private void process(Formula formula, String previousOp) {
		
		if (representationExists(formula)) return;
		
		processCount++;
		
		addToMap(formula);
		
		if (processCount > 500) {
			return;
		}
		
		// Process inner formulas and create a new formula where each rewritten inner formula replaced original formula
		for (int i = 0; i < formula.getElements().size(); i++) {
			
			Formula current = formula.getElements().get(i);
			process(current, formula.getOp());

			List<Formula> representations = new ArrayList<>(getRepresentations(current));
			
			for (Formula result : representations) {
				
				if (result.equals(current)) continue;

				Formula temp = new Formula(formula);
				temp.getElements().remove(i);
				temp.getElements().add(i, result);
				temp = temp.simplify();
				
				process(temp, previousOp);
			}
		}
		
		int oldValue = metric.count(formula);
			
		for (Identity identity : IDENTITIES) {
			
			Formula rewritten = identity.rewrite(formula);
			
			if (rewritten != null) {
				
				rewritten = rewritten.simplify();
				
				if (!rewritten.semanticEquals(formula)) {
					throw new RuntimeException();
				}
				
				// Rules when to apply identities to prevent bloating formulas unnecessarily
				if (metric.shouldApply(rewritten, identity, oldValue, previousOp)) {
					process(rewritten, previousOp);
				}
			}	
		}
	}
}
