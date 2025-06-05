package question;

import java.util.Map;

import formula.Formula;

public class Question {
	
	public Map<String, Boolean> assignment;
	public FormulaPair pair;
	public boolean isOriginal;
	public TestCategory category;
	public Boolean result;
	public double time;
	
	public Formula getFormula() {
		return isOriginal ? pair.original : pair.rewritten;
	}
	
	public String getText() {
		
		return String.format("Given the following logical formula %s, what is the outcome of the following assignment %s", 
				getFormula().toString(), assignment.toString());
	}
}