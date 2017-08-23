package hu.elte.txtuml.export.cpp.templates.structual;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;

import hu.elte.txtuml.export.cpp.statemachine.TransitionConditions;
import hu.elte.txtuml.export.cpp.templates.GenerationNames;
import hu.elte.txtuml.export.cpp.templates.GenerationNames.ModifierNames;
import hu.elte.txtuml.export.cpp.templates.GenerationNames.PointerAndMemoryNames;
import hu.elte.txtuml.export.cpp.templates.PrivateFunctionalTemplates;
import hu.elte.txtuml.export.cpp.templates.RuntimeTemplates;
import hu.elte.txtuml.export.cpp.templates.activity.ActivityTemplates;
import hu.elte.txtuml.export.cpp.templates.statemachine.StateMachineTemplates;
import hu.elte.txtuml.utils.Pair;

public class ConstructorTemplates {

	public static String initDecl(String className, List<String> params) {
		StringBuilder source = new StringBuilder("");

		source.append(ModifierNames.NoReturn + " ");
		source.append(GenerationNames.initFunctionName(className));
		source.append("(" + PrivateFunctionalTemplates.paramTypeList(params) + ");\n");

		return source.toString();
	}

	public static String constructorDecl(String className, List<String> params) {
		StringBuilder source = new StringBuilder("");
		source.append(className);
		source.append("(" + PrivateFunctionalTemplates.paramTypeList(params) + ");\n");

		return source.toString();
	}

	public static String defaultConstructorDecl(String className) {
		return constructorDecl(className, null);
	}

	public static String destructorDecl(String className) {
		return "~" + className + "();\n";
	}

	public static String destructorDef(String className, Boolean ownStates) {
		if (!ownStates) {
			return className + "::" + "~" + className + "(){}\n";
		} else {
			return className + "::" + "~" + className + "()\n{\n" + RuntimeTemplates.GetRuntimeInstance
					+ PointerAndMemoryNames.PointerAccess + RuntimeTemplates.ObjectRemoverForRuntime + "("
					+ PointerAndMemoryNames.Self + ");\n}\n\n";
		}

	}

	public static String initDef(String className, String body, List<Pair<String, String>> params,
			Boolean stateMachine) {
		StringBuilder source = new StringBuilder("");
		source.append(ModifierNames.NoReturn + " ");
		source.append(className + "::" + GenerationNames.initFunctionName(className) + "(");
		source.append(PrivateFunctionalTemplates.paramList(params) + ")");

		source.append("\n{\n" + body + "\n");
		source.append(GenerationNames.InitiliazetFixFunctionNames.InitPorts + "();\n");
		if (stateMachine) {
			source.append(GenerationNames.InitiliazetFixFunctionNames.InitStateMachine + "();\n");
		}
		source.append("}\n");

		return source.toString();
	}

	public static String constructorDef(String className, List<String> paramNames, List<Pair<String, String>> params) {

		return className + "::" + className + "(" + PrivateFunctionalTemplates.paramList(params) + ")" + "{"
				+ GenerationNames.initFunctionName(className) + "("
				+ PrivateFunctionalTemplates.paramNameList(paramNames) + ");}\n";

	}

	/*
	 * Map<Pair<String, String>,<String,String> <event,
	 * state>,<guard,handlerName>
	 */
	public static String simpleSubStateMachineClassConstructor (String className, String parentStateMachine,
			Multimap<TransitionConditions, Pair<String, String>> machine, String initialState) {
		String constructor = subStateMachineSharedConstructor (className, parentStateMachine, machine,
						StateMachineTemplates.stateMachineInitializationSharedBody(false, null));
		return constructor + StateMachineTemplates.simpleStateMachineFixFunctionDefinitions(className,
						initialState, true);
	}

	public static String hierarchicalSubStateMachineClassConstructor(String className, String parentClassName,
			Multimap<TransitionConditions, Pair<String, String>> machine, String initialState, Map<String, String> subMachines) {
		String constructor = subStateMachineSharedConstructor(className, parentClassName, machine,
				ActivityTemplates.simpleSetValue(GenerationNames.CurrentMachineName, PointerAndMemoryNames.NullPtr) + 
				StateMachineTemplates.hierarchicalStateMachineClassConstructorSharedBody(subMachines, false, null));
		return  constructor + StateMachineTemplates.hiearchialStateMachineFixFunctionDefinitions(className, initialState, true);
	}
	
	private static String subStateMachineSharedConstructor(String className, String parentClassName, 
			Multimap<TransitionConditions, Pair<String, String>> machine, String body) {
		String parentParamName = GenerationNames.formatIncomingParamName(GenerationNames.ParentSmName);
		return className + "::" + className + "(" + PrivateFunctionalTemplates.cppType(parentClassName)
		+ " " + parentParamName + "):" + GenerationNames.ParentSmMemberName + "(" + parentParamName + ")"
		+ "\n{\n" + body
		+ "}\n\n";
	}

}
