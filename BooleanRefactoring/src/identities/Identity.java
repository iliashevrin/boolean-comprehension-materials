package identities;

import formula.Formula;

public interface Identity {

	Formula rewrite(Formula formula);
}
