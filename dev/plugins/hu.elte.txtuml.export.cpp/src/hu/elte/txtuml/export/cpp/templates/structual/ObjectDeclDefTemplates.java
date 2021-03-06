package hu.elte.txtuml.export.cpp.templates.structual;

import java.util.List;
import java.util.Optional;

import hu.elte.txtuml.export.cpp.CppExporterUtils;
import hu.elte.txtuml.export.cpp.templates.GenerationNames;
import hu.elte.txtuml.export.cpp.templates.GenerationNames.ModifierNames;
import hu.elte.txtuml.export.cpp.templates.GenerationNames.PointerAndMemoryNames;
import hu.elte.txtuml.export.cpp.templates.GenerationTemplates;
import hu.elte.txtuml.export.cpp.templates.PrivateFunctionalTemplates;
import hu.elte.txtuml.export.cpp.templates.activity.ActivityTemplates;

public class ObjectDeclDefTemplates {

	public static String variableDecl(String typeName, String variableName, String defaultValue, Optional<List<String>> templateParameters, GenerationTemplates.VariableType varType, boolean isStatic) {
		StringBuilder source = new StringBuilder("");
		String type = "";
		String actualTypeName = typeName + CppExporterUtils.createTemplateParametersCode(templateParameters);
		type = PrivateFunctionalTemplates.cppType(actualTypeName, varType);
	
		if(isStatic) {
			source.append(ModifierNames.StaticModifier + " ");
		}
		source.append(type);
		source.append(" ");
		source.append(variableName);
		if (defaultValue != "" && defaultValue != null) {
			source.append(ActivityTemplates.ReplaceSimpleTypeOp + defaultValue);
		}

		return ActivityTemplates.blockStatement(source.toString());
	}

	public static String variableDecl(String typeName, String variableName) {
		return variableDecl(typeName, variableName, "", Optional.empty(), GenerationTemplates.VariableType.RawPointerType, false);
	}

	public static String variableDecl(String typeName, String variableName, GenerationTemplates.VariableType varType) {
		return variableDecl(typeName, variableName, "", Optional.empty(), varType, false);
	}
	
	public static String propertyDecl(String typeName, String variableName, String defaultValue, Optional<List<String>> templateParameters, GenerationTemplates.VariableType varType) {
		return variableDecl(typeName,variableName, defaultValue,templateParameters, varType, false);
	}
	
	public static String propertyDecl(String typeName, String variableName, String defaultValue,  GenerationTemplates.VariableType varType) {
		return variableDecl(typeName, variableName, defaultValue,Optional.empty(), varType, false);
	}
	
	public static String propertyDecl(String typeName, String variableName, String defaultValue) {
		return variableDecl(typeName, variableName, defaultValue,Optional.empty(), GenerationTemplates.VariableType.RawPointerType, false);
	}

	public static String createObject(String typeName, String objName, boolean sharedObject) {
		return ObjectDeclDefTemplates.createObject(typeName, objName, null, null, sharedObject);
	}

	public static String createObject(String typeName, String objName, Optional<List<String>> params, boolean sharedObject) {
		return ObjectDeclDefTemplates.createObject(typeName, objName, null, params, sharedObject);
	}

	public static String createObject(String typeName, String objName, Optional<List<String>> templateParams,
			Optional<List<String>> params, boolean sharedObject) {
		String templateList = CppExporterUtils.createTemplateParametersCode(templateParams);
		if(!sharedObject) {
			return GenerationNames.sharedPtrType(typeName + templateList) + " " + 
					setAllocatedObjectToObjectVariable(typeName,templateParams, objName, params, false);
		} else {
			return GenerationNames.sharedPtrType(typeName + templateList) + " " + 
							setAllocatedObjectToObjectVariable(typeName,templateParams, objName, params, true);
		}
	
	
	}
	
	public static String setAllocatedObjectToObjectVariable(String typeName, 
			Optional<List<String>> templateParams, String objName, Optional<List<String>> params, Boolean sharedObject) {
		return objName  + " = " + allocateObject(typeName, templateParams, params, sharedObject) + ";\n";
	}

	public static String allocateObject(String typeName, Optional<List<String>> templateParams, Optional<List<String>> params, boolean sharedObject) {
		String templateParameters = CppExporterUtils.createTemplateParametersCode(templateParams);
		String allocatedObject = PointerAndMemoryNames.MemoryAllocator + " " + typeName + 
				templateParameters + 
				CppExporterUtils.createParametersCode(params);
		if(!sharedObject) {
			return allocatedObject;
		} else {
			return GenerationNames.sharedPtrType(typeName + templateParameters) + "(" + allocatedObject + ")";
		}
	
	}

	public static String allocateObject(String typeName, Optional<List<String>> params, boolean sharedObject) {
		return allocateObject(typeName, Optional.empty(), params, sharedObject);
	}

	public static String allocateObject(String typeName) {
		return allocateObject(typeName, Optional.empty(), Optional.empty(), false);
	}

	public static String staticPropertyDecl(String typeName, String variableName) {
		return variableDecl(typeName, variableName, "",Optional.empty(), GenerationTemplates.VariableType.StackStored, true);
	}
	
	public static String staticPropertyDef(String typeName, String ownerClassName, String propertyName, Optional<String> value) {
		String leftExpression =  typeName + " " + ownerClassName + "::" + propertyName;
		String rightPart = value.isPresent() ? " " + ActivityTemplates.ReplaceSimpleTypeOp + " " + value : "";
		return leftExpression + rightPart + ";\n";

	}

}
