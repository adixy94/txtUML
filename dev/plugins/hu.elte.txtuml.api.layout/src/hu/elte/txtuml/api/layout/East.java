package hu.elte.txtuml.api.layout;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import hu.elte.txtuml.api.layout.containers.EastContainer;

/**
 * A diagram layout statement which sets that some nodes and/or node groups (
 * {@link #val}) are somewhere right to other nodes and/or node groups (
 * {@link #from}).
 * <p>
 * May also mean that some links and/or link groups ({@link #val}) connect to
 * the right side of a node ({@link #from}). If {@link #val} is a single
 * reflexive link, {@link #end} may specify which end of that link is set in
 * this statement.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EastContainer.class)
public @interface East {
	Class<?>[] val();

	Class<?>[] from();

	LinkEnd end() default LinkEnd.Default;

}
