package hu.elte.txtuml.export.cpp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;
import org.osgi.framework.Bundle;

import hu.elte.txtuml.api.deployment.RuntimeType;
import hu.elte.txtuml.export.cpp.structural.ClassExporter;
import hu.elte.txtuml.export.cpp.structural.DataTypeExporter;
import hu.elte.txtuml.export.cpp.structural.DependencyExporter;
import hu.elte.txtuml.export.cpp.structural.EventStructuresExporter;
import hu.elte.txtuml.export.cpp.structural.InterfaceExporter;
import hu.elte.txtuml.export.cpp.templates.GenerationNames;
import hu.elte.txtuml.export.cpp.templates.GenerationNames.FileNames;
import hu.elte.txtuml.export.cpp.templates.GenerationNames.ModifierNames;
import hu.elte.txtuml.export.cpp.templates.GenerationTemplates;
import hu.elte.txtuml.export.cpp.templates.Options;
import hu.elte.txtuml.export.cpp.templates.PrivateFunctionalTemplates;
import hu.elte.txtuml.export.cpp.templates.RuntimeTemplates;
import hu.elte.txtuml.export.cpp.templates.activity.ActivityTemplates;
import hu.elte.txtuml.export.cpp.templates.statemachine.StateMachineTemplates;
import hu.elte.txtuml.export.cpp.templates.structual.FunctionTemplates;
import hu.elte.txtuml.export.cpp.templates.structual.HeaderTemplates;
import hu.elte.txtuml.export.cpp.templates.structual.LinkTemplates;
import hu.elte.txtuml.export.cpp.thread.ThreadHandlingManager;
import hu.elte.txtuml.export.cpp.thread.ThreadPoolConfiguration;
import hu.elte.txtuml.utils.Pair;

public class Uml2ToCppExporter {
	public static final String GENERATED_CPP_FOLDER_NAME = "cpp-gen";
	public static final String UML_FILES_FOLDER_NAME = "model";

	private static final String RUNTIME_DIR_PREFIX = RuntimeTemplates.RTPath;
	private static final String RUNTIME_LIB_NAME = "libsmrt";
	private static final String DEFAULT_ASSOCIATIONS_NAME = "associations";
	public static final String PROJECT_NAME = "hu.elte.txtuml.export.cpp";
	private static final String CPP_FILES_FOLDER_NAME = "cpp-runtime";

	// default sources
	private static final String DEFAULT_TARGET_EXECUTABLE = "main";
	private static final String DEPLOYMENT_NAME = "deployment";
	private static final String INIT_MACHINE_NAME = StateMachineTemplates.TransitionTableInitialSourceName;
	private static final String ENVIRONMENT_INITIALIZER = "Env";


	private final Options options;

	private ThreadHandlingManager threadManager;

	private List<Class> classes;
	private Set<String> stateMachineOwners;
	private List<String> classNames;
	private List<Element> modelRoot;
	private boolean testing;


	public Uml2ToCppExporter(List<Element> modelRoot, Pair<RuntimeType, Map<String, ThreadPoolConfiguration>> config,
			boolean addRuntimeOption, boolean overWriteMainFileOption, boolean testing) {

		this.modelRoot = modelRoot;

		this.testing = testing;
		threadManager = new ThreadHandlingManager(config);

		classNames = new LinkedList<String>();
		stateMachineOwners = new HashSet<String>();
		options = new Options(addRuntimeOption, overWriteMainFileOption);


		classes = CppExporterUtils.getAllModelClass(modelRoot);

	}

	public void buildCppCode(String outputDirectory) throws IOException {

		threadManager.createConfigurationSource(outputDirectory);

		copyPreWrittenCppFiles(outputDirectory);
		createEventSource(outputDirectory);
		createClassSources(outputDirectory);
		createTransitionTableInitialSource(outputDirectory);
		createDataTypes(outputDirectory);
		createAssociationsSources(outputDirectory);
		createInterfaces(outputDirectory);
		createCMakeFile(outputDirectory);
	}

