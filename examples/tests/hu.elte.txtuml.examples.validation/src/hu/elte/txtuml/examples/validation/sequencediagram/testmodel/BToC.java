package hu.elte.txtuml.examples.validation.sequencediagram.testmodel;

import hu.elte.txtuml.api.model.Association;
import hu.elte.txtuml.api.model.One;

public class BToC extends Association {
	public class BSide extends End<One<B>> {
	}

	public class CSide extends End<One<C>> {
	}
}
