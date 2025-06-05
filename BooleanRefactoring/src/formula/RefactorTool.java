package formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import identities.Contrapositive;
import identities.DeMorgan;
import identities.DistributivityReverse;
import identities.Equivalence;
import identities.Identity;
import identities.Implication;
import identities.ImplicationReverse;
import metrics.AndCount;
import metrics.DistinctOperatorCount;
import metrics.IffCount;
import metrics.ImplicationCount;
import metrics.ImplicationNesting;
import metrics.Metric;
import metrics.NegationCount;
import metrics.NegationLevel;
import metrics.NegationNesting;
import metrics.OperatorCount;
import metrics.OperatorNesting;
import metrics.OrCount;

public class RefactorTool {
	
	public static final int MAX_FORMULAS = 40;
	public static final int MAX_RETURN = 3;

	public Metric metric;
	public List<Identity> identities;
	public FormulaComparator comparator;
	
	public static Comparator<Formula> heuristicComparator = new Comparator<Formula>() {

		@Override
		public int compare(Formula f1, Formula f2) {
			return Double.compare(score(f1), score(f2));
		}
	};

	public RefactorTool(Metric metric) {
		this.metric = metric;
		this.identities = metric.getRelevantIdentities();
		this.comparator = new FormulaComparator(metric);
	}

	public RefactorTool(Metric metric, List<Identity> identities) {
		this.metric = metric;
		this.identities = identities;
		this.comparator = new FormulaComparator(metric);
	}

	// Refactor according to guidelines
//	public static Formula byGuidelines(Formula formula) {
//
//		NegationCount negationCount = new NegationCount();
//		NegationLevel negationLevel = new NegationLevel();
//		OperatorNesting nestingLevel = new OperatorNesting();
//		OperatorCount operatorCount = new OperatorCount();
//
//		Formula bestNegationCount = formula;
//		Formula bestNegationLevel = formula;
//		Formula bestNestingLevel = formula;
//		Formula bestOperatorCount = formula;
//
//		List<Identity> identities = Arrays.asList(new DeMorgan(), new DistributivityReverse(), new Contrapositive(),
//				new Implication(), new ImplicationReverse());
//
//		FormulaComparator negationCountComparator = new FormulaComparator(negationCount);
//		FormulaComparator negationLevelComparator = new FormulaComparator(negationLevel);
//		FormulaComparator nestingLevelComparator = new FormulaComparator(nestingLevel);
//		FormulaComparator operatorCountComparator = new FormulaComparator(operatorCount);
//
//		Set<Formula> visited = new HashSet<>();
//
//		Queue<Formula> toProcess = new LinkedList<>();
//		toProcess.add(formula);
//
//		boolean earlyStop = false;
//
//		while (!toProcess.isEmpty() && !earlyStop) {
//
//			Formula current = toProcess.poll();
//			visited.add(current);
//
//			for (Identity identity : identities) {
//
//				if (negationCount.count(current) == 0) {
//					if (identity instanceof Contrapositive)
//						continue;
//					if (identity instanceof DeMorgan)
//						continue;
//				}
//				if (operatorCount.count(current) == new LiteralCount().count(current) - 1) {
//					if (identity instanceof DistributivityReverse)
//						continue;
//				}
//				if (nestingLevel.count(current) == 1 && nestingLevel.count(formula) > 1) {
//					if (identity instanceof ImplicationReverse)
//						continue;
//				}
//
//				List<Formula> rewrites = rewriteNested(current, identity);
//
//				for (Formula rewrite : rewrites) {
//
//					if (visited.contains(rewrite))
//						continue;
//
//					toProcess.add(rewrite);
//
//					// Check if it is really the best formula
//					if (negationCountComparator.compare(rewrite, bestNegationCount) < 0) {
//						bestNegationCount = rewrite;
//					}
//					if (negationLevelComparator.compare(rewrite, bestNegationLevel) < 0) {
//						bestNegationLevel = rewrite;
//					}
//					if (nestingLevelComparator.compare(rewrite, bestNestingLevel) < 0) {
//						bestNestingLevel = rewrite;
//
//						if (nestingLevel.count(bestNestingLevel) == 1 && nestingLevel.count(formula) > 1) {
//							earlyStop = true;
//							break;
//						}
//					}
//					if (operatorCountComparator.compare(rewrite, bestOperatorCount) < 0) {
//						bestOperatorCount = rewrite;
//					}
//				}
//
//			}
//		}
//
//		// Nesting level refactor takes priority
//		if (nestingLevel.count(bestNestingLevel) < nestingLevel.count(formula)) {
//			return bestNestingLevel;
//		}
//
//		if (negationCount.count(bestNegationCount) == 0 && negationCount.count(formula) > 0) {
//			return bestNegationCount;
//		}
//
//		if (operatorCount.count(bestOperatorCount) < operatorCount.count(formula)) {
//			return bestOperatorCount;
//		}
//
//		if (negationLevel.count(bestNegationLevel) < negationLevel.count(formula)) {
//			return bestNegationLevel;
//		}
//
//		// No change to the original formula
//		return formula;
//	}