	private void createClassSources(String outputDirectory) throws IOException {
		for (Class cls : classes) {
			
			ClassExporter classExporter = new ClassExporter(cls, cls.getName(), outputDirectory);
			classExporter.setTesting(testing);		
			classExporter.setPoolId(threadManager.getConfiguratedPoolId(cls.getName()));

			classExporter.createUnitSource();
			if (CppExporterUtils.isStateMachineOwner(cls)) {
				classNames.addAll(classExporter.getSubmachines());
			}

			classNames.add(cls.getName());
			classNames.addAll(classExporter.getAdditionalSources());

			if (CppExporterUtils.isStateMachineOwner(cls)) {
				stateMachineOwners.add(cls.getName());
				stateMachineOwners.addAll(classExporter.getSubmachines());
			}

		}
	}

	private void createTransitionTableInitialSource(String outputDirectory) throws IOException {
		CppExporterUtils
				.writeOutSource(outputDirectory,
						GenerationTemplates.headerName(StateMachineTemplates.TransitionTableInitialSourceName),
						CppExporterUtils
								.format(HeaderTemplates.headerGuard(
										GenerationTemplates.putNamespace(
												FunctionTemplates.functionDecl(
														StateMachineTemplates.AllTransitionTableInitialProcName),
												GenerationNames.Namespaces.ModelNamespace),
										StateMachineTemplates.TransitionTableInitialSourceName)));

		DependencyExporter dependencyExporter = new DependencyExporter();
		dependencyExporter.addDependencies(stateMachineOwners);
		StringBuilder initalFunctionBody = new StringBuilder("");
		for (String stateMachineOwner : stateMachineOwners) {
			initalFunctionBody.append(ActivityTemplates.blockStatement(GenerationTemplates
					.staticMethodInvoke(stateMachineOwner, StateMachineTemplates.InitTransitionTable)));
		}

		CppExporterUtils.writeOutSource(outputDirectory,
				GenerationTemplates.sourceName(StateMachineTemplates.TransitionTableInitialSourceName),
				CppExporterUtils.format(dependencyExporter
						.createDependencyCppIncludeCode(StateMachineTemplates.TransitionTableInitialSourceName)
						+ GenerationTemplates.putNamespace(
								FunctionTemplates.simpleFunctionDef(ModifierNames.NoReturn,
										StateMachineTemplates.AllTransitionTableInitialProcName,
										initalFunctionBody.toString(), ""),
								GenerationNames.Namespaces.ModelNamespace)));

	}

	private void createDataTypes(String outputDirectory) throws IOException {
		
		List<DataType> dataTypes = new ArrayList<>();
		CppExporterUtils.getTypedElements(dataTypes, UMLPackage.Literals.DATA_TYPE, modelRoot);

		for (DataType dataType : dataTypes) {
			DataTypeExporter dataTypeExporter = new DataTypeExporter(dataType, dataType.getName(), outputDirectory);
			dataTypeExporter.createUnitSource();
			
		}

	}

	private void copyPreWrittenCppFiles(String destination) throws IOException {

		String cppFilesLocation = seekCppFilesLocation();
		File file = new File(destination);
		if (!file.exists()) {
			file.mkdirs();
		}

		Files.copy(
				Paths.get(cppFilesLocation + ENVIRONMENT_INITIALIZER + "."
						+ GenerationNames.FileNames.HeaderExtension),
				Paths.get(destination + File.separator + ENVIRONMENT_INITIALIZER + "."
						+ GenerationNames.FileNames.HeaderExtension),
				StandardCopyOption.REPLACE_EXISTING);
		Files.copy(
				Paths.get(cppFilesLocation + ENVIRONMENT_INITIALIZER + "."
						+ GenerationNames.FileNames.SourceExtension),
				Paths.get(destination + File.separator + ENVIRONMENT_INITIALIZER + "."
						+ GenerationNames.FileNames.SourceExtension),
				StandardCopyOption.REPLACE_EXISTING);

		if (options.isAddRuntime()) {

			File sourceRuntimeDir = new File(cppFilesLocation);
			File outputRuntimeDir = new File(destination + File.separator + RUNTIME_DIR_PREFIX);
			copyFolder(sourceRuntimeDir, outputRuntimeDir);

		}

		if (!Paths.get(destination + File.separator + "main.cpp").toFile().exists() || options.isOverWriteMainFile()) {
			Files.copy(Paths.get(cppFilesLocation + "main.cpp"), Paths.get(destination + File.separator + "main.cpp"),
					StandardCopyOption.REPLACE_EXISTING);
		}

	}

