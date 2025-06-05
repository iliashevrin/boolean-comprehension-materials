package scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import question.TestCategory;

public class RunStatisticalAnalysis {
	
	public static int EMPIRICAL_RANDOM_SIZE = 20000;
	
	public static int MIN_TIME = 5000;
	public static int MAX_TIME = 180000;
	
	public static String TWO_SIDED = "two-sided";
	public static String ONE_SIDED = "one-sided"; // The rewritten is better than the original
	
	public static String csvFile;
	
	public static double averageTime = 0.0;
	public static int questions = 0;
	
	public static String[] generalHeaders = new String[] {
			"Category",
			"Original Median",
			"Original Mean",
			"Original Stdv.",
			"Original Correct",
			"Rewritten Median",
			"Rewritten Mean",
			"Rewritten Stdv.",
			"Rewritten Correct",
			"N",
			"Both Correct",
			"Cohen\'s d",
			"permutation p-value",
			"wilcoxon p-value",
			"correctness p-value",
			"McNemar"
	};
	
	public static String[] formulaHeaders = new String[] {
			"Category",
			"Original Formula",
			"Original N",
			"Original Median",
			"Original Mean",
			"Original Stdv.",
			"Original Correct",
			"Rewritten Formula",
			"Rewritten N",
			"Rewritten Median",
			"Rewritten Mean",
			"Rewritten Stdv.",
			"Rewritten Correct"
	};

	public static void main(String[] args) throws IOException {
		
		csvFile = args[0];
		
		List<Object[]> resultsTable = new ArrayList<>();
		resultsTable.add(generalHeaders);
		
		for (TestCategory category : TestCategory.values()) {
			resultsTable.add(analyzeCategory(category, true));
		}
		
		
		writeResultsTable(resultsTable, args[1]);
		
		System.out.println();
		analyzeIndexes(args[2]);
		
		List<Object[]> formulasTable = new ArrayList<>();
		formulasTable.add(formulaHeaders);
		
		for (TestCategory category : TestCategory.values()) {
			formulasTable.addAll(analyzeCategoryByFormula(category));
		}
		
		writeResultsTable(formulasTable, args[3]);
		
		System.out.println("Total Questions " + questions);
		System.out.println("Average Time " + averageTime / questions);
	}
	
