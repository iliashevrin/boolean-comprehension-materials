package language;
  
import java.util.ArrayList;

import formula.Formula;
import language.BooleanFormulaParser.FormulaContext;
  
public class FormulaVisitor extends BooleanFormulaBaseVisitor<Formula> {
  
    @Override
    public Formula visitFormula(FormulaContext ctx) { 
         
        if (ctx.literal != null) {
            return new Formula(ctx.negated != null, ctx.literal.getText());
        }
         
        if (ctx.elements != null) {
            return new Formula(ctx.negated != null, new ArrayList<>(ctx.elements.stream().map(e -> visit(e)).toList()), ctx.op.getText());
        }
         
        throw new IllegalArgumentException();
    }
}
