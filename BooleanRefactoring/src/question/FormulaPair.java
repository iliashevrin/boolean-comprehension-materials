package question;

import formula.Formula;

public class FormulaPair {
	public Formula original;
	public Formula rewritten;
	
	public FormulaPair(Formula original, Formula rewritten) {
		this.original = original;
		this.rewritten = rewritten;
	}
}