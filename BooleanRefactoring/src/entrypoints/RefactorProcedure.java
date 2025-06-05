package entrypoints;

import java.util.List;

import formula.Formula;
import formula.RefactorTool;
import language.ParserHelper;

public class RefactorProcedure {

	public static void main(String[] args) {
		
		Formula fromString = ParserHelper.parse(args[0]);
		int maxReturn = Integer.parseInt(args[1]);
		List<Formula> result = RefactorTool.refactorWithPriorityQueue(fromString, maxReturn);
		for (Formula f : result) {
			System.out.println(String.format("%s = %,.2f", f, RefactorTool.score(f)));
		}
		
	}

}