	public static void writeResultsTable(List<Object[]> results, String resultsCsv) {
		// Writing data to CSV file
        try (FileWriter writer = new FileWriter(resultsCsv)) {
            for (Object[] row : results) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                	if (row[i] instanceof Double) {
                		sb.append(String.format("%.3f", row[i]));
                	} else {
                		sb.append(row[i]);	
                	}
                    if (i < row.length - 1) {
                        sb.append(",");
                    }
                }
                sb.append(System.lineSeparator());
                writer.write(sb.toString());
            }
            System.out.println("CSV file written successfully.");
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
        }
	}
	
	private static void analyzeIndexes(String resultsCsv) {
		
		StringBuilder sb = new StringBuilder();
		
		List<List<Double>> indexResults = new ArrayList<>();
		List<List<Boolean>> indexCorrect = new ArrayList<>();
		
		for (int i = 0; i < 16; i++) {
			indexResults.add(new ArrayList<>());
			indexCorrect.add(new ArrayList<>());
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
        	String line;
        	
        	// Read first line of column headers
        	line = br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                Instant timestamp = Instant.parse(parts[1]);
                
                if (!LocalDate.ofInstant(timestamp, ZoneId.systemDefault()).isBefore(LocalDate.of(2024, 2, 5))) {
                	
                	for (int j = 0; j < 8; j++) {
                		
                		if (parts[8 + 4*j] == null || parts[8 + 4*j].isEmpty()) continue;
                		
                    	Integer index = Integer.parseInt(parts[10 + 4*j]);
                    	Double time = Double.parseDouble(parts[8 + 4*j]);
                    	Boolean correct = Boolean.parseBoolean(parts[9 + 4*j]);
                    	
                    	// Sanity check
                    	if (time > MAX_TIME || time < MIN_TIME) continue;
                    	
                        indexResults.get(index).add(time);
                        indexCorrect.get(index).add(correct);
                	}

                }
                
            }

            for (int i = 0; i < 16; i++) {
            	sb.append(i);
            	sb.append(",");
            }
            sb.append(System.lineSeparator());
            
    		for (int i = 0; i < 16; i++) {
    			double sum = sum(indexResults.get(i));
    			double mean = sum / indexResults.get(i).size();
    			
    			sb.append(mean);
    			sb.append(",");
    		}
    		sb.append(System.lineSeparator());
            
    		for (int i = 0; i < 16; i++) {
    			long correct = correctNum(indexCorrect.get(i));
    			
    			sb.append(correct);
    			sb.append(",");
    		}
    		sb.append(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		// Writing data to CSV file
        try (FileWriter writer = new FileWriter(resultsCsv)) {
        	writer.write(sb.toString());
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
        }
	}
	
	private static List<Object[]> analyzeCategoryByFormula(TestCategory category) {
		
		class FormulaPair {

			String original;
			String rewritten;
			
			public FormulaPair(String original, String rewritten) {
				this.original = original;
				this.rewritten = rewritten;
			}
			
			@Override
			public int hashCode() {
				return Objects.hash(original, rewritten);
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				FormulaPair other = (FormulaPair) obj;
				return Objects.equals(original, other.original) && Objects.equals(rewritten, other.rewritten);
			}
		}
		
		List<Object[]> rows = new ArrayList<>();
		
		Map<FormulaPair, List<Double>> originalResults = new HashMap<>();
		Map<FormulaPair, List<Boolean>> originalCorrect = new HashMap<>();
		
		Map<FormulaPair, List<Double>> rewrittenResults = new HashMap<>();
		Map<FormulaPair, List<Boolean>> rewrittenCorrect = new HashMap<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
        	String line;
        	
        	// Read first line of column headers
        	line = br.readLine();
        	String[] parts = line.split(",");
        	int index = 0;
        	for (int i = 0; i < parts.length; i++) {
        		if (parts[i].equals(category + "_original_time")) {
        			index = i;
        			break;
        		}
        	}
            
            while ((line = br.readLine()) != null) {
                parts = line.split(",");

                Instant timestamp = Instant.parse(parts[1]);
                
                if (LocalDate.ofInstant(timestamp, ZoneId.systemDefault()).isBefore(LocalDate.of(2024, 2, 5)) ||
                		parts[index] == null || parts[index].isEmpty() ||
                		parts[index+4] == null || parts[index+4].isEmpty()) continue;
                	
            	Double originalTime = Double.parseDouble(parts[index]);
            	Double rewrittenTime = Double.parseDouble(parts[index+4]);

            	// Sanity check
            	if (originalTime > MAX_TIME || rewrittenTime > MAX_TIME || originalTime < MIN_TIME || rewrittenTime < MIN_TIME) continue;
            	
            	Boolean originalIsCorrect = Boolean.parseBoolean(parts[index+1]);
            	Boolean rewrittenIsCorrect = Boolean.parseBoolean(parts[index+5]);
            	
            	String originalFormula = parts[index+3];
            	String rewrittenFormula = parts[index+7];
            	
            	FormulaPair formulaPair = new FormulaPair(originalFormula, rewrittenFormula);
            	
            	if (!originalResults.containsKey(formulaPair)) {
            		originalResults.put(formulaPair, new ArrayList<>());
            	}
            	if (!originalCorrect.containsKey(formulaPair)) {
            		originalCorrect.put(formulaPair, new ArrayList<>());
            	}
            	if (!rewrittenResults.containsKey(formulaPair)) {
            		rewrittenResults.put(formulaPair, new ArrayList<>());
            	}
            	if (!rewrittenCorrect.containsKey(formulaPair)) {
            		rewrittenCorrect.put(formulaPair, new ArrayList<>());
            	}
            			
                originalResults.get(formulaPair).add(originalTime);
                originalCorrect.get(formulaPair).add(originalIsCorrect);
                rewrittenResults.get(formulaPair).add(rewrittenTime);
                rewrittenCorrect.get(formulaPair).add(rewrittenIsCorrect);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		for (FormulaPair formulaPair : originalResults.keySet()) {
			
			Object[] row = new Object[formulaHeaders.length];
			row[0] = category;
			
			List<Double> results = originalResults.get(formulaPair);
			double sum = sum(results);
			double mean = sum / results.size();
			double median = median(results);
			double stdv = standardDeviation(results, mean);
			long correct = correctNum(originalCorrect.get(formulaPair));
			
			row[1] = formulaPair.original;
			row[2] = results.size();
			row[3] = median;
			row[4] = mean;
			row[5] = stdv;
			row[6] = correct;
			
			results = rewrittenResults.get(formulaPair);
			sum = sum(results);
			mean = sum / results.size();
			median = median(results);
			stdv = standardDeviation(results, mean);
			correct = correctNum(rewrittenCorrect.get(formulaPair));
			
			row[7] = formulaPair.rewritten;
			row[8] = results.size();
			row[9] = median;
			row[10] = mean;
			row[11] = stdv;
			row[12] = correct;
			
			rows.add(row);
		}
		
		return rows;
		
	}
	
	private static long correctNum(List<Boolean> correct) {
		return correct.stream().filter(c -> c.equals(Boolean.TRUE)).count();
	}

	private static Object[] analyzeCategory(TestCategory category, boolean twoSided) {
		
		Object[] row = new Object[generalHeaders.length];
		
		// We're comparing just one pair of formulas
		// In other words, we treat all formulas that belong to a single category as a one pair
		
		List<Double> originalResults = new ArrayList<>();
		List<Double> rewrittenResults = new ArrayList<>();
		List<Boolean> originalCorrect = new ArrayList<>();
		List<Boolean> rewrittenCorrect = new ArrayList<>();
		
		List<Double> diff = new ArrayList<>();
		List<Double> correctnessDiff = new ArrayList<>();

		System.out.println(category);
		row[0] = category;
		
		System.out.println("Is two-sided test= " + twoSided);
		
		int originalCorrectRewrittenWrong = 0;
		int originalWrongRewrittenCorrect = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
        	String line;
        	
        	// Read first line of column headers
        	line = br.readLine();
        	String[] parts = line.split(",");
        	int index = 0;
        	for (int i = 0; i < parts.length; i++) {
        		if (parts[i].equals(category + "_original_time")) {
        			index = i;
        			break;
        		}
        	}
            
            while ((line = br.readLine()) != null) {
                parts = line.split(",");

                Instant timestamp = Instant.parse(parts[1]);
                
//                // Discard first question
//                if (Integer.parseInt(parts[index+2]) == 0 || Integer.parseInt(parts[index+6]) == 0) {
//                	continue;
//                }
                
                if (LocalDate.ofInstant(timestamp, ZoneId.systemDefault()).isBefore(LocalDate.of(2024, 2, 5)) ||
                		parts[index] == null || parts[index].isEmpty() ||
                		parts[index+4] == null || parts[index+4].isEmpty()) continue;
                	
            	Double originalTime = Double.parseDouble(parts[index]);
            	Double rewrittenTime = Double.parseDouble(parts[index+4]);
            	
            	// Sanity check
            	if (originalTime > MAX_TIME || rewrittenTime > MAX_TIME || originalTime < MIN_TIME || rewrittenTime < MIN_TIME) continue;
            	
            	averageTime += originalTime;
            	averageTime += rewrittenTime;
            	
            	questions += 2;
            	
            	Boolean originalIsCorrect = Boolean.parseBoolean(parts[index+1]);
            	Boolean rewrittenIsCorrect = Boolean.parseBoolean(parts[index+5]);
            	
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
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        analyze(twoSided, row, originalResults, rewrittenResults, originalCorrect, rewrittenCorrect, diff,
				correctnessDiff, originalCorrectRewrittenWrong, originalWrongRewrittenCorrect);
        
        return row;
	}

	public static void analyze(boolean twoSided, Object[] row, List<Double> originalResults,
			List<Double> rewrittenResults, List<Boolean> originalCorrect, List<Boolean> rewrittenCorrect,
			List<Double> diff, List<Double> correctnessDiff, int originalCorrectRewrittenWrong,
			int originalWrongRewrittenCorrect) {
		
		Random r = new Random();
		int dataPoints = diff.size();
        System.out.println("Data points= " + dataPoints);
        row[9] = dataPoints;
		
        List<Double> filteredDiff = new ArrayList<>();
        List<Double> filteredOriginal = new ArrayList<>();
        List<Double> filteredRewritten = new ArrayList<>();
		for (int i = 0; i < dataPoints; i++) {
			if (originalCorrect.get(i) && rewrittenCorrect.get(i)) {
				filteredDiff.add(diff.get(i));
				filteredOriginal.add(originalResults.get(i));
				filteredRewritten.add(rewrittenResults.get(i));
			}
		}
		
		int correctDataPoints = filteredDiff.size();
		System.out.println("Correct data points= " + correctDataPoints);
		System.out.println();
		row[10] = correctDataPoints;
		
		double originalMedian = median(filteredOriginal);
		double originalSum = sum(filteredOriginal);
		double originalMean = originalSum / correctDataPoints;
		double originalStdv = standardDeviation(filteredOriginal, originalMean);
		long originalCorrectCount = correctNum(originalCorrect);
		System.out.println("Original median= " + originalMedian);
		System.out.println("Original mean= " + originalMean);
		System.out.println("Original stdv= " + originalStdv);
		System.out.println("Original correct= " + originalCorrectCount);
		row[1] = originalMedian / 1000;
		row[2] = originalMean / 1000;
		row[3] = originalStdv / 1000;
		row[4] = originalCorrectCount;
		
		double rewrittenMedian = median(filteredRewritten);
		double rewrittenSum = sum(filteredRewritten);
		double rewrittenMean = rewrittenSum / correctDataPoints;
		double rewrittenStdv = standardDeviation(filteredRewritten, rewrittenMean);
		long rewrittenCorrectCount = correctNum(rewrittenCorrect);
		System.out.println("Rewritten median= " + rewrittenMedian);
		System.out.println("Rewritten mean= " + rewrittenMean);
		System.out.println("Rewritten stdv= " + rewrittenStdv);
		System.out.println("Rewritten correct= " + rewrittenCorrectCount);
		System.out.println();
		row[5] = rewrittenMedian / 1000;
		row[6] = rewrittenMean / 1000;
		row[7] = rewrittenStdv / 1000;
		row[8] = rewrittenCorrectCount;
		
		double diffSum = sum(filteredDiff);
		double diffMean = diffSum / correctDataPoints;
		double diffStdv = standardDeviation(filteredDiff, diffMean);
		System.out.println("Diff mean= " + diffMean);
		System.out.println("Diff stdv= " + diffStdv);
		
		double correctnessDiffSum = sum(correctnessDiff);
		double correctnessDiffMean = correctnessDiffSum / dataPoints;
		double correctnessDiffStdv = standardDeviation(correctnessDiff, correctnessDiffMean);
		System.out.println("Correctness diff mean= " + correctnessDiffMean);
		System.out.println("Correctness diff stdv= " + correctnessDiffStdv);
		System.out.println();

		double t = calculateT(diffSum, filteredDiff);
		System.out.println("t= " + t);
		double correctnessT = calculateT(correctnessDiffSum, correctnessDiff);
		System.out.println("Correctness t= " + correctnessT);
		
		
		// Wilcoxon computation
		
		double wilcoxon = wilcoxon(filteredDiff);
	    System.out.println("Wilcoxon= " + wilcoxon);
	    System.out.println();
		
		
		List<Double> empiricalT = new ArrayList<>();
		List<Double> empiricalWilcoxon = new ArrayList<>();
		List<Double> empiricalCorrectness = new ArrayList<>();
		
		for (int k = 0; k < EMPIRICAL_RANDOM_SIZE; k++) {
			
	        List<Double> tempDiff = new ArrayList<>();
	        List<Double> tempCorrectnessDiff = new ArrayList<>();
	        
			for (int i = 0; i < dataPoints; i++) {
				
				Double currDiff = diff.get(i);
				Double currCorrectnessDiff = correctnessDiff.get(i);
				
				if (r.nextBoolean()) {
					currDiff *= -1;
					currCorrectnessDiff *= -1;
				}
				tempCorrectnessDiff.add(currCorrectnessDiff);
				tempDiff.add(currDiff);

			}
			
			List<Double> tempFilteredDiff = new ArrayList<>();
			for (int i = 0; i < dataPoints; i++) {
				if (originalCorrect.get(i) && rewrittenCorrect.get(i)) {
					tempFilteredDiff.add(tempDiff.get(i));
				}
			}
			
			double tempSum = sum(tempFilteredDiff);
			double tempCorrectnessSum = sum(tempCorrectnessDiff);
			
			double currT = calculateT(tempSum, tempFilteredDiff);
			double currWilcoxon = wilcoxon(tempFilteredDiff);
			double currCorrectness = calculateT(tempCorrectnessSum, tempCorrectnessDiff);
			
			if (twoSided) {
				currT = Math.abs(currT);
				currWilcoxon = Math.abs(currWilcoxon);
				currCorrectness = Math.abs(currCorrectness);
			}
			
			empiricalT.add(currT);
			empiricalWilcoxon.add(currWilcoxon);
			empiricalCorrectness.add(currCorrectness);

		}
		
		
		if (twoSided) {
			t = Math.abs(t);
			wilcoxon = Math.abs(wilcoxon);
			correctnessT = Math.abs(correctnessT);
		}
		
		
		int biggerT = 0;
		int biggerWilcoxon = 0;
		int biggerCorrectnessT = 0;
		for (int i = 0; i < EMPIRICAL_RANDOM_SIZE; i++) {
			if (empiricalT.get(i) > t) {
				biggerT++;
			}
			if (empiricalWilcoxon.get(i) > wilcoxon) {
				biggerWilcoxon++;
			}
			if (empiricalCorrectness.get(i) > correctnessT) {
				biggerCorrectnessT++;
			}
		}
		
		double cohensD = cohensd(originalMean, rewrittenMean, originalStdv, rewrittenStdv);
		System.out.println("Cohen's d= " + cohensD);
		row[11] = cohensD;
		
		double pValueT = ((double) biggerT) / EMPIRICAL_RANDOM_SIZE;
		double pValueWilcoxon = ((double) biggerWilcoxon) / EMPIRICAL_RANDOM_SIZE;
		double pValueCorrectness = ((double) biggerCorrectnessT) / EMPIRICAL_RANDOM_SIZE;
		
		System.out.println("p-value permutation fraction= " + pValueT);
		System.out.println("p-value wilcoxon fraction= " + pValueWilcoxon);
		System.out.println("p-value correctness fraction= " + pValueCorrectness);
		row[12] = pValueT;
		row[13] = pValueWilcoxon;
		row[14] = pValueCorrectness;
		
		double mcnemar = Math.pow(originalCorrectRewrittenWrong - originalWrongRewrittenCorrect, 2) / 
				(originalCorrectRewrittenWrong + originalWrongRewrittenCorrect);
		System.out.println("mcnemar correctness= " + mcnemar);
		row[15] = mcnemar;
	}
	
	public static double median(List<Double> arr) {
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
	
	public static double wilcoxon(List<Double> arr) {
		
		List<Double> sortedArr = new ArrayList<>(arr).stream().map(d -> Math.abs(d)).sorted().toList();
		
		List<Double> wilcoxonArr = new ArrayList<>();
	    
	    double wilcoxon = 0.0;
	    for (int i = 0; i < sortedArr.size(); i++) {
	    	
	    	int sgn = 1;
	    	for (int k = 0; k < arr.size(); k++) {
	    		if (Math.abs(arr.get(k)) == sortedArr.get(i)) {
	    			sgn = arr.get(k) > 0 ? 1 : -1;
	    			break;
	    		}
	    	}
	    	wilcoxon += sgn * (i + 1);
	    	wilcoxonArr.add((double) (sgn * (i + 1)));
	    }
	    
	    return calculateT(wilcoxon, wilcoxonArr);
	}
	
	public static double sum(List<Double> arr) {
		double sum = 0.0;
	    for (Double num : arr) {
	    	sum += num;
	    }
	    return sum;
	}
	
	public static double calculateT(double sum, List<Double> diff) {
		double mean = sum / diff.size();
		return (Math.sqrt(diff.size()) * mean) / standardDeviation(diff, mean);
	}
	
	public static double standardDeviation(List<Double> arr, double mean) {
		double sum = 0.0;
	    for (Double num : arr) {
	    	sum += Math.pow(num - mean, 2);
	    }
		return Math.sqrt(sum / arr.size());
	}
	
	public static double cohensd(double mean1, double mean2, double stdv1, double stdv2) {
		return (mean1 - mean2) / Math.sqrt(Math.pow(stdv1, 2) + Math.pow(stdv2, 2));
	}
}
