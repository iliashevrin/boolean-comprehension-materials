// Generated from RandomTestGenerator/src/language/BooleanFormula.g4 by ANTLR 4.13.1

package language;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link BooleanFormulaVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
@SuppressWarnings("CheckReturnValue")
public class BooleanFormulaBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements BooleanFormulaVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitFormula(BooleanFormulaParser.FormulaContext ctx) { return visitChildren(ctx); }
}