	private String seekCppFilesLocation() throws IOException {

		Bundle bundle = Platform.getBundle(PROJECT_NAME);
		URL fileURL = bundle.getEntry(CPP_FILES_FOLDER_NAME);
		File f = new File(FileLocator.toFileURL(fileURL).getPath());

		return f.getPath() + File.separator;
	}

	private void copyFolder(File sourceRuntimeDir, File outputRuntimeDir) throws IOException {

		String files[] = sourceRuntimeDir.list();

		if (!outputRuntimeDir.exists()) {
			outputRuntimeDir.mkdirs();
		}

		for (String file : files) {
			Path source = Paths.get(sourceRuntimeDir.getAbsolutePath() + File.separator + file);
			Path target = Paths.get(outputRuntimeDir.getAbsolutePath() + File.separator + file);
			if (Files.isDirectory(source)) {
				copyFolder(source.toFile(), target.toFile());
			} else {
				Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
			}

		}

	}

	private void createCMakeFile(String outputDirectory) throws FileNotFoundException, UnsupportedEncodingException {
		CMakeSupport cmake = new CMakeSupport(outputDirectory);
		cmake.addIncludeDirectory(
				RUNTIME_DIR_PREFIX.substring(0, RUNTIME_DIR_PREFIX.indexOf(org.eclipse.core.runtime.Path.SEPARATOR)));
		List<String> librarySourceClasses = new ArrayList<String>();
		librarySourceClasses.add("runtime");
		librarySourceClasses.add("StateMachineOwner");
		librarySourceClasses.add("NotStateMachineOwner");
		librarySourceClasses.add("threadpool");
		librarySourceClasses.add("threadpoolmanager");
		librarySourceClasses.add("threadcontainer");
		librarySourceClasses.add("timer");
		librarySourceClasses.add("itimer");
		librarySourceClasses.add(FileNames.FileNameAction);
		cmake.addStaticLibraryTarget(RUNTIME_LIB_NAME, librarySourceClasses, RUNTIME_DIR_PREFIX);
		List<String> sourceNames = new ArrayList<String>();
		sourceNames.add(DEFAULT_TARGET_EXECUTABLE);
		sourceNames.add(DEPLOYMENT_NAME);
		sourceNames.add(DEFAULT_ASSOCIATIONS_NAME);
		sourceNames.add(INIT_MACHINE_NAME);
		sourceNames.add(ENVIRONMENT_INITIALIZER);
		sourceNames.addAll(classNames);
		cmake.addExecutableTarget(DEFAULT_TARGET_EXECUTABLE, sourceNames, "");
		cmake.writeOutCMakeLists();
	}

	private void createEventSource(String outputDirectory) throws FileNotFoundException, UnsupportedEncodingException {
		
		EventStructuresExporter eventSourceExporter = new EventStructuresExporter(CppExporterUtils.getSignalsWithConstructors(modelRoot), outputDirectory);
		eventSourceExporter.createUnitSource();

	}

	private void createAssociationsSources(String outputDirectory)
			throws FileNotFoundException, UnsupportedEncodingException {
		
		class TypeDecriptor {

			public TypeDecriptor(String name, Boolean isInterface) {
				this.name = name;
				this.isInterface = isInterface;
			}
			public String getName() {
				return name;
			}
			public Boolean getIsInterface() {
				return isInterface;
			}
			private String name;
			private Boolean isInterface;
			
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((isInterface == null) ? 0 : isInterface.hashCode());
				result = prime * result + ((name == null) ? 0 : name.hashCode());
				return result;
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				TypeDecriptor other = (TypeDecriptor) obj;
				if (isInterface == null) {
					if (other.isInterface != null)
						return false;
				} else if (!isInterface.equals(other.isInterface))
					return false;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				return true;
			}
			
			
		}
		
		Set<TypeDecriptor> associatedClasses = new HashSet<TypeDecriptor>();
		StringBuilder includes = new StringBuilder(
				PrivateFunctionalTemplates.include(LinkTemplates.AssociationsStructuresHreaderName));
		StringBuilder preDeclerations = new StringBuilder("");
		StringBuilder structures = new StringBuilder("");
		StringBuilder functions = new StringBuilder("");

