package hu.elte.txtuml.xtxtuml.tests.compiler;

import com.google.inject.Inject
import hu.elte.txtuml.xtxtuml.XtxtUMLInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.xbase.compiler.CompilationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(XtxtUMLInjectorProvider)
class XtxtUMLStructureCompilerTest {

	@Inject extension CompilationTestHelper;

	@Test
	def compileModelDeclaration() {
		'''
			model-package test.model;
		'''.assertCompilesTo('''
			@Model
			package test.model;

			import hu.elte.txtuml.api.model.Model;
		''')

		'''
			model-package test.model as "TestModel";
		'''.assertCompilesTo('''
			@Model("TestModel")
			package test.model;

			import hu.elte.txtuml.api.model.Model;
		''')
	}

	@Test
	def compileExecution() {
		'''
			package test.exec;
			execution EmptyExecution;
		'''.assertCompilesTo('''
			package test.exec;

			import hu.elte.txtuml.api.model.execution.CheckLevel;
			import hu.elte.txtuml.api.model.execution.Execution;
			import hu.elte.txtuml.api.model.execution.LogLevel;

			@SuppressWarnings("all")
			public class EmptyExecution implements Execution {
			  private CheckLevel checkLevel;
			  
			  private LogLevel logLevel;
			  
			  private double timeMultiplier;
			  
			  private String name;
			  
			  @Override
			  public void initialization() {
			    
			  }
			  
			  public static void main(final String... args) {
			    new EmptyExecution().run();
			  }
			}
		''')

		'''
			package test.exec;
			execution ExecutionWithMembers {
				int bTest = 5;
				configure {
					name = "ExecName";
				}
				initialization {}
				before {}
				during {}
				after {}
			}
		'''.assertCompilesTo('''			
			package test.exec;

			import hu.elte.txtuml.api.model.execution.CheckLevel;
			import hu.elte.txtuml.api.model.execution.Execution;
			import hu.elte.txtuml.api.model.execution.LogLevel;

			@SuppressWarnings("all")
			public class ExecutionWithMembers implements Execution {
			  private CheckLevel checkLevel;
			  
			  private LogLevel logLevel;
			  
			  private double timeMultiplier;
			  
			  private String name;
			  
			  private int bTest = 5;
			  
			  @Override
			  public String name() {
			    name = "ExecName";
			    return name;
			  }
			  
			  private void doConfigure() {
			    this.name = "ExecName";
			  }
			  
			  @Override
			  public void configure(final Execution.Settings s) {
			    doConfigure();
			    if (logLevel != null)
			      s.logLevel = logLevel;
			    if (checkLevel != null)
			      s.checkLevel = checkLevel;
			    if (timeMultiplier != 0.0)
			      s.timeMultiplier = timeMultiplier;
			  }
			  
			  @Override
			  public void initialization() {
			  }
			  
			  @Override
			  public void before() {
			  }
			  
			  @Override
			  public void during() {
			  }
			  
			  @Override
			  public void after() {
			  }
			  
			  public static void main(final String... args) {
			    new ExecutionWithMembers().run();
			  }
			}
		''')

		'''
			package test.exec;
			import hu.elte.txtuml.api.model.^execution.CheckLevel;
			import hu.elte.txtuml.api.model.^execution.LogLevel;
			class TestClass;
			signal Sig1;
			execution ExecutionWithMembers {
				TestClass tClass;
				configure {
					name = "ExecName";
					logLevel = LogLevel.TRACE;
					timeMultiplier = 1.0;
					checkLevel = CheckLevel.OPTIONAL;
				}
				initialization {
					tClass = new TestClass();
					start tClass;
					log "InitLog";
				}
				before {
					send new Sig1() to tClass;
				}
				during {
					log "DuringLog";
				}
				after {
					log "AfterLog";
				}
			}
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/exec/ExecutionWithMembers.java

			package test.exec;

			import hu.elte.txtuml.api.model.API;
			import hu.elte.txtuml.api.model.Action;
			import hu.elte.txtuml.api.model.execution.CheckLevel;
			import hu.elte.txtuml.api.model.execution.Execution;
			import hu.elte.txtuml.api.model.execution.LogLevel;
			import test.exec.Sig1;
			import test.exec.TestClass;

			@SuppressWarnings("all")
			public class ExecutionWithMembers implements Execution {
			  private CheckLevel checkLevel;
			  
			  private LogLevel logLevel;
			  
			  private double timeMultiplier;
			  
			  private String name;
			  
			  private TestClass tClass;
			  
			  @Override
			  public String name() {
			    name = "ExecName";
			    return name;
			  }
			  
			  private void doConfigure() {
			    this.name = "ExecName";
			    this.logLevel = LogLevel.TRACE;
			    this.timeMultiplier = 1.0;
			    this.checkLevel = CheckLevel.OPTIONAL;
			  }
			  
			  @Override
			  public void configure(final Execution.Settings s) {
			    doConfigure();
			    if (logLevel != null)
			      s.logLevel = logLevel;
			    if (checkLevel != null)
			      s.checkLevel = checkLevel;
			    if (timeMultiplier != 0.0)
			      s.timeMultiplier = timeMultiplier;
			  }
			  
			  @Override
			  public void initialization() {
			    this.tClass = new TestClass();
			    Action.start(this.tClass);
			    Action.log("InitLog");
			  }
			  
			  @Override
			  public void before() {
			    API.send(new Sig1(), this.tClass);
			  }
			  
			  @Override
			  public void during() {
			    API.log("DuringLog");
			  }
			  
			  @Override
			  public void after() {
			    API.log("AfterLog");
			  }
			  
			  public static void main(final String... args) {
			    new ExecutionWithMembers().run();
			  }
			}

			File 2 : /myProject/./src-gen/test/exec/Sig1.java

			package test.exec;

			import hu.elte.txtuml.api.model.Signal;

			@SuppressWarnings("all")
			public class Sig1 extends Signal {
			}

			File 3 : /myProject/./src-gen/test/exec/TestClass.java

			package test.exec;

			import hu.elte.txtuml.api.model.ModelClass;

			@SuppressWarnings("all")
			public class TestClass extends ModelClass {
			}

		''')
	}

	@Test
	def compileSignal() {
		'''
			package test.model;
			signal S;
		'''.assertCompilesTo('''
			package test.model;

			import hu.elte.txtuml.api.model.Signal;

			@SuppressWarnings("all")
			public class S extends Signal {
			}
		''');

		'''
			package test.model;
			signal S {
				public int a1;
				public String a2;
			}
		'''.assertCompilesTo('''
			package test.model;

			import hu.elte.txtuml.api.model.Signal;

			@SuppressWarnings("all")
			public class S extends Signal {
			  public int a1;
			  
			  public String a2;
			  
			  public S(final int a1, final String a2) {
			    this.a1 = a1;
			    this.a2 = a2;
			  }
			}
		''');
		}

	@Test
	def compileSignalInheritance() {
		'''
			package test.model;
			signal S1 {
				public int i;
				public boolean b;
			}
			signal S2 extends S1 {
				public String s;
			}
			signal S3 extends S2 {
				public double d;
			}
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/S1.java

			package test.model;

			import hu.elte.txtuml.api.model.Signal;

			@SuppressWarnings("all")
			public class S1 extends Signal {
			  public int i;
			  
			  public boolean b;
			  
			  public S1(final int i, final boolean b) {
			    this.i = i;
			    this.b = b;
			  }
			}

			File 2 : /myProject/./src-gen/test/model/S2.java

			package test.model;

			import test.model.S1;

			@SuppressWarnings("all")
			public class S2 extends S1 {
			  public String s;
			  
			  public S2(final int i, final boolean b, final String s) {
			    super(i, b);
			    this.s = s;
			  }
			}

			File 3 : /myProject/./src-gen/test/model/S3.java

			package test.model;

			import test.model.S2;

			@SuppressWarnings("all")
			public class S3 extends S2 {
			  public double d;
			  
			  public S3(final int i, final boolean b, final String s, final double d) {
			    super(i, b, s);
			    this.d = d;
			  }
			}

		''');

		'''
			package test.model;
			signal S1 {
				public int i;
				public boolean b;
			}
			signal S3 extends S2 {
				public String s;
			}
			signal S2 extends S1;
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/S1.java

			package test.model;

			import hu.elte.txtuml.api.model.Signal;

			@SuppressWarnings("all")
			public class S1 extends Signal {
			  public int i;
			  
			  public boolean b;
			  
			  public S1(final int i, final boolean b) {
			    this.i = i;
			    this.b = b;
			  }
			}

			File 2 : /myProject/./src-gen/test/model/S2.java

			package test.model;

			import test.model.S1;

			@SuppressWarnings("all")
			public class S2 extends S1 {
			  public S2(final int i, final boolean b) {
			    super(i, b);
			  }
			}

			File 3 : /myProject/./src-gen/test/model/S3.java

			package test.model;

			import test.model.S2;

			@SuppressWarnings("all")
			public class S3 extends S2 {
			  public String s;
			  
			  public S3(final int i, final boolean b, final String s) {
			    super(i, b);
			    this.s = s;
			  }
			}

		''');
	}

	@Test
	def compileClass() {
		'''
			package test.model;
			class A;
		'''.assertCompilesTo('''
			package test.model;

			import hu.elte.txtuml.api.model.ModelClass;

			@SuppressWarnings("all")
			public class A extends ModelClass {
			}
		''');

		'''
			package test.model;
			class A;
			class B extends A;
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/A.java

			package test.model;

			import hu.elte.txtuml.api.model.ModelClass;

			@SuppressWarnings("all")
			public class A extends ModelClass {
			}

			File 2 : /myProject/./src-gen/test/model/B.java

			package test.model;

			import test.model.A;

			@SuppressWarnings("all")
			public class B extends A {
			}

		''');
	}
	@Test
	def compileDataType() {
		'''
			package test.model;
			datatype A;
		'''.assertCompilesTo('''
			package test.model;

			import hu.elte.txtuml.api.model.DataType;

			@SuppressWarnings("all")
			public class A extends DataType {
			}
		''');

		'''
			package test.model;
			datatype A;
			datatype B extends A;
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/A.java

			package test.model;

			import hu.elte.txtuml.api.model.DataType;

			@SuppressWarnings("all")
			public class A extends DataType {
			}

			File 2 : /myProject/./src-gen/test/model/B.java

			package test.model;

			import test.model.A;

			@SuppressWarnings("all")
			public class B extends A {
			}

		''');
	}

	@Test
	def compileEnum() {
		'''
			package test.model;
			enum E;
		'''.assertCompilesTo('''
			package test.model;

			import hu.elte.txtuml.api.model.ModelEnum;

			@SuppressWarnings("all")
			public enum E implements ModelEnum {
			}
		''');

		'''
			package test.model;
			enum E {
				A,
				B,
				C,
			}
		'''.assertCompilesTo('''
			package test.model;

			import hu.elte.txtuml.api.model.ModelEnum;

			@SuppressWarnings("all")
			public enum E implements ModelEnum {
			  A,
			  
			  B,
			  
			  C;
			}
		''');
	}

	@Test
	def compileClassAttributeAndOperation() {
		'''
			package test.model;
			class A {
				int a1;
				protected int a2;
				public static external int a3 = 0;
				private external String a4;
				public void o1() {}
				private int o2() {
					return 0;
				}
				package A o3(int p) {
					return null;
				}
				public static void o4() {}
				public external void o5() {}
				public external-body void o6() {}
				public static external void o7() {}
				public A(int p1, A p2) {}
				external A() {}
				external-body A(int p) {}
			}
		'''.assertCompilesTo('''
			package test.model;

			import hu.elte.txtuml.api.model.External;
			import hu.elte.txtuml.api.model.ExternalBody;
			import hu.elte.txtuml.api.model.ModelClass;

			@SuppressWarnings("all")
			public class A extends ModelClass {
			  int a1;
			  
			  protected int a2;
			  
			  @External
			  public static int a3 = 0;
			  
			  @External
			  private String a4;
			  
			  public void o1() {
			  }
			  
			  private int o2() {
			    return 0;
			  }
			  
			  A o3(final int p) {
			    return null;
			  }
			  
			  public static void o4() {
			  }
			  
			  @External
			  public void o5() {
			  }
			  
			  @ExternalBody
			  public void o6() {
			  }
			  
			  @External
			  public static void o7() {
			  }
			  
			  public A(final int p1, final A p2) {
			  }
			  
			  @External
			  A() {
			  }
			  
			  @ExternalBody
			  A(final int p) {
			  }
			}
		''')
	}
	
	@Test
	def compileDataTypeAttributeAndOperation() {
		'''
			package test.model;
			datatype A {
				int a1;
				protected int a2 = 2;
				public static external int a3 = 0;
				private external String a4;
				public void o1() {}
				private int o2() {
					return 0;
				}
				package A o3(int p) {
					return null;
				}
				public static void o4() {}
				public external void o5() {}
				public external-body void o6() {}
				public static external void o7() {}
				public A(int p1, A p2) {}
				external A() {}
				external-body A(int p) {}
			}
		'''.assertCompilesTo('''
			package test.model;
			
			import hu.elte.txtuml.api.model.DataType;
			import hu.elte.txtuml.api.model.External;
			import hu.elte.txtuml.api.model.ExternalBody;

			@SuppressWarnings("all")
			public class A extends DataType {
			  final int a1;
			  
			  protected final int a2 = 2;
			  
			  @External
			  public final static int a3 = 0;
			  
			  @External
			  private final String a4;
			  
			  public void o1() {
			  }
			  
			  private int o2() {
			    return 0;
			  }
			  
			  A o3(final int p) {
			    return null;
			  }
			  
			  public static void o4() {
			  }
			  
			  @External
			  public void o5() {
			  }
			  
			  @ExternalBody
			  public void o6() {
			  }
			  
			  @External
			  public static void o7() {
			  }
			  
			  public A(final int p1, final A p2) {
			  }
			  
			  @External
			  A() {
			  }
			  
			  @ExternalBody
			  A(final int p) {
			  }
			}
		''')
	}

	@Test
	def compileStatemachine() {
		'''
			package test.model;
			signal Sig;
			class A {
				port Port {}
				initial Init;
				choice Choice;
				transition T1 {
					from Init;
					to Choice;
					effect {}
				}
				transition T2 {
					from Choice;
					to Composite;
					guard ( else );
				}
				composite Composite {
					entry {}
					exit {}
					initial CInit;
					state State;
					transition T3 {
						from CInit;
						to State;
					}
					transition T4 {
						from State;
						to State;
						trigger Sig;
						port Port;
					}
				}
			}
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/A.java

			package test.model;

			import hu.elte.txtuml.api.model.From;
			import hu.elte.txtuml.api.model.Interface;
			import hu.elte.txtuml.api.model.ModelClass;
			import hu.elte.txtuml.api.model.StateMachine;
			import hu.elte.txtuml.api.model.To;
			import hu.elte.txtuml.api.model.Trigger;
			import test.model.Sig;

			@SuppressWarnings("all")
			public class A extends ModelClass {
			  public class Port extends ModelClass.Port<Interface.Empty, Interface.Empty> {
			  }
			  
			  public class Init extends StateMachine.Initial {
			  }
			  
			  public class Choice extends StateMachine.Choice {
			  }
			  
			  @From(A.Init.class)
			  @To(A.Choice.class)
			  public class T1 extends StateMachine.Transition {
			    @Override
			    public void effect() {
			    }
			  }
			  
			  @From(A.Choice.class)
			  @To(A.Composite.class)
			  public class T2 extends StateMachine.Transition {
			    @Override
			    public boolean guard() {
			      return Else();
			    }
			  }
			  
			  public class Composite extends StateMachine.CompositeState {
			    @Override
			    public void entry() {
			    }
			    
			    @Override
			    public void exit() {
			    }
			    
			    public class CInit extends StateMachine.Initial {
			    }
			    
			    public class State extends StateMachine.State {
			    }
			    
			    @From(A.Composite.CInit.class)
			    @To(A.Composite.State.class)
			    public class T3 extends StateMachine.Transition {
			    }
			    
			    @From(A.Composite.State.class)
			    @To(A.Composite.State.class)
			    @Trigger(port = A.Port.class, value = Sig.class)
			    public class T4 extends StateMachine.Transition {
			    }
			  }
			}

			File 2 : /myProject/./src-gen/test/model/Sig.java

			package test.model;

			import hu.elte.txtuml.api.model.Signal;

			@SuppressWarnings("all")
			public class Sig extends Signal {
			}

		''')
	}

	@Test
	def compilePort() {
		'''
			package test.model;
			interface I1;
			interface I2;
			class A {
				port EP {}
				behavior port BP {
					provided I1;
					required I2;
				}
			}
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/A.java

			package test.model;

			import hu.elte.txtuml.api.model.BehaviorPort;
			import hu.elte.txtuml.api.model.Interface;
			import hu.elte.txtuml.api.model.ModelClass;
			import test.model.I1;
			import test.model.I2;

			@SuppressWarnings("all")
			public class A extends ModelClass {
			  public class EP extends ModelClass.Port<Interface.Empty, Interface.Empty> {
			  }
			  
			  @BehaviorPort
			  public class BP extends ModelClass.Port<I1, I2> {
			  }
			}

			File 2 : /myProject/./src-gen/test/model/I1.java

			package test.model;

			import hu.elte.txtuml.api.model.Interface;

			@SuppressWarnings("all")
			public interface I1 extends Interface {
			}

			File 3 : /myProject/./src-gen/test/model/I2.java

			package test.model;

			import hu.elte.txtuml.api.model.Interface;

			@SuppressWarnings("all")
			public interface I2 extends Interface {
			}

		''')
	}

	@Test
	def compileAssociation() {
		'''
			package test.model;
			class A;
			association AA1 {
				A a1;
				hidden * A a2;
			}
			association AA2 {
				hidden 1..* A a1;
				0..1 A a2;
			}
			association AA3 {
				ordered unique 1..* A a1;
				unique 2..6 A a2;
			}
			association AA4 {
				ordered 6..* A a1;
				2..6 A a2;
			}
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/A.java

			package test.model;

			import hu.elte.txtuml.api.model.ModelClass;

			@SuppressWarnings("all")
			public class A extends ModelClass {
			}

			File 2 : /myProject/./src-gen/test/model/AA1.java

			package test.model;

			import hu.elte.txtuml.api.model.Any;
			import hu.elte.txtuml.api.model.Association;
			import hu.elte.txtuml.api.model.One;
			import test.model.A;

			@SuppressWarnings("all")
			public class AA1 extends Association {
			  public class a1 extends Association.End<One<A>> {
			  }
			  
			  public class a2 extends Association.HiddenEnd<Any<A>> {
			  }
			}

			File 3 : /myProject/./src-gen/test/model/AA2.java

			package test.model;

			import hu.elte.txtuml.api.model.Association;
			import hu.elte.txtuml.api.model.OneToAny;
			import hu.elte.txtuml.api.model.ZeroToOne;
			import test.model.A;

			@SuppressWarnings("all")
			public class AA2 extends Association {
			  public class a1 extends Association.HiddenEnd<OneToAny<A>> {
			  }
			  
			  public class a2 extends Association.End<ZeroToOne<A>> {
			  }
			}

			File 4 : /myProject/./src-gen/test/model/AA3.java

			package test.model;

			import hu.elte.txtuml.api.model.Association;
			import hu.elte.txtuml.api.model.OrderedUniqueOneToAny;
			import test.model.A;
			import test.model.a2CustomCollection0;

			@SuppressWarnings("all")
			public class AA3 extends Association {
			  public class a1 extends Association.End<OrderedUniqueOneToAny<A>> {
			  }
			  
			  public class a2 extends Association.End<a2CustomCollection0<A>> {
			  }
			}

			File 5 : /myProject/./src-gen/test/model/AA4.java

			package test.model;

			import hu.elte.txtuml.api.model.Association;
			import test.model.A;
			import test.model.a1CustomCollection1;
			import test.model.a2CustomCollection2;

			@SuppressWarnings("all")
			public class AA4 extends Association {
			  public class a1 extends Association.End<a1CustomCollection1<A>> {
			  }
			  
			  public class a2 extends Association.End<a2CustomCollection2<A>> {
			  }
			}

			File 6 : /myProject/./src-gen/test/model/a1CustomCollection1.java

			package test.model;

			import hu.elte.txtuml.api.model.Min;
			import hu.elte.txtuml.api.model.OrderedCollection;

			@Min(6)
			@SuppressWarnings("all")
			public class a1CustomCollection1<T> extends OrderedCollection<T, a1CustomCollection1<T>> {
			}

			File 7 : /myProject/./src-gen/test/model/a2CustomCollection0.java

			package test.model;

			import hu.elte.txtuml.api.model.Max;
			import hu.elte.txtuml.api.model.Min;
			import hu.elte.txtuml.api.model.UniqueCollection;

			@Min(2)
			@Max(6)
			@SuppressWarnings("all")
			public class a2CustomCollection0<T> extends UniqueCollection<T, a2CustomCollection0<T>> {
			}

			File 8 : /myProject/./src-gen/test/model/a2CustomCollection2.java

			package test.model;

			import hu.elte.txtuml.api.model.Collection;
			import hu.elte.txtuml.api.model.Max;
			import hu.elte.txtuml.api.model.Min;

			@Min(2)
			@Max(6)
			@SuppressWarnings("all")
			public class a2CustomCollection2<T> extends Collection<T, a2CustomCollection2<T>> {
			}

		''')
	}

	@Test
	def compileComposition() {
		'''
			package test.model;
			class A;
			composition AA {
				hidden container A a1;
				A a2;
			}
			composition AA2 {
				hidden container A a1;
				ordered unique 3..7 A a2;
			}
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/A.java

			package test.model;

			import hu.elte.txtuml.api.model.ModelClass;

			@SuppressWarnings("all")
			public class A extends ModelClass {
			}

			File 2 : /myProject/./src-gen/test/model/AA.java

			package test.model;

			import hu.elte.txtuml.api.model.Association;
			import hu.elte.txtuml.api.model.Composition;
			import hu.elte.txtuml.api.model.One;
			import test.model.A;

			@SuppressWarnings("all")
			public class AA extends Composition {
			  public class a1 extends Composition.HiddenContainerEnd<A> {
			  }
			  
			  public class a2 extends Association.End<One<A>> {
			  }
			}

			File 3 : /myProject/./src-gen/test/model/AA2.java

			package test.model;

			import hu.elte.txtuml.api.model.Association;
			import hu.elte.txtuml.api.model.Composition;
			import test.model.A;
			import test.model.a2CustomCollection0;

			@SuppressWarnings("all")
			public class AA2 extends Composition {
			  public class a1 extends Composition.HiddenContainerEnd<A> {
			  }
			  
			  public class a2 extends Association.End<a2CustomCollection0<A>> {
			  }
			}

			File 4 : /myProject/./src-gen/test/model/a2CustomCollection0.java

			package test.model;

			import hu.elte.txtuml.api.model.Max;
			import hu.elte.txtuml.api.model.Min;
			import hu.elte.txtuml.api.model.OrderedUniqueCollection;

			@Min(3)
			@Max(7)
			@SuppressWarnings("all")
			public class a2CustomCollection0<T> extends OrderedUniqueCollection<T, a2CustomCollection0<T>> {
			}

		''')
	}

	@Test
	def compileInterface() {
		'''
			package test.model;
			signal S;
			interface I1 {}
			interface I2 {
				reception S;
			}
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/I1.java

			package test.model;

			import hu.elte.txtuml.api.model.Interface;

			@SuppressWarnings("all")
			public interface I1 extends Interface {
			}

			File 2 : /myProject/./src-gen/test/model/I2.java

			package test.model;

			import hu.elte.txtuml.api.model.Interface;
			import test.model.S;

			@SuppressWarnings("all")
			public interface I2 extends Interface {
			  void reception(final S signal);
			}

			File 3 : /myProject/./src-gen/test/model/S.java

			package test.model;

			import hu.elte.txtuml.api.model.Signal;

			@SuppressWarnings("all")
			public class S extends Signal {
			}

		''')
	}

	@Test
	def compileConnector() {
		'''
			package test.model;

			interface I1 {}
			interface I2 {}
			interface I3 {}

			class A {
			    port AP1 {
			        provided I1;
			        required I2;
			    }
			    port AP2 {
			        provided I3;
			    }
			}

			class B {
			    port BP {
			        provided I2;
			        required I1;
			    }
			}

			class C {
			    port CP {
			        provided I3;
			    }
			}

			composition CA {
			    container C c;
			    A a;
			}

			composition CB {
			    container C c;
			    B b;
			}

			connector A_AB {
			    CA.a->A.AP1 aap;
			    CB.b->B.BP abp;
			}

			delegation D_CA {
			    CA.c->C.CP dcp;
			    CA.a->A.AP2 dap;
			}
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/A.java

			package test.model;

			import hu.elte.txtuml.api.model.Interface;
			import hu.elte.txtuml.api.model.ModelClass;
			import test.model.I1;
			import test.model.I2;
			import test.model.I3;

			@SuppressWarnings("all")
			public class A extends ModelClass {
			  public class AP1 extends ModelClass.Port<I1, I2> {
			  }
			  
			  public class AP2 extends ModelClass.Port<I3, Interface.Empty> {
			  }
			}

			File 2 : /myProject/./src-gen/test/model/A_AB.java

			package test.model;

			import hu.elte.txtuml.api.model.Connector;
			import hu.elte.txtuml.api.model.ConnectorBase;
			import test.model.A;
			import test.model.B;
			import test.model.CA;
			import test.model.CB;

			@SuppressWarnings("all")
			public class A_AB extends Connector {
			  public class aap extends ConnectorBase.One<CA.a, A.AP1> {
			  }
			  
			  public class abp extends ConnectorBase.One<CB.b, B.BP> {
			  }
			}

			File 3 : /myProject/./src-gen/test/model/B.java

			package test.model;

			import hu.elte.txtuml.api.model.ModelClass;
			import test.model.I1;
			import test.model.I2;

			@SuppressWarnings("all")
			public class B extends ModelClass {
			  public class BP extends ModelClass.Port<I2, I1> {
			  }
			}

			File 4 : /myProject/./src-gen/test/model/C.java

			package test.model;

			import hu.elte.txtuml.api.model.Interface;
			import hu.elte.txtuml.api.model.ModelClass;
			import test.model.I3;

			@SuppressWarnings("all")
			public class C extends ModelClass {
			  public class CP extends ModelClass.Port<I3, Interface.Empty> {
			  }
			}

			File 5 : /myProject/./src-gen/test/model/CA.java

			package test.model;

			import hu.elte.txtuml.api.model.Association;
			import hu.elte.txtuml.api.model.Composition;
			import hu.elte.txtuml.api.model.One;
			import test.model.A;
			import test.model.C;

			@SuppressWarnings("all")
			public class CA extends Composition {
			  public class c extends Composition.ContainerEnd<C> {
			  }
			  
			  public class a extends Association.End<One<A>> {
			  }
			}

			File 6 : /myProject/./src-gen/test/model/CB.java

			package test.model;

			import hu.elte.txtuml.api.model.Association;
			import hu.elte.txtuml.api.model.Composition;
			import hu.elte.txtuml.api.model.One;
			import test.model.B;
			import test.model.C;

			@SuppressWarnings("all")
			public class CB extends Composition {
			  public class c extends Composition.ContainerEnd<C> {
			  }
			  
			  public class b extends Association.End<One<B>> {
			  }
			}

			File 7 : /myProject/./src-gen/test/model/D_CA.java

			package test.model;

			import hu.elte.txtuml.api.model.ConnectorBase;
			import hu.elte.txtuml.api.model.Delegation;
			import test.model.A;
			import test.model.C;
			import test.model.CA;

			@SuppressWarnings("all")
			public class D_CA extends Delegation {
			  public class dcp extends ConnectorBase.One<CA.c, C.CP> {
			  }
			  
			  public class dap extends ConnectorBase.One<CA.a, A.AP2> {
			  }
			}

			File 8 : /myProject/./src-gen/test/model/I1.java

			package test.model;

			import hu.elte.txtuml.api.model.Interface;

			@SuppressWarnings("all")
			public interface I1 extends Interface {
			}

			File 9 : /myProject/./src-gen/test/model/I2.java

			package test.model;

			import hu.elte.txtuml.api.model.Interface;

			@SuppressWarnings("all")
			public interface I2 extends Interface {
			}

			File 10 : /myProject/./src-gen/test/model/I3.java

			package test.model;

			import hu.elte.txtuml.api.model.Interface;

			@SuppressWarnings("all")
			public interface I3 extends Interface {
			}

		''')
	}
	
	@Test
	def compileCollection() {
		'''
			package test.model;
			class A
			{
				void fun() {
			    	GCB<Integer> g;
				}
			}

			ordered collection of 1..10 A as CA;
			collection of 1..10 int as CB;
			unique collection of 1..* A as CC;
			unique ordered collection of 5 A as CD;
			ordered unique collection of * A as CE;

			collection of 1..10 as GCA;
			ordered collection of 6 as GCB;
			ordered unique collection of 1..* as GCC;
		'''.assertCompilesTo('''
			MULTIPLE FILES WERE GENERATED

			File 1 : /myProject/./src-gen/test/model/A.java

			package test.model;

			import hu.elte.txtuml.api.model.ModelClass;
			import test.model.GCB;

			@SuppressWarnings("all")
			public class A extends ModelClass {
			  void fun() {
			    GCB<Integer> g = null;
			  }
			}

			File 2 : /myProject/./src-gen/test/model/CA.java

			package test.model;

			import hu.elte.txtuml.api.model.Max;
			import hu.elte.txtuml.api.model.Min;
			import hu.elte.txtuml.api.model.OrderedCollection;
			import test.model.A;

			@Min(1)
			@Max(10)
			@SuppressWarnings("all")
			public class CA extends OrderedCollection<A, CA> {
			}

			File 3 : /myProject/./src-gen/test/model/CB.java

			package test.model;

			import hu.elte.txtuml.api.model.Collection;
			import hu.elte.txtuml.api.model.Max;
			import hu.elte.txtuml.api.model.Min;

			@Min(1)
			@Max(10)
			@SuppressWarnings("all")
			public class CB extends Collection<Integer, CB> {
			}

			File 4 : /myProject/./src-gen/test/model/CC.java

			package test.model;

			import hu.elte.txtuml.api.model.Min;
			import hu.elte.txtuml.api.model.UniqueCollection;
			import test.model.A;

			@Min(1)
			@SuppressWarnings("all")
			public class CC extends UniqueCollection<A, CC> {
			}

			File 5 : /myProject/./src-gen/test/model/CD.java

			package test.model;

			import hu.elte.txtuml.api.model.Max;
			import hu.elte.txtuml.api.model.Min;
			import hu.elte.txtuml.api.model.OrderedUniqueCollection;
			import test.model.A;

			@Min(5)
			@Max(5)
			@SuppressWarnings("all")
			public class CD extends OrderedUniqueCollection<A, CD> {
			}

			File 6 : /myProject/./src-gen/test/model/CE.java

			package test.model;

			import hu.elte.txtuml.api.model.OrderedUniqueCollection;
			import test.model.A;

			@SuppressWarnings("all")
			public class CE extends OrderedUniqueCollection<A, CE> {
			}

			File 7 : /myProject/./src-gen/test/model/GCA.java

			package test.model;

			import hu.elte.txtuml.api.model.Collection;
			import hu.elte.txtuml.api.model.Max;
			import hu.elte.txtuml.api.model.Min;

			@Min(1)
			@Max(10)
			@SuppressWarnings("all")
			public class GCA<T> extends Collection<T, GCA<T>> {
			}

			File 8 : /myProject/./src-gen/test/model/GCB.java

			package test.model;

			import hu.elte.txtuml.api.model.Max;
			import hu.elte.txtuml.api.model.Min;
			import hu.elte.txtuml.api.model.OrderedCollection;

			@Min(6)
			@Max(6)
			@SuppressWarnings("all")
			public class GCB<T> extends OrderedCollection<T, GCB<T>> {
			}

			File 9 : /myProject/./src-gen/test/model/GCC.java

			package test.model;

			import hu.elte.txtuml.api.model.Min;
			import hu.elte.txtuml.api.model.OrderedUniqueCollection;

			@Min(1)
			@SuppressWarnings("all")
			public class GCC<T> extends OrderedUniqueCollection<T, GCC<T>> {
			}

		''')
	}
	
}
