// Generated from RandomTestGenerator/src/language/BooleanFormula.g4 by ANTLR 4.13.1

package language;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BooleanFormulaParser}.
 */
public interface BooleanFormulaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BooleanFormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormula(BooleanFormulaParser.FormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link BooleanFormulaParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormula(BooleanFormulaParser.FormulaContext ctx);
}