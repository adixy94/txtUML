package hu.elte.txtuml.validation.model.visitors;

import static hu.elte.txtuml.validation.model.ModelErrors.INVALID_ATTRIBUTE_TYPE;
import static hu.elte.txtuml.validation.model.ModelErrors.INVALID_MODEL_CLASS_ELEMENT;
import static hu.elte.txtuml.validation.model.ModelErrors.INVALID_PARAMETER_TYPE;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import hu.elte.txtuml.utils.jdt.ElementTypeTeller;
import hu.elte.txtuml.validation.common.ProblemCollector;

public class ModelClassVisitor extends VisitorBase {

	public static final Class<?>[] ALLOWED_MODEL_CLASS_DECLARATIONS = new Class<?>[] { TypeDeclaration.class,
			FieldDeclaration.class, MethodDeclaration.class, SimpleName.class, SimpleType.class, Modifier.class,
			Annotation.class, Javadoc.class };

	public ModelClassVisitor(ProblemCollector collector) {
		super(collector);
	}

	@Override
	public boolean visit(TypeDeclaration elem) {
		if (ElementTypeTeller.isExternal(elem)) {
			return false;
		}

		if (ElementTypeTeller.isVertex(elem) || ElementTypeTeller.isTransition(elem)) {
			handleStateMachineElements(elem);
		} else if (ElementTypeTeller.isPort(elem)) {
			// TODO: check port
		} else {
			collector.report(INVALID_MODEL_CLASS_ELEMENT.create(collector.getSourceInfo(), elem));
		}
		return false;
	}

	@Override
	public boolean visit(FieldDeclaration elem) {
		if (ElementTypeTeller.isExternal(elem)) {
			return false;
		}

		if (!Utils.isAllowedAttributeType(elem.getType().resolveBinding(), false)) {
			collector.report(INVALID_ATTRIBUTE_TYPE.create(collector.getSourceInfo(), elem.getType()));
		} else {
			Utils.checkModifiers(collector, elem);
		}
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration elem) {
		if (ElementTypeTeller.isExternal(elem)) {
			return false;
		}

		if (!elem.isConstructor()) {
			if (elem.getReturnType2() != null
					&& !Utils.isAllowedParameterType(elem.getReturnType2().resolveBinding(), true)) {
				collector.report(INVALID_PARAMETER_TYPE.create(collector.getSourceInfo(), elem.getReturnType2()));
			}
		}

		Utils.checkModifiers(collector, elem, m -> m.isStatic());
		for (Object obj : elem.parameters()) {
			SingleVariableDeclaration param = (SingleVariableDeclaration) obj;
			if (!Utils.isAllowedParameterType(param.getType().resolveBinding(), false)) {
				collector.report(INVALID_PARAMETER_TYPE.create(collector.getSourceInfo(), param.getType()));
			}
		}

		if (ElementTypeTeller.hasExternalBody(elem)) {
			return false;
		}

		// TODO: check body
		return false;
	}
}
