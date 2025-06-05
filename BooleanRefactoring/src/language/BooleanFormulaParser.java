// Generated from RandomTestGenerator/src/language/BooleanFormula.g4 by ANTLR 4.13.1

package language;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class BooleanFormulaParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, CONST=8, WS=9;
	public static final int
		RULE_formula = 0;
	private static String[] makeRuleNames() {
		return new String[] {
			"formula"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'!'", "'('", "'|'", "'&'", "'->'", "'<->'", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "CONST", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "BooleanFormula.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public BooleanFormulaParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FormulaContext extends ParserRuleContext {
		public Token negated;
		public Token literal;
		public FormulaContext formula;
		public List<FormulaContext> elements = new ArrayList<FormulaContext>();
		public Token op;
		public TerminalNode CONST() { return getToken(BooleanFormulaParser.CONST, 0); }
		public List<FormulaContext> formula() {
			return getRuleContexts(FormulaContext.class);
		}
		public FormulaContext formula(int i) {
			return getRuleContext(FormulaContext.class,i);
		}
		public FormulaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formula; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BooleanFormulaListener ) ((BooleanFormulaListener)listener).enterFormula(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BooleanFormulaListener ) ((BooleanFormulaListener)listener).exitFormula(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BooleanFormulaVisitor ) return ((BooleanFormulaVisitor<? extends T>)visitor).visitFormula(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormulaContext formula() throws RecognitionException {
		FormulaContext _localctx = new FormulaContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_formula);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(3);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(2);
				((FormulaContext)_localctx).negated = match(T__0);
				}
			}

			setState(16);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CONST:
				{
				setState(5);
				((FormulaContext)_localctx).literal = match(CONST);
				}
				break;
			case T__1:
				{
				{
				setState(6);
				match(T__1);
				setState(7);
				((FormulaContext)_localctx).formula = formula();
				((FormulaContext)_localctx).elements.add(((FormulaContext)_localctx).formula);
				setState(10); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(8);
					((FormulaContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 120L) != 0)) ) {
						((FormulaContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(9);
					((FormulaContext)_localctx).formula = formula();
					((FormulaContext)_localctx).elements.add(((FormulaContext)_localctx).formula);
					}
					}
					setState(12); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 120L) != 0) );
				setState(14);
				match(T__6);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\t\u0013\u0002\u0000\u0007\u0000\u0001\u0000\u0003\u0000\u0004"+
		"\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0004"+
		"\u0000\u000b\b\u0000\u000b\u0000\f\u0000\f\u0001\u0000\u0001\u0000\u0003"+
		"\u0000\u0011\b\u0000\u0001\u0000\u0000\u0000\u0001\u0000\u0000\u0001\u0001"+
		"\u0000\u0003\u0006\u0014\u0000\u0003\u0001\u0000\u0000\u0000\u0002\u0004"+
		"\u0005\u0001\u0000\u0000\u0003\u0002\u0001\u0000\u0000\u0000\u0003\u0004"+
		"\u0001\u0000\u0000\u0000\u0004\u0010\u0001\u0000\u0000\u0000\u0005\u0011"+
		"\u0005\b\u0000\u0000\u0006\u0007\u0005\u0002\u0000\u0000\u0007\n\u0003"+
		"\u0000\u0000\u0000\b\t\u0007\u0000\u0000\u0000\t\u000b\u0003\u0000\u0000"+
		"\u0000\n\b\u0001\u0000\u0000\u0000\u000b\f\u0001\u0000\u0000\u0000\f\n"+
		"\u0001\u0000\u0000\u0000\f\r\u0001\u0000\u0000\u0000\r\u000e\u0001\u0000"+
		"\u0000\u0000\u000e\u000f\u0005\u0007\u0000\u0000\u000f\u0011\u0001\u0000"+
		"\u0000\u0000\u0010\u0005\u0001\u0000\u0000\u0000\u0010\u0006\u0001\u0000"+
		"\u0000\u0000\u0011\u0001\u0001\u0000\u0000\u0000\u0003\u0003\f\u0010";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}