	public static double score(Formula formula) {

		int negationCount = new NegationCount().count(formula);
		int negationLevel = new NegationLevel().count(formula);
		int nestingLevel = new OperatorNesting().count(formula);
		int operatorCount = new OperatorCount().count(formula);
		
		int implicationCount = new ImplicationCount().count(formula);
		int iffCount = new IffCount().count(formula);
		int distinctOperatorCount = new DistinctOperatorCount().count(formula);
		int negationNesting = new NegationNesting().count(formula);
		int implicationNesting = new ImplicationNesting().count(formula);
		
		int andCount = new AndCount().count(formula);
		int orCount = new OrCount().count(formula);
		
//		return 2.82 + 0.55 * negationCount + 3.59 * negationLevel + 3.76 * nestingLevel + 1.63 * operatorCount +
//				3 * implicationCount - 0.59 * implicationNesting + 4.63 * iffCount + 0.37 * distinctOperatorCount - 1.81 * negationNesting;

		return 5.24 + 
				0.69 * negationCount + 
				3.4 * negationLevel + 
				2.12 * nestingLevel + 
				0.58 * operatorCount +
				4.57 * implicationCount + 
				0.43 * implicationNesting + 
				7.68 * iffCount +
				-0.71 * distinctOperatorCount +
				-1.54 * negationNesting +
				2.53 * andCount + 
				3.03 * orCount;
		
//		return 3.29 + 
//				0.41 * negationCount + 
//				3.74 * negationLevel + 
//				3.76 * nestingLevel + 
////				1.95 * operatorCount +
//				4.44 * implicationCount +
//				-0.47 * implicationNesting + 
//				5.96 * iffCount + 
//				0.21 * distinctOperatorCount +
//				-2.07 * negationNesting +
//				1.95 * andCount + 
//				1.48 * orCount;
		
		// Score from previous version of the paper
//		return 0.27 + 0.31 * negationCount + 3.06 * negationLevel + 6.42 * nestingLevel + 1.53 * operatorCount;
		
		// Old regression formula of average time and correctness score
		// 1.49 + 0.15 * negationCount + 1.41 * negationLevel + 4.91 * nestingLevel + 0.76 * operatorCount;
	}
	
	public static List<Formula> refactorWithPriorityQueue(Formula formula) {
		return refactorWithPriorityQueue(formula, MAX_RETURN);
	}

	public static List<Formula> refactorWithPriorityQueue(Formula formula, int maxReturn) {
		
		Set<Formula> visited = new HashSet<>();

		List<Identity> identities = Arrays.asList(new DeMorgan(), new DistributivityReverse(), new Contrapositive(),
				new Implication(), new ImplicationReverse(), new Equivalence());

		PriorityQueue<Formula> toProcess = new PriorityQueue<>(heuristicComparator);
		toProcess.add(formula);

		while (!toProcess.isEmpty()) {

			Formula current = toProcess.poll();
			visited.add(current);

			if (visited.size() >= MAX_FORMULAS) {
				break;
			}

			// Neighbor rewrites
			for (Identity identity : identities) {
				List<Formula> rewrites = rewriteNested(current, identity);
				for (Formula rewrite : rewrites) {

					if (visited.contains(rewrite))
						continue;
					toProcess.add(rewrite);
				}

			}
		}
		
		Comparator<Formula> scoreComparator = new Comparator<Formula>() {

			@Override
			public int compare(Formula f1, Formula f2) {
				int res = Double.compare(score(f1), score(f2));
				
				// Return the original in case the score is equal
				if (res == 0) {
					if (f1.equals(formula)) {
						return -1;
					} else if (f2.equals(formula)) {
						return 1;
					}
				}
				return res;
			}
		};

		List<Formula> sorted = visited.stream().sorted(scoreComparator).toList();
		return sorted.subList(0, Math.min(sorted.size(), maxReturn));
	}

	public Formula naive(Formula formula) {

		Formula bestFormula = formula;

		Set<Formula> visited = new HashSet<>();

		Queue<Formula> toProcess = new LinkedList<>();
		toProcess.add(formula);

		while (!toProcess.isEmpty()) {

			Formula current = toProcess.poll();
			visited.add(current);

			for (Identity identity : identities) {

				List<Formula> rewrites = rewriteNested(current, identity);

				for (Formula rewrite : rewrites) {

					if (visited.contains(rewrite))
						continue;

					toProcess.add(rewrite);

					// Check if it is really the best formula
					if (comparator.compare(rewrite, bestFormula) <= 0) {
						bestFormula = rewrite;
					}

				}

			}
		}

		return bestFormula;

	}

	private static Formula rewrite(Formula formula, Identity identity) {

		Formula rewritten = identity.rewrite(formula);

		if (rewritten == null)
			return null;

		Formula temp = new Formula(rewritten);
		rewritten = rewritten.simplify();

		// Sanity check
		if (!rewritten.semanticEquals(formula)) {
			System.out.println(formula);
			System.out.println(temp);
			System.out.println(rewritten);
			throw new RuntimeException();
		}

		return rewritten;
	}

	private static Formula replace(Formula formula, Formula subformula, Formula subformulaRewrite) {
		int index = formula.getElements().indexOf(subformula);
		Formula temp = new Formula(formula);
		temp.getElements().remove(index);
		temp.getElements().add(index, subformulaRewrite);
		temp = temp.simplify();
		return temp;
	}

	private static List<Formula> rewriteNested(Formula formula, Identity identity) {

		List<Formula> rewrites = new ArrayList<>();
		rewrites.add(rewrite(formula, identity));

		for (Formula subformula : formula.getElements()) {

			for (Formula subformulaRewrite : rewriteNested(subformula, identity)) {
				rewrites.add(replace(formula, subformula, subformulaRewrite));
			}

		}

		return rewrites.stream().filter(f -> f != null).toList();
	}
}
