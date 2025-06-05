package scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunFollowupAnalysis {

	public static void main(String[] args) throws IOException {
		

		String csvFile = args[0];
		
		Map<Integer, Integer> originalIndexes = new HashMap<>();
		Map<Integer, Integer> rewrittenIndexes = new HashMap<>();
		
		List<Double> originalResults = new ArrayList<>();
		List<Double> rewrittenResults = new ArrayList<>();
		List<Boolean> originalCorrect = new ArrayList<>();
		List<Boolean> rewrittenCorrect = new ArrayList<>();
		
		List<Double> diff = new ArrayList<>();
		List<Double> correctnessDiff = new ArrayList<>();
		
		List<Double> originalAvgResults = new ArrayList<>();
		List<Double> rewrittenAvgResults = new ArrayList<>();
		List<Double> originalCorrectRatio = new ArrayList<>();
		List<Double> rewrittenCorrectRatio = new ArrayList<>();
		List<Double> diffAvgResults = new ArrayList<>();
		List<Double> diffCorrectRatio = new ArrayList<>();
		
		int originalCorrectRewrittenWrong = 0;
		int originalWrongRewrittenCorrect = 0;
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
        	String line;
        	
        	// Read first line of column headers
        	line = br.readLine();
        	
        	String[] parts = line.split(",");
        	int length = parts.length;
        	
        	for (int index = 0; index < parts.length; index++) {
        		Matcher m = Pattern.compile("(\\d)_rewritten_time").matcher(parts[index]);
        		if (m.matches()) {
        			int pairIndex = Integer.parseInt(m.group(1));
        			rewrittenIndexes.put(index, pairIndex);
        			continue;
        		}
        		m = Pattern.compile("(\\d)_original_time").matcher(parts[index]);
        		if (m.matches()) {
        			int pairIndex = Integer.parseInt(m.group(1));
        			originalIndexes.put(index, pairIndex);
        		}
        	}

            while ((line = br.readLine()) != null) {
            	
            	parts = new String[length];
            	Arrays.fill(parts, "");

                String[] tempParts = line.split(",");
                
	            for (int i = 0; i < tempParts.length; i++) {
	            	parts[i] = tempParts[i];
	            }

                Instant timestamp = Instant.parse(parts[1]);
                
                if (LocalDate.ofInstant(timestamp, ZoneId.systemDefault()).isBefore(LocalDate.of(2025, 3, 24))) continue;
                
                double originalAvg = 0;
                double rewrittenAvg = 0;
                double originalCorrectCount = 0;
                double rewrittenCorrectCount = 0;
                int bothCorrectCount = 0;
                int allDatapoints = 0;
                
                
                for (int index = 8; index <= parts.length; index++) {
                	
                	if (!originalIndexes.containsKey(index) && !rewrittenIndexes.containsKey(index)) {
                		continue;
                	}
                	
                	final int originalTimeIndex, originalCorrectIndex, rewrittenTimeIndex, rewrittenCorrectIndex;
                	
                	if (originalIndexes.containsKey(index)) {
                		originalTimeIndex = index;
                		originalCorrectIndex = originalTimeIndex + 1;
                		
                		rewrittenTimeIndex = rewrittenIndexes.keySet().stream().filter(i -> rewrittenIndexes.get(i) == originalIndexes.get(originalTimeIndex)).findFirst().orElseThrow();
                		rewrittenCorrectIndex = rewrittenTimeIndex + 1;
                		
                		// Already visited
                		if (rewrittenTimeIndex < originalTimeIndex) {
                			continue;
                		}
                		
                	} else if (rewrittenIndexes.containsKey(index)) {
                		rewrittenTimeIndex = index;
                		rewrittenCorrectIndex = rewrittenTimeIndex + 1;
                		
                		originalTimeIndex = originalIndexes.keySet().stream().filter(i -> originalIndexes.get(i) == rewrittenIndexes.get(rewrittenTimeIndex)).findFirst().orElseThrow();
                		originalCorrectIndex = originalTimeIndex + 1;
                		
                		// Already visited
                		if (originalTimeIndex < rewrittenTimeIndex) {
                			continue;
                		}
                		
                	} else {
                		continue;
                	}
                	
                	if (parts[originalTimeIndex].isEmpty() || parts[rewrittenTimeIndex].isEmpty() || parts[originalCorrectIndex].isEmpty() || parts[rewrittenCorrectIndex].isEmpty()) {
                		continue;
                	}
            		
                	Double originalTime = Double.parseDouble(parts[originalTimeIndex]);
                	Double rewrittenTime = Double.parseDouble(parts[rewrittenTimeIndex]);
                	
                	// Sanity check
                	if (originalTime > RunStatisticalAnalysis.MAX_TIME || rewrittenTime > RunStatisticalAnalysis.MAX_TIME || 
                			originalTime < RunStatisticalAnalysis.MIN_TIME || rewrittenTime < RunStatisticalAnalysis.MIN_TIME) continue;

                	
                	Boolean originalIsCorrect = Boolean.parseBoolean(parts[originalCorrectIndex]);
                	Boolean rewrittenIsCorrect = Boolean.parseBoolean(parts[rewrittenCorrectIndex]);
                	
                    originalResults.add(originalTime);
                    originalCorrect.add(originalIsCorrect);
                    rewrittenResults.add(rewrittenTime);
                    rewrittenCorrect.add(rewrittenIsCorrect);
                    
                    if (rewrittenIsCorrect && originalIsCorrect) {
                    	originalAvg += originalTime;
                    	rewrittenAvg += rewrittenTime;
                    	bothCorrectCount += 1;
                    }
                    originalCorrectCount += originalIsCorrect ? 1 : 0;
                    rewrittenCorrectCount += rewrittenIsCorrect ? 1 : 0;
                    allDatapoints += 1;

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
                
                if (bothCorrectCount > 0) {
                    originalAvgResults.add(originalAvg / bothCorrectCount);
                    rewrittenAvgResults.add(rewrittenAvg / bothCorrectCount);
                    diffAvgResults.add((originalAvg / bothCorrectCount) - (rewrittenAvg / bothCorrectCount));
                }
                
                if (allDatapoints > 0) {
                	originalCorrectRatio.add(100 * originalCorrectCount / allDatapoints);
                    rewrittenCorrectRatio.add(100 * rewrittenCorrectCount / allDatapoints);
                    diffCorrectRatio.add(100 * ((originalCorrectCount / allDatapoints) - (rewrittenCorrectCount / allDatapoints)));
                } 
            }
        }
		
		// Separate datapoints
        Object[] row = new Object[RunStatisticalAnalysis.generalHeaders.length];
        row[0] = "Refactoring Tool";
        
        RunStatisticalAnalysis.analyze(true, row, originalResults, rewrittenResults, originalCorrect, rewrittenCorrect, 
        		diff, correctnessDiff, originalCorrectRewrittenWrong, originalWrongRewrittenCorrect);
        
        System.out.println();
        for (int i = 0; i < row.length; i++) {
        	System.out.println(String.format("  %s=%s", RunStatisticalAnalysis.generalHeaders[i], row[i]));
        }
        System.out.println();
        
        // Single participant time to correct datapoints
        Object[] row2 = new Object[RunStatisticalAnalysis.generalHeaders.length];
        row2[0] = "Refactoring Tool (by single participant) (time to correct)";
        
        List<Boolean> takeAll = new ArrayList<>();
        List<Double> noDiff = new ArrayList<>();
        for (int i = 0; i < diffAvgResults.size(); i++) {
        	takeAll.add(true);
        	noDiff.add(0d);
        }
        
        RunStatisticalAnalysis.analyze(true, row2, originalAvgResults, rewrittenAvgResults, takeAll, takeAll, 
        		diffAvgResults, noDiff, 0, 0);
        
        System.out.println();
        for (int i = 0; i < row2.length; i++) {
        	System.out.println(String.format("  %s=%s", RunStatisticalAnalysis.generalHeaders[i], row2[i]));
        }
        System.out.println();
        
        // Single participant correct ratio datapoints
        Object[] row3 = new Object[RunStatisticalAnalysis.generalHeaders.length];
        row3[0] = "Refactoring Tool (by single participant) (correct ratio)";
        
        takeAll = new ArrayList<>();
        noDiff = new ArrayList<>();
        for (int i = 0; i < diffCorrectRatio.size(); i++) {
        	takeAll.add(true);
        	noDiff.add(0d);
        }
        
        RunStatisticalAnalysis.analyze(true, row3, originalCorrectRatio, rewrittenCorrectRatio, takeAll, takeAll, 
        		diffCorrectRatio, noDiff, 0, 0);
        
        System.out.println();
        for (int i = 0; i < row3.length; i++) {
        	System.out.println(String.format("  %s=%s", RunStatisticalAnalysis.generalHeaders[i], row3[i]));
        }
        System.out.println();

	}

}
