package txtuml.importer;

import java.lang.reflect.*;
import java.util.LinkedList;

import org.aspectj.lang.Signature;

import txtuml.importer.InstructionImporter;
import txtuml.api.*;

public privileged aspect ImporterAspect {
	private pointcut withinProject() : within(txtuml..*) && !within(txtuml.examples..*); // TODO relevant only until the 'examples' package exists
	private pointcut withinModel() : within(ModelElement+) && !within(ExternalClass+) && !within(txtuml.api..*);
	private pointcut importing() : if(MethodImporter.isImporting());
	private pointcut isActive() : withinModel() && importing();
	
	Object around(ModelClass target): target(target) && call(* *(..)) && isActive() {
		return InstructionImporter.call(target, thisJoinPoint.getSignature().getName(),thisJoinPoint.getArgs());
	}
	
	Object around(ExternalClass target) : target(target) && call(* (ExternalClass+).*(..)) && isActive() {
		return InstructionImporter.callExternal(target, thisJoinPoint.getSignature().getName(), thisJoinPoint.getArgs());
	}

	Object around() : call(static * (ExternalClass+).*(..)) && isActive() {
		return InstructionImporter.callStaticExternal(thisJoinPoint.getSignature().getDeclaringType(), thisJoinPoint.getSignature().getName(), thisJoinPoint.getArgs());
	}

	
	Object around() : call(* (!ModelElement+).*(..)) && !call(* (java.lang..*).*(..)) && isActive() {
		System.err.println("Error: unpermitted method call: " + thisJoinPoint.getSignature().getDeclaringType().getName() + "." + thisJoinPoint.getSignature().getName());
		return proceed();
	}
	
	Object around(ModelClass target, Object newValue) : target(target) && set(* *) && args(newValue) && isActive() {
		return InstructionImporter.fieldSet(target,thisJoinPoint.getSignature().getName(),newValue);
	}
	
	Object around(ModelClass target) : target(target) && get(* *) && isActive() {
		Signature signature=thisJoinPoint.getSignature();
		try {
			return InstructionImporter.fieldGet(target,signature.getName(),signature.getDeclaringType().getDeclaredField(signature.getName()).getType());
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * This advice hides all the synthetic methods from the result of Class.getDeclaredMethods() calls.
	 * It is needed to hide the private synthetic methods generated by AspectJ.
	 */
	Method[] around(Object c) : target(c) && call(Method[] Class.getDeclaredMethods()) && withinProject() {
		LinkedList<Method> methods = new LinkedList<>();
		for(Method m : proceed(c)) {
			if (!m.isSynthetic()) {
				methods.add(m);
			}
		}
		return methods.toArray(new Method[0]);
	}
	
}