package hu.elte.txtuml.layout.lang.statements;

import hu.elte.txtuml.layout.lang.elements.LayoutElement;
import hu.elte.txtuml.layout.lang.elements.LayoutNode;
import hu.elte.txtuml.layout.lang.statements.containers.SouthContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(SouthContainer.class)
public @interface South {
	Class<? extends LayoutElement> val();

	Class<? extends LayoutNode> from();

}
