package entrypoints;

import formula.Formula;
import formula.RefactorTool;
import language.ParserHelper;

public class FormulaScoreCalculator {

	public static void main(String[] args) {

		Formula fromString = ParserHelper.parse(args[0]);
		System.out.printf("%,.2f", RefactorTool.score(fromString));

	}

}
