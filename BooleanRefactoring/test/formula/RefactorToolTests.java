package formula;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import language.ParserHelper;

public class RefactorToolTests {
	
	private static double sum(Collection<Double> arr) {
		double sum = 0.0;
	    for (Double num : arr) {
	    	sum += num;
	    }
	    return sum;
	}
	
	private static double median(Collection<Double> arr) {
		List<Double> copy = new ArrayList<>(arr);
		Collections.sort(copy);
		
		Double median;
		if (copy.size() % 2 == 0) {
			median = (copy.get(copy.size() / 2) + copy.get((copy.size() / 2) - 1)) / 2;
		} else {
			median = copy.get(copy.size() / 2);
		}
		return median;
	}
	
	private List<Formula> getMutations(Formula formula) {
		List<Formula> mutations = new ArrayList<>();
		Random r = new Random();
		Formula mut1 = new Formula(formula);
		mut1.mutateNegation(r);
		if (!mut1.equals(formula)) {
			mutations.add(mut1);
		}
		mut1 = new Formula(formula);
		mut1.mutateNegation(r);
		if (!mut1.equals(formula)) {
			mutations.add(mut1);
		}
		Formula mut2 = new Formula(formula);
		mut2.mutateOperator(r);
		if (!mut2.equals(formula)) {
			mutations.add(mut2);	
		}
		mut2 = new Formula(formula);
		mut2.mutateOperator(r);
		if (!mut2.equals(formula)) {
			mutations.add(mut2);	
		}
		return mutations;
	}
	
	@Test
	void testRefactoringTool() {
		
		List<String> formulas = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader("formulas/single_formulas_final.csv"))) {
            String line;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length > 0) {
                	formulas.add(columns[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		Map<Formula, Double> times = new HashMap<>();
		Map<Formula, Double> score = new HashMap<>();
		
		Double sumScoreOriginal = 0.0;
		Double sumScoreResult = 0.0;
		
		Double datasetScoreOriginal = 0.0;
		Double datasetScoreResult = 0.0;
		
		// Write to CSV
        try {
        	
            FileWriter writer = new FileWriter("refactoring_procedure_runtimes.csv");

            // Write header
            writer.append("Original,Refactoring,Time,Improvement,Original Score,Refactoring Score\n");
            
		
			for (String formula : formulas) {
				
				Formula original = ParserHelper.parse(formula);
				
				Formula result = recordRefactoring(times, score, writer, original);
                sumScoreOriginal += score.get(original);
                sumScoreResult += score.get(result);
                
                datasetScoreOriginal += score.get(original);
                datasetScoreResult += score.get(result);
                
                for (Formula mutation : getMutations(original)) {
                	
    				result = recordRefactoring(times, score, writer, mutation);
                    sumScoreOriginal += score.get(mutation);
                    sumScoreResult += score.get(result);
                }

			}
			
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
		
		System.out.println("Median time of refactor tool = " + median(times.values()));
		System.out.println("Mean time of refactor tool = " + sum(times.values()) / times.size());
		
		System.out.println("Best time of refactor tool = " + times.values().stream().min(Double::compareTo).get());
		System.out.println("Worst time of refactor tool = " + times.values().stream().max(Double::compareTo).get());

		System.out.println("Mean score original = " + (sumScoreOriginal / times.size()));
		System.out.println("Mean score result = " + (sumScoreResult / times.size()));
		System.out.println("Improvement = " + (sumScoreResult / sumScoreOriginal));
		
		System.out.println("Mean score (only dataset) original = " + (datasetScoreOriginal / formulas.size()));
		System.out.println("Mean score (only dataset) result = " + (datasetScoreResult / formulas.size()));
		System.out.println("Improvement = " + (datasetScoreResult / datasetScoreOriginal));

	}

	private Formula recordRefactoring(Map<Formula, Double> times, Map<Formula, Double> score, FileWriter writer, Formula original) throws IOException {
		
		long start = System.currentTimeMillis();
		
		Formula result = RefactorTool.refactorWithPriorityQueue(original).get(0);

		long end = System.currentTimeMillis();
		double time = ((double) (end - start)) / 1000;
		
		score.put(original, RefactorTool.score(original));
		score.put(result, RefactorTool.score(result));
		
		times.put(original, time);
		
		writer.append(original.toString());
		writer.append(',');
		writer.append(result.toString());
		writer.append(',');
		writer.append(String.valueOf(times.get(original)));
		writer.append(',');
		writer.append(String.valueOf(score.get(result) / score.get(original)));
		writer.append(',');
		writer.append(String.valueOf(score.get(original)));
		writer.append(',');
		writer.append(String.valueOf(score.get(result)));
		writer.append('\n');
		return result;
	}
}
