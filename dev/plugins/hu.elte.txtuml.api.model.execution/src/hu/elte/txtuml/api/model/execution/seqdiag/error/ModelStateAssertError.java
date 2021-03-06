package hu.elte.txtuml.api.model.execution.seqdiag.error;

import hu.elte.txtuml.api.model.impl.SequenceDiagramRelated;

/**
 * Assertion error, which occurs when the actual state of a model class does not
 * match with the expected state.
 */
@SequenceDiagramRelated
public class ModelStateAssertError extends SequenceDiagramProblem {

	public ModelStateAssertError(String expected, String actual) {
		super(createErrorMessage(expected, actual), ErrorLevel.ERROR);
	}

	private static String createErrorMessage(String expected, String actual) {
		StringBuilder builder = new StringBuilder("\nState assertion error");
		builder.append("\nExpected: ").append(expected).append("\nActual:   ").append(actual);
		return builder.append("\n").toString();
	}

}
