package scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import formula.Formula;
import formula.RefactorTool;
import language.ParserHelper;

public class FormulasForFollowup {

	public static void main(String[] args) throws IOException {

        String file1 = args[0];

        Set<Formula> firstFileData = readFirstColumn(file1);
        
        FileWriter writer = new FileWriter("formulas_followup.csv");

        // Write header
        writer.append("Original,Refactoring,Improvement,Original Score,Refactoring Score\n");
        
        for (Formula formula : firstFileData) {
        	Formula result = RefactorTool.refactorWithPriorityQueue(formula).get(0);
    		
    		Double origScore = RefactorTool.score(formula);
    		Double resultScore = RefactorTool.score(result);
    		
    		writer.append(formula.toString());
    		writer.append(',');
    		writer.append(result.toString());
    		writer.append(',');
    		writer.append(String.valueOf(resultScore / origScore));
    		writer.append(',');
    		writer.append(String.valueOf(origScore));
    		writer.append(',');
    		writer.append(String.valueOf(resultScore));
    		writer.append('\n');
        }
        
        writer.flush();
        writer.close();
	}

	
    private static Set<Formula> readFirstColumn(String fileName) {
        Set<Formula> data = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length > 0) {
                    data.add(ParserHelper.parse(columns[0].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + fileName);
            e.printStackTrace();
        }
        return data;
    }

}
