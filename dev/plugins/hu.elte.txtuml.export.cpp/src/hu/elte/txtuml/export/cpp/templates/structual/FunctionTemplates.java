package hu.elte.txtuml.export.cpp.templates.structual;

import java.util.List;

import hu.elte.txtuml.export.cpp.templates.GenerationNames.ModifierNames;
import hu.elte.txtuml.export.cpp.templates.GenerationTemplates;
import hu.elte.txtuml.export.cpp.CppExporterUtils.TypeDescriptor;
import hu.elte.txtuml.export.cpp.templates.GenerationNames;
import hu.elte.txtuml.export.cpp.templates.PrivateFunctionalTemplates;
import hu.elte.txtuml.export.cpp.templates.activity.ActivityTemplates;
import hu.elte.txtuml.utils.Pair;

public class FunctionTemplates {

	public static String functionDecl(String functionName) {
		return FunctionTemplates.functionDecl(functionName, null);
	}

	public static String functionDecl(String functionName, List<TypeDescriptor> params) {
		return FunctionTemplates.functionDecl(new TypeDescriptor(ModifierNames.NoReturn), functionName, params, "", false);
	}

	public static String simpleFunctionDecl(String returnType, String functionName) {
		return PrivateFunctionalTemplates.cppType(returnType,GenerationTemplates.VariableType.RawPointerType) + " " + functionName + "()";
	}

	public static String functionDecl(TypeDescriptor returnTypeName, String functionName, List<TypeDescriptor> params, String modifier,
			boolean isPureVirtual) {
		String mainDecl = PrivateFunctionalTemplates
				.cppType(returnTypeName.getTypeName(), GenerationTemplates.VariableType.getUMLMultpliedElementType(returnTypeName.getLowMul(), returnTypeName.getUpMul())) + " " + functionName + "("
				+ PrivateFunctionalTemplates.paramTypeList(params) + ")";
		if (modifier != "") {
			return modifier + " " + mainDecl + (isPureVirtual ? "= 0" : "") + ";\n";
		} else {
			return ActivityTemplates.blockStatement(mainDecl);
		}

	}

	public static String functionDecl(TypeDescriptor returnTypeName, String functionName, List<TypeDescriptor> params) {
		return functionDecl(returnTypeName, functionName, params, "", false);
	}

	public static String functionDef(String className, String functionName, String body) {
		return FunctionTemplates.functionDef(className, TypeDescriptor.NoReturn, functionName, body);
	}

	public static String functionDef(String className, TypeDescriptor returnTypeName, String functionName, String body) {
		return FunctionTemplates.functionDef(className, returnTypeName, functionName, null, body);
	}

	public static String functionDef(String className, String functionName, List<Pair<TypeDescriptor, String>> params,
			String body) {
		return FunctionTemplates.functionDef(className, TypeDescriptor.NoReturn, functionName, params, body);
	}

	public static String functionDef(String className, TypeDescriptor returnTypeName, String functionName,
			List<Pair<TypeDescriptor, String>> params, String body) {
		String mainDef = PrivateFunctionalTemplates.cppType(returnTypeName.getTypeName(), 
				GenerationTemplates.VariableType.getUMLMultpliedElementType(returnTypeName.getLowMul(), returnTypeName.getUpMul())) + " " + className + "::" + functionName
				+ "(" + PrivateFunctionalTemplates.paramList(params) + ")\n{\n";
		return mainDef + body + "}\n\n";

	}

	public static String abstractFunctionDef(String className, TypeDescriptor returnTypeName, String functionName,
			List<Pair<TypeDescriptor, String>> params, Boolean testing) {
		StringBuilder body = new StringBuilder("");
		body.append(GenerationNames.Comments.ToDoMessage);
		if(!testing) {
			body.append(GenerationNames.Macros.ErrorMacro + GenerationTemplates.generatedErrorMessage(functionName));
		}
		return functionDef(className, returnTypeName, functionName, params, body.toString());
	}

	public static String simpleFunctionDef(String returnType, String functionName, String body, String returnVariable) {
		return simpleFunctionDecl(returnType, functionName) + " {\n" + body + "\n" + "return " + returnVariable
				+ ";\n}";
	}

}
