package hu.elte.txtuml.export.cpp.templates.statemachine;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.SignalEvent;

import hu.elte.txtuml.export.cpp.CppExporterUtils.TypeDescriptor;
import hu.elte.txtuml.export.cpp.templates.GenerationNames;
import hu.elte.txtuml.export.cpp.templates.GenerationNames.TypeDeclarationKeywords;
import hu.elte.txtuml.export.cpp.templates.GenerationTemplates;
import hu.elte.txtuml.export.cpp.templates.PrivateFunctionalTemplates;
import hu.elte.txtuml.export.cpp.templates.structual.HeaderTemplates;
import hu.elte.txtuml.export.cpp.templates.structual.ObjectDeclDefTemplates;
import hu.elte.txtuml.utils.Pair;

public class EventTemplates {
	
	public static final String ProcessEventFunctionName = "processEventVirtual";
	public static final String EventFParamName = GenerationNames.formatIncomingParamName(EventTemplates.EventParamName);
	public static final String EventParamName = "e";
	public static final String EventHeaderName = "EventStructures";
	public static final String EventBaseName = "EventBase";
	public static final String EventsEnumName = "Events";
	public static final String EventPointerType = GenerationNames.PointerAndMemoryNames.EventPtr;
	
	public static final List<String> EventParamVarList = Arrays.asList(EventFParamName);
	public static final List<TypeDescriptor> EventParamDeclList = Arrays.asList(new TypeDescriptor(EventPointerType));
	public static final List<Pair<TypeDescriptor,String>> EventParamDefList = Arrays.asList(new Pair<>(new TypeDescriptor(EventPointerType), EventParamName)); 

	public static String eventClass(String className, List<Pair<TypeDescriptor, String>> params, String constructorBody,
			List<Property> properites) {
		StringBuilder source = new StringBuilder(
				TypeDeclarationKeywords.ClassType + " " + GenerationNames.eventClassName(className) + ":public "
						+ EventTemplates.EventBaseName + "\n{\n" + GenerationNames.ModifierNames.PublicModifier + ":\n" + 
						GenerationNames.eventClassName(className) + "(");
		String paramList = PrivateFunctionalTemplates.paramList(params);
		if (paramList != "") {
			source.append(paramList);
		}
		source.append("):" + EventTemplates.EventBaseName + "(");
		source.append(GenerationNames.eventEnumName(className) + ")");
		StringBuilder body = new StringBuilder("\n{\n" + constructorBody + "}\n");

		for (Property property : properites) {
			body.append(ObjectDeclDefTemplates.variableDecl(property.getType().getName(), property.getName(), GenerationTemplates.VariableType.getUMLMultpliedElementType(property.getLower(), property.getUpper())));
		}
		source.append(body).append("};\n\n");
		body.setLength(0);
		return source.toString();
	}

	// TODO works only with signal events! (Time,Change,.. not handled) - not
	// supported..
	public static String eventEnum(Set<SignalEvent> events) {
		StringBuilder eventList = new StringBuilder("enum ");
		eventList.append(EventTemplates.EventsEnumName);
		eventList.append("{");
		if (events != null && !events.isEmpty()) {
			for (SignalEvent item : events) {
				eventList.append(GenerationNames.eventEnumName(item.getSignal().getName()) + ",");
			}
		}

		return eventList.substring(0, eventList.length() - 1) + "};\n";
	}

	public static String eventHeaderGuard(String source) {
		return HeaderTemplates.headerGuard(source, EventTemplates.EventHeaderName);
	}

	public static String eventParamName() {
		return GenerationNames.formatIncomingParamName(EventTemplates.EventParamName);
	}
	
	public static String eventPtrTypeDef(String eventName) {
		
		return GenerationTemplates.usingTemplateType(eventPtr(eventName), 
				GenerationNames.PointerAndMemoryNames.SmartPtr, 
				Arrays.asList(signalType(eventName)));
	}
	
	public static String eventPtr(String eventName) {
		return eventName + "Ptr";
	}

	public static String signalType(String type) {
		return type + GenerationNames.EventClassTypeId;
	}



}