		List<Association> associationList = new ArrayList<Association>();
		CppExporterUtils.getTypedElements(associationList, UMLPackage.Literals.ASSOCIATION, modelRoot);
		for (Association assoc : associationList) {
			Property e1End = assoc.getMemberEnds().get(0);
			Property e2End = assoc.getMemberEnds().get(1);
			String e1 = e1End.getType().getName();
			String e1Name = e1End.getName();
			String e2 = e2End.getType().getName();
			String e2Name = e2End.getName();
			associatedClasses.add(new TypeDecriptor(e1, e1End.getType().eClass().equals(UMLPackage.Literals.INTERFACE)));
			associatedClasses.add(new TypeDecriptor(e2, e2End.getType().eClass().equals(UMLPackage.Literals.INTERFACE)));
			structures.append(LinkTemplates.createAssociationStructure(assoc.getName(), e1, e2, e1Name, e2Name));
			
			// TODO temporally solution, this functions will be elinamented
			if(!e1End.getType().eClass().equals(UMLPackage.Literals.INTERFACE) && !e2End.getType().eClass().equals(UMLPackage.Literals.INTERFACE)) {
				functions.append(LinkTemplates.linkTemplateSpecializationDef(e1, e2, assoc.getName(), e2Name,
						e2End.isNavigable(), LinkTemplates.LinkFunctionType.Link));
				functions.append(LinkTemplates.linkTemplateSpecializationDef(e2, e1, assoc.getName(), e1Name,
						e1End.isNavigable(), LinkTemplates.LinkFunctionType.Link));

				functions.append(LinkTemplates.linkTemplateSpecializationDef(e2, e1, assoc.getName(), e1Name,
						e1End.isNavigable(), LinkTemplates.LinkFunctionType.Unlink));
				functions.append(LinkTemplates.linkTemplateSpecializationDef(e1, e2, assoc.getName(), e2Name,
						e2End.isNavigable(), LinkTemplates.LinkFunctionType.Unlink));
			}


		}
		StringBuilder headerIncludes = new StringBuilder("");
		headerIncludes.append(PrivateFunctionalTemplates.include(RuntimeTemplates.RTPath + LinkTemplates.AssocationHeader));
		for (TypeDecriptor classDescriptor : associatedClasses) {
			includes.append(PrivateFunctionalTemplates.include(classDescriptor.getName()));
			if(!classDescriptor.getIsInterface()) {
				preDeclerations.append(GenerationTemplates.forwardDeclaration(classDescriptor.getName()));

			} else {
				headerIncludes.append(PrivateFunctionalTemplates.include(classDescriptor.getName()));
			}
		}
		String headerSource = HeaderTemplates.headerGuard(
				headerIncludes.toString() 
				+ GenerationTemplates.putNamespace(preDeclerations.toString() 
				+ structures.toString(),
				GenerationNames.Namespaces.ModelNamespace),
				LinkTemplates.AssociationsStructuresHreaderName);

		CppExporterUtils.writeOutSource(outputDirectory, (LinkTemplates.AssociationStructuresHeader),
				CppExporterUtils.format(headerSource));

		String cppSource = includes.toString()
				+ GenerationTemplates.putNamespace(functions.toString(), GenerationNames.Namespaces.ModelNamespace);
		
		CppExporterUtils.writeOutSource(outputDirectory, (LinkTemplates.AssociationStructuresSource),
				CppExporterUtils.format(cppSource));

	}

	private void createInterfaces(String outputDirectory) throws FileNotFoundException, UnsupportedEncodingException {
		List<Interface> interfaces = new ArrayList<Interface>();

		CppExporterUtils.getTypedElements(interfaces, UMLPackage.Literals.INTERFACE, modelRoot);

		for (Interface inf : interfaces) {
			if (!inf.getOwnedReceptions().isEmpty()) {
				InterfaceExporter interfaceExporter = new InterfaceExporter(inf,inf.getName(), outputDirectory);
				interfaceExporter.createUnitSource();
			}

		}
	}


}
