package language;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import formula.Formula;

public class ParserHelper {

	public static Formula parse(String cell) {
		
		if ("".equals(cell) || "TODO".equals(cell)) return null;
		
		BooleanFormulaLexer lexer = new BooleanFormulaLexer(CharStreams.fromString(cell));
		CommonTokenStream stream = new CommonTokenStream(lexer);
	  	BooleanFormulaParser parser = new BooleanFormulaParser(stream);
	  	FormulaVisitor visitor = new FormulaVisitor();
	    return visitor.visit(parser.formula());
	}

}
