package statisticalanalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import language.ParserHelper;
import metrics.Metric;

public class RunAdditionalAnalysis {

	

	public static void main(String[] args) throws Exception {
		
//		BiFunction<Integer, Integer, Boolean> bigger = (d1,d2) -> d2 > d1;
		
		BiFunction<Integer, Integer, Boolean> smaller = (d1,d2) -> d2 < d1;
		BiFunction<Integer, Integer, Boolean> toOne = (d1,d2) -> d2 < d1 && d2 == 1;
		BiFunction<Integer, Integer, Boolean> toZero = (d1,d2) -> d2 < d1 && d2 == 0;
		BiFunction<Integer, Integer, Boolean> nonTrivial = (d1,d2) -> d2 < d1 && d1 > 1;
		
		List<Object[]> resultsTable = new ArrayList<>();
		resultsTable.add(RunStatisticalAnalysis.generalHeaders);

		String csvFile = args[0];
		
		
		for (int i = 1; i < args.length; i=i+2) {
			
			Set<String> distinctPairs = new HashSet<>();
			
			List<Double> originalResults = new ArrayList<>();
			List<Double> rewrittenResults = new ArrayList<>();
			List<Boolean> originalCorrect = new ArrayList<>();
			List<Boolean> rewrittenCorrect = new ArrayList<>();
			
			List<Double> diff = new ArrayList<>();
			List<Double> correctnessDiff = new ArrayList<>();
			
			int originalCorrectRewrittenWrong = 0;
			int originalWrongRewrittenCorrect = 0;
			
			Class<?> clazz = Class.forName("metrics." + args[i]);
			Constructor<?> ctor = clazz.getConstructor();
			Metric metric = (Metric) ctor.newInstance();
			
			BiFunction<Integer, Integer, Boolean> filter = null;
			if ("smaller".equals(args[i+1])) {
				filter = smaller;
			} else if ("toOne".equals(args[i+1])) {
				filter = toOne;
			} else if ("toZero".equals(args[i+1])) {
				filter = toZero;
			} else if ("nonTrivial".equals(args[i+1])) {
				filter = nonTrivial;
				
//			} else if ("bigger".equals(args[i+1])) {
//				filter = bigger;
				
			} else {
				continue;
			}
			
			
	        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
	        	
	        	String line;
	        	
	        	// Read first line of column headers
	        	line = br.readLine();

	            while ((line = br.readLine()) != null) {
	            	
	            
	            	
	                String[] parts = line.split(",");

	                Instant timestamp = Instant.parse(parts[1]);
	                
	                if (LocalDate.ofInstant(timestamp, ZoneId.systemDefault()).isBefore(LocalDate.of(2024, 2, 5))) continue;
	                
	                
	                for (int index = 11; index <= 67; index = index + 8) {
	                	
	            		String originalFormula = parts[index];
	            		String rewrittenFormula = parts[index + 4];

	                	int originalMetric = metric.count(ParserHelper.parse(originalFormula));
	                	int rewrittenMetric = metric.count(ParserHelper.parse(rewrittenFormula));
	                	
	                	
	                	if (!filter.apply(originalMetric, rewrittenMetric)) continue;
	                	
	                	if (parts[index - 3].isEmpty() || parts[index + 1].isEmpty() || parts[index - 2].isEmpty() || parts[index + 2].isEmpty()) {
	                		continue;
	                	}
	            		
	                	Double originalTime = Double.parseDouble(parts[index - 3]);
	                	Double rewrittenTime = Double.parseDouble(parts[index + 1]);
	                	
	                	// Sanity check
	                	if (originalTime > RunStatisticalAnalysis.MAX_TIME || rewrittenTime > RunStatisticalAnalysis.MAX_TIME || 
	                			originalTime < RunStatisticalAnalysis.MIN_TIME || rewrittenTime < RunStatisticalAnalysis.MIN_TIME) continue;
	                	
	                	distinctPairs.add(originalFormula + "_" + rewrittenFormula);
	                	
	                	Boolean originalIsCorrect = Boolean.parseBoolean(parts[index - 2]);
	                	Boolean rewrittenIsCorrect = Boolean.parseBoolean(parts[index + 2]);
	                	
	                    originalResults.add(originalTime);
	                    originalCorrect.add(originalIsCorrect);
	                    rewrittenResults.add(rewrittenTime);
	                    rewrittenCorrect.add(rewrittenIsCorrect);

	                    diff.add(originalTime - rewrittenTime);

	                    double correctness = 0.0;
	                    if (originalIsCorrect && !rewrittenIsCorrect) {
	                    	correctness = 1.0;
	                    	originalCorrectRewrittenWrong++;
	                    } else if (!originalIsCorrect && rewrittenIsCorrect) {
	                    	correctness = -1.0;
	                    	originalWrongRewrittenCorrect++;
	                    }
	                    
	                    correctnessDiff.add(correctness);	                	
	                }
	            }
	        }
	        
	        
	        Object[] row = new Object[RunStatisticalAnalysis.generalHeaders.length + 1];
	        row[0] = metric.toString() + "_" + args[i+1];
	        
	        RunStatisticalAnalysis.analyze(true, row, originalResults, rewrittenResults, originalCorrect, rewrittenCorrect, 
	        		diff, correctnessDiff, originalCorrectRewrittenWrong, originalWrongRewrittenCorrect);
	        
	        row[16] = distinctPairs.size();
	        resultsTable.add(row);
		}
		


		RunStatisticalAnalysis.writeResultsTable(resultsTable, csvFile + "_additional_results.csv");

	}

}
