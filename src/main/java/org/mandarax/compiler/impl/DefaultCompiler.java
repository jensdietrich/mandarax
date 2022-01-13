package org.mandarax.compiler.impl;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;
import org.mandarax.MandaraxException;
import org.mandarax.compiler.*;
import org.mandarax.compiler.Compiler;
import org.mandarax.dsl.*;
import org.mandarax.dsl.parser.ScriptReader;
import org.mandarax.dsl.util.AbstractTypeReasoner;
import org.mandarax.dsl.util.DefaultResolver;
import org.mandarax.dsl.util.Resolver;
import org.mandarax.dsl.util.ResolverException;
import org.mandarax.dsl.util.TypeReasoner;
import org.mandarax.dsl.util.TypeReasoningException;
import org.mvel2.templates.TemplateRuntime;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import static org.mandarax.compiler.impl.Templates.*;
import static org.mandarax.compiler.impl.CompilerUtils.*;


/**
 * Default compiler.
 * @author jens dietrich
 */
public class DefaultCompiler implements Compiler {
	
	public static org.apache.log4j.Logger LOGGER = Logger.getLogger(DefaultCompiler.class);
	
	private Verifier verifier = new VerifyAll();
	private VerificationErrorReporter verificationErrorReporter = new DefaultVerificationErrorReporter();
	private Resolver resolver = new DefaultResolver();
	static String TYPE_EXTENSION = "Rel";
	
	class RelTypeReasoner extends AbstractTypeReasoner {
		public RelTypeReasoner(Map<String, Class> objTypeMap,Map<Expression, Class> varTypeMap) {
			super();
			this.objTypeMap = objTypeMap;
			this.varTypeMap = varTypeMap;
		}
		private Map<String,Class> objTypeMap = new HashMap<String,Class>();
		private Map<Expression,Class> varTypeMap = new HashMap<Expression,Class>();
		@Override
		protected Class doGetType(Variable expression, Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException {
			Class clazz = varTypeMap.get(expression);
			if (clazz==null) clazz = objTypeMap.get(expression);
			return clazz;
		}
		@Override
		public Class getType(Expression expression, Resolver resolver,Collection<RelationshipDefinition> rels) throws TypeReasoningException {
			Class type = expression.getType();
			if (type==null) type=varTypeMap.get(expression);
			if (type==null) type=objTypeMap.get(expression);
			if (type!=null) return type; // we can associate complex terms directly with types through slots 
			else return super.getType(expression,resolver, rels);
		}
	};
	
	
	
	
	class TypeSetter implements ASTVisitor {
		private TypeReasoner typeReasoner = null;
		private Collection<RelationshipDefinition> rels = null;
		private List<TypeReasoningException> typeReasonerExceptions = new ArrayList<TypeReasoningException>();
		
		public TypeSetter(TypeReasoner typeReasoner,
				Collection<RelationshipDefinition> rels,
				List<TypeReasoningException> typeReasonerExceptions) {
			super();
			this.typeReasoner = typeReasoner;
			this.rels = rels;
			this.typeReasonerExceptions = typeReasonerExceptions;
		}
		
		private boolean setType(Expression x)  {
			Class c;
			try {
				c = typeReasoner.getType(x, resolver,rels);
				x.setType(c);
			} catch (TypeReasoningException e) {
				typeReasonerExceptions.add(e);
			}
			return true;
		}

		@Override public boolean visit(Rule x) {return true;}
		@Override public boolean visit(ExternalFacts x) {return true;}
		@Override public boolean visit(Annotation x) {return true;}
		@Override public boolean visit(FunctionDeclaration x) {return true;}
		@Override public boolean visit(ImportDeclaration x) {return true;}
		@Override public boolean visit(ObjectDeclaration x) {return true;}
		@Override public boolean visit(PackageDeclaration x) {return true;}
		@Override public boolean visit(VariableDeclaration x) {return true;}
		@Override public boolean visit(CompilationUnit x) {return true;}
		@Override public boolean visit(RelationshipDefinition x) {return true;}
		@Override public boolean visit(BinaryExpression x) {return setType(x);}
		@Override public boolean visit(Aggregation x) {return setType(x);}
		@Override public boolean visit(BooleanLiteral x) {return setType(x);}
		@Override public boolean visit(CastExpression x) {return setType(x);}
		@Override public boolean visit(ConditionalExpression x) {return setType(x);}
		@Override public boolean visit(InstanceOfExpression x) {return setType(x);}
		@Override public boolean visit(IntLiteral x) {return setType(x);}
		@Override public boolean visit(DoubleLiteral x) {return setType(x);}
		@Override public boolean visit(MemberAccess x) {return setType(x);}
		@Override public boolean visit(StringLiteral x) {return setType(x);}
		@Override public boolean visit(UnaryExpression x) {return setType(x);}
		@Override public boolean visit(Variable x) {return setType(x);}
		@Override public boolean visit(FunctionInvocation x) {return setType(x);}
		@Override public boolean visit(ConstructorInvocation x) {return setType(x);}
		@Override public boolean visit(NullValue x) {return setType(x);}
		@Override public void endVisit(CompilationUnit x) {}
		@Override public void endVisit(RelationshipDefinition x) {}
		@Override public void endVisit(Rule x) {}
		@Override public void endVisit(ExternalFacts x) {}
		@Override public void endVisit(Annotation x) {}
		@Override public void endVisit(FunctionDeclaration x) { }
		@Override public void endVisit(ImportDeclaration x) { }
		@Override public void endVisit(ObjectDeclaration x) { }
		@Override public void endVisit(PackageDeclaration x) { }
		@Override public void endVisit(VariableDeclaration x) { }
		@Override public void endVisit(BinaryExpression x) { }
		@Override public void endVisit(BooleanLiteral x) { }
		@Override public void endVisit(CastExpression x) { }
		@Override public void endVisit(ConditionalExpression x) { }
		@Override public void endVisit(InstanceOfExpression x) { }
		@Override public void endVisit(IntLiteral x) { }
		@Override public void endVisit(DoubleLiteral x) { }
		@Override public void endVisit(MemberAccess x) { }
		@Override public void endVisit(StringLiteral x) { }
		@Override public void endVisit(UnaryExpression x) { }
		@Override public void endVisit(Variable x) { }
		@Override public void endVisit(FunctionInvocation x) { }
		@Override public void endVisit(ConstructorInvocation x) { }
		@Override public void endVisit(NullValue x) { }
		@Override public void endVisit(Aggregation x) { }
	};
	

	@Override
	public void compile(Location target, CompilationMode mode, URL... urls) throws MandaraxException {
		class URLSource implements Source {
			private URL url = null;
			public URLSource(URL url) {
				super();
				this.url = url;
			}
			@Override
			public InputStream openStream() throws IOException {
				return url.openStream();
			}
			@Override
			public String getName() {
				return url==null?"unknown url":url.toString();
			}
		}
		Source[] sources = new Source[urls.length];
		for (int i=0;i<sources.length;i++) sources[i] = new URLSource(urls[i]);
		compile(target,mode,sources);
	}

	@Override
	public void compile(Location target, CompilationMode mode, File... files) throws MandaraxException {
		class FileSource implements Source {
			private File file = null;
			public FileSource(File file) {
				super();
				this.file = file;
			}
			@Override
			public InputStream openStream() throws IOException {
				return new FileInputStream(file);
			}
			@Override
			public String getName() {
				return file==null?"unknown file":file.getAbsolutePath();
			}
		}
		Source[] sources = new Source[files.length];
		for (int i=0;i<sources.length;i++) sources[i] = new FileSource(files[i]);
		compile(target,mode,sources);

	}

	@Override
	public void compile(Location target, CompilationMode mode, Source... sources) throws MandaraxException {
		// parse
		List<CompilationUnit> cus = new ArrayList<CompilationUnit>();
		ScriptReader reader = new ScriptReader();
		for (Source s:sources) {
			InputStream in;
			try {
				in = s.openStream();
				CompilationUnit cu = reader.readCompilationUnit(in);
				cu.setSource(s.getName());
				cus.add(cu);
				in.close();
			} catch (IOException x) {
				throw new CompilerException("Exception reading file from source " + s,x);
			}
		}
		
		// verify
		verifier.verify(cus,verificationErrorReporter);
		
		
		// delegate
		if (mode==CompilationMode.RELATIONSHIP_TYPES) {
			compileToInterfaces(target,cus);
		}
		else if (mode==CompilationMode.QUERIES){
			compileToClasses(target,cus);
		}
			
		else {
			throw new CompilerException("Unsupported compilation mode " + mode.name());
		}
	}
	
	private void compileToClasses(Location target, List<CompilationUnit> cus) throws MandaraxException {	
		// add additional rules for inheritance
		resolveSuperRelationships(cus);
		// associate function invocation with referenced relationships
		resolveFunctionRefs(cus);
		// rewrite NAF (precond: we must know which function invocation ref to relationships)
		normalizeNAF(cus);
		// normalise variables names - if variable snames are used that are also used in slots
		normalizeVarNames(cus);
		
		for (CompilationUnit cu:cus) {
			for (RelationshipDefinition rel:cu.getRelationshipDefinitions()) {
				try {
					LOGGER.info("Generating classes for " + rel + " defined in " + cu);
					LOGGER.info("Output to: " + target);
					createRelationshipQueryImplementation(target,cus,cu,rel);
				} catch (Exception e) {
					LOGGER.error(e);
					throw new CompilerException(cu,rel.getPosition(),"Cannot generate query interface for relationship " + rel.getName(),e);
				}
				
			}
		}
	}

	private void normalizeVarNames(List<CompilationUnit> cus) {
		for (RelationshipDefinition rel:getRels(cus,false)) {
			Collection<String> reservedNames = collectReservedNames(rel);
			for (int i=0;i<rel.getDefinitionParts().size();i++) {
				RelationshipDefinitionPart defPart = rel.getDefinitionParts().get(i);
				if (defPart instanceof Rule) {
					Rule rule = (Rule)defPart;
					Map<Expression,Expression> substitutions = new HashMap<Expression,Expression>();
					Collection<Variable> vars = new HashSet<Variable>();
					vars.addAll(rule.getHead().getVariables());
					for (Expression s:rule.getBody()) {
						vars.addAll(s.getVariables());
					}
					for (Variable var:vars) {
						if (reservedNames.contains(var.getName())) {
							Variable newVar = new Variable(var.getPosition(),var.getContext(),"_"+var.getName());
							substitutions.put(var,newVar);
						}
					}
					if (!substitutions.isEmpty()) {
						rule = rule.substitute(substitutions);
						rel.setDefinitionPart(i,rule);
						LOGGER.debug("Substitute variable names in rule (conflict with slot names): " + rule);
						
					}
				}
			}
		}
	}

	private Collection<String> collectReservedNames(RelationshipDefinition rel) {
		return Collections2.transform(rel.getSlotDeclarations(),new Function<VariableDeclaration,String>(){
			@Override
			public String apply(VariableDeclaration v) {
				return v.getName();
			}});
	}

	private void normalizeNAF(List<CompilationUnit> cus) {
		for (RelationshipDefinition rel:getRels(cus,false)) {
			for (int i=0;i<rel.getDefinitionParts().size();i++) {
				RelationshipDefinitionPart defPart = rel.getDefinitionParts().get(i);
				if (defPart instanceof Rule) {
					Rule rule = (Rule)defPart;
					Rule normalised = rule.normaliseNAF();
					if (!rule.equals(normalised)) {
						LOGGER.debug("Normalised rule containing NAF from " + rule + " to " + normalised);
						rel.setDefinitionPart(i,normalised);
					}
				}
			}
		}
	}

	// create additional rules representing relationship inheritance
	private void resolveSuperRelationships(List<CompilationUnit> cus) {
		for (RelationshipDefinition rel:getRels(cus,false)) {
			Set<String> ids = new HashSet<String>();
			for (RelationshipDefinition subRel:findSubRels(cus,rel)) {
				String id = "_"+subRel.getName()+"_extends_"+rel.getName();
				Position pos = Position.NO_POSITION; // means not defined in script
				Context context = rel.getContext();
				if (ids.add(id)) {
					FunctionInvocation head = new FunctionInvocation(pos,context,rel.getName(),createVariables(pos,context,rel.getSlotDeclarations().size()));
					FunctionInvocation body = new FunctionInvocation(pos,context,subRel.getName(),createVariables(pos,context,rel.getSlotDeclarations().size()));
					Rule rule = new Rule(pos,context,id,body,head);
					rel.addRule(rule);
					LOGGER.info("Adding inheritance rule to relationship " + rel.getName() + ": " + rule);
				}
				else {
					LOGGER.warn("Attempt to add more than one rule for sub relationship " + subRel.getName() + " of " + rel.getName());
				}
			}
		}
	}
	
	private List<Expression> createVariables(Position pos,Context context,int n) {
		List<Expression> terms = new ArrayList<Expression>();
		for (int i=0;i<n;i++) {
			terms.add(new Variable(pos,context,"_x"+i));
		}
		return terms;
	}

	private List<RelationshipDefinition> findSubRels(List<CompilationUnit> cus, RelationshipDefinition superRel) {
		List<RelationshipDefinition> subTypes = new ArrayList<RelationshipDefinition>();
		for (RelationshipDefinition rel:getRels(cus,false)) {
			if (rel.getSuperTypes().contains(superRel.getName()) && rel.getSlotDeclarations().size()==superRel.getSlotDeclarations().size()) {
				LOGGER.debug("Found sub relationships " + rel.getName() + " extending " + superRel.getName());
				subTypes.add(rel);
			}
		}	
		return subTypes;
	}

	private void compileToInterfaces(Location target, List<CompilationUnit> cus) throws MandaraxException {	
		for (CompilationUnit cu:cus) {
			for (RelationshipDefinition rel:cu.getRelationshipDefinitions()) {
				try {
					LOGGER.info("Generating relationship types for " + rel + " defined in " + cu);
					LOGGER.info("Output to: " + target);
					createRelationshipType (target,cu,rel);
				} catch (Exception e) {
					LOGGER.error(e);
					throw new CompilerException(cu,rel.getPosition(),"Cannot generate class to represent relationship " + rel.getName(),e);
				}
				
				try {
					createRelationshipQueryInterface (target,cu,rel);
				} catch (Exception e) {
					throw new CompilerException(cu,rel.getPosition(),"Cannot generate query interface for relationship " + rel.getName(),e);
				}
				
			}
		}
	}
	
	
	
	
	// keep public for unit testing
	public void createRelationshipType (Location target,CompilationUnit cu,RelationshipDefinition rel) throws Exception {
		Map<String,Object> bindings = createParamBindings(cu);
		bindings.put("rel",rel);
		String generated = (String) TemplateRuntime.execute(getTemplate(RELATIONSHIP_TYPE), bindings,Templates.registry);	
		printGeneratedCode(cu,target,rel.getName()+TYPE_EXTENSION,generated);
	}
	
	// keep public for unit testing
	public void createRelationshipQueryInterface (Location target,CompilationUnit cu,RelationshipDefinition rel) throws Exception {
		Map<String,Object> bindings = createParamBindings(cu);
		bindings.put("rel",rel);
		String packageName = cu.getContext().getPackageDeclaration().getName();
		bindings.put("packageName",packageName);
		String generated = (String) TemplateRuntime.execute(getTemplate(RELATIONSHIP_QUERY_INTERFACE), bindings,Templates.registry);
		printGeneratedCode(cu,target,rel.getName()+TYPE_EXTENSION+"Instances",generated);
	}
	
	// keep public for unit testing
	public void createRelationshipQueryImplementation(Location target,List<CompilationUnit> cus,CompilationUnit cu, RelationshipDefinition rel) throws Exception {
		String className = rel.getName()+"Instances";
		String packageName = cu.getContext().getPackageDeclaration().getName(); //+".v"+getTimestampAsVersion()
		
		for (RelationshipDefinitionPart part:rel.getDefinitionParts()) {
			if (part instanceof Rule) {
				assignTypes (cus,cu,rel,(Rule)part);
			}
			else if (part instanceof ExternalFacts) {
				assignTypes (cus,cu,rel,(ExternalFacts)part);
			}
			else {
				throw new CompilerException("Unsupported type for relationship definition part: " + part);
			}
		}
		
		// this is the list where aggregations will be collected
		// elements are references to function invocations representing references to the method generated for each aggregation
		List<FunctionInvocation> aggregations = new ArrayList<FunctionInvocation>();
		
		Map<String,Object> bindings = createParamBindings(cu);
		bindings.put("rel",rel);
		bindings.put("className",className);
		bindings.put("packageName",packageName);
		bindings.put("ruleIndices",getIndices(rel.getDefinitionParts()));
		bindings.put("resolver",resolver);
		bindings.put("aggcounter",new Counter());
		bindings.put("aggregations",aggregations);
		
		String generated = (String) TemplateRuntime.execute(getTemplate(RELATIONSHIP_QUERY_IMPLEMENTATION), bindings,Templates.registry);
			
		printGeneratedCode(cu,target,rel.getName()+TYPE_EXTENSION+"Instances",generated);
	}
	

	private String createAggregationFunction(Location target,CompilationUnit cu, RelationshipDefinition rel,FunctionInvocation agg) throws Exception {
		return "\n// TODO: code for aggregation will go here: "+agg.toString()+"\n";
	}

	private void printGeneratedCode(CompilationUnit cu,Location target,String localClassName,String code) throws Exception {
		Writer out = target.getSrcOut(cu.getContext().getPackageDeclaration().getName(),localClassName);
		out.write(code);
		out.close();
	}
	
	// bindings to be used to instantiate the templates
	private Map<String,Object> createParamBindings(CompilationUnit cu) {
		Map<String,Object> bindings = new HashMap<String,Object>();
		bindings.put("context",cu.getContext());
		bindings.put("timestamp",getTimestamp());	
		bindings.put("ext",TYPE_EXTENSION);
		bindings.put("compiler",this.getClass().getName());
		bindings.put("resolver",resolver);
		return bindings;
	}
	
	// associates expressions with type information
	// keep public for unit testing
	public void assignTypes (Collection<CompilationUnit> cus,CompilationUnit cu, RelationshipDefinition rel,Rule rule) throws CompilerException, ResolverException {
		LOGGER.debug("Assigning types to terms in " + rule);
		
		// types for all variables and declared (imported) objects will be stored here
		final Map<String,Class> objTypeMap = buildTypeMapForDeclaredObjects(cu);
		final Map<Expression,Class> varTypeMap = new HashMap<Expression,Class>();
		
		// collect relationships
		final Collection<RelationshipDefinition> rels = getRels(cus,true);
		
		// mark variables defined by object declarations as defined
		for (Variable v:rule.getVariables()) {
			v.setDefined(objTypeMap.containsKey(v.getName()));
			if (v.isDefined()) v.setType(objTypeMap.get(v.getName()));
		}
		
		collectTypeInfo(cus,objTypeMap,varTypeMap,rule.getHead());

		for (Expression expression:rule.getBody()) {
			collectTypeInfo(cus,objTypeMap,varTypeMap,expression);
		}
		
		// build type reasoner
		TypeReasoner typeReasoner = new RelTypeReasoner(objTypeMap,varTypeMap);
		List<TypeReasoningException> typeReasonerExceptions = new ArrayList<TypeReasoningException>();
		ASTVisitor visitor = new TypeSetter(typeReasoner,rels,typeReasonerExceptions);
		rule.accept(visitor);
		
		if (typeReasonerExceptions.size()>0) throw new CompilerException("Type reasoner exception",typeReasonerExceptions.get(0));
		
	}
	
	// associates expressions with type information
	// keep public for unit testing
	public void assignTypes (Collection<CompilationUnit> cus,CompilationUnit cu, RelationshipDefinition rel,ExternalFacts facts) throws CompilerException, ResolverException {
		LOGGER.debug("Assigning types to terms in " + facts);
		
		// types for all variables and declared (imported) objects will be stored here
		final Map<String,Class> objTypeMap = buildTypeMapForDeclaredObjects(cu);
		final Map<Expression,Class> varTypeMap = new HashMap<Expression,Class>();
		
		// collect relationships
		final Collection<RelationshipDefinition> rels = getRels(cus,true);
		
		// mark variables defined by object declarations as defined
		for (Variable v:facts.getIterable().getVariables()) {
			v.setDefined(objTypeMap.containsKey(v.getName()));
			if (v.isDefined()) v.setType(objTypeMap.get(v.getName()));
		}
		
		collectTypeInfo(cus,objTypeMap,varTypeMap,facts.getIterable());
		
		// build type reasoner
		TypeReasoner typeReasoner = new RelTypeReasoner(objTypeMap,varTypeMap);
		List<TypeReasoningException> typeReasonerExceptions = new ArrayList<TypeReasoningException>();
		ASTVisitor visitor = new TypeSetter(typeReasoner,rels,typeReasonerExceptions);
		facts.accept(visitor);
		
		if (typeReasonerExceptions.size()>0) throw new CompilerException("Type reasoner exception",typeReasonerExceptions.get(0));
		
	}

	
	private Map<String,Class> buildTypeMapForDeclaredObjects(CompilationUnit cu) throws CompilerException {
		final Map<String,Class> objTypeMap = new HashMap<String,Class>();
		
		// assign types for imported objects
		for (ObjectDeclaration objDecl:cu.getObjectDeclarations()) {
			String name = objDecl.getName();
			Class type = resolver.getType(cu.getContext(),objDecl.getType());
			objTypeMap.put(name,type);
			LOGGER.debug("Adding type info from object declaration to type map: " + name + " -> " + type);
		}
		
		return objTypeMap;
	}
	
	
	private void collectTypeInfo(Collection<CompilationUnit> cus,Map<String,Class> objTypeMap,Map<Expression,Class> varTypeMap,Expression expr) throws CompilerException, ResolverException {
		
		final List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(expr);
		
			
		ASTVisitor aggregationFinder = new AbstractASTVisitor() {
			@Override
			public boolean visit(Aggregation x) {
				expressions.add(x.getExpression());
				return true; // check: do we allow that aggregations can be nested ? 
			}
		};
		expr.accept(aggregationFinder);

		for (Expression expression:expressions) {
			RelationshipDefinition rel2 = null;
			for (Variable var:expression.getVariables()) {
				if (!varTypeMap.containsKey(var)) {
					int pos = -1;
					if (expression instanceof FunctionInvocation) {
						FunctionInvocation fi = (FunctionInvocation)expression;
						rel2 = this.findRelationshipDefinition(cus,fi.getFunction(),fi.getParameters().size());
						for (int i=0;i<fi.getParameters().size();i++) {
							if (fi.getParameters().get(i).equals(var)) pos=i;
						}
						
					}
					if (pos==-1) {
						// do not throw an error here - we now also support complex expressions having their types assigned by slots in rels
						// throw new CompilerException(cu,var.getPosition(),"The variable " + var + " introduced in " + rule + " must be a top level term in the relationship " + rel2.getName());
					}
					else {
						Class type = objTypeMap.get((var).getName());
						// else get type from slot definition
						
						Class type2 = resolver.getType(rel2.getContext(),rel2.getSlotDeclarations().get(pos).getType());
						if (type==null) {
							varTypeMap.put(var,type2);
							LOGGER.debug("Adding type info from prerequisite " + expression + " to type map: " + var + " -> " + type2);
						}
						else {
							checkTypeConsistency(type,type2);
							varTypeMap.put(var,type);
							LOGGER.debug("Adding type info from prerequisite " + expression + " to type map: " + var + " -> " + type);
						}
					}
				}
			}
			
			// add types to complex expressions having their types assigned by slots in rels
			if (expression instanceof FunctionInvocation) {
				FunctionInvocation fi = (FunctionInvocation)expression;
				rel2 = this.findRelationshipDefinition(cus,fi.getFunction(),fi.getParameters().size());
				for (int i=0;i<fi.getParameters().size();i++) {
					Expression part = fi.getParameters().get(i);
					if (!(part instanceof Variable)) {
						Class type = resolver.getType(rel2.getContext(),rel2.getSlotDeclarations().get(i).getType());
						varTypeMap.put(part,type);
						LOGGER.debug("Adding type info from prerequisite " + expression + " to type map: " + part + " -> " + type);
					}
				}
			}
			
		}
		
	}
	
	
	private void checkTypeConsistency(Class subtype, Class supertype) throws CompilerException {
		if (!supertype.isAssignableFrom(subtype)) throw new CompilerException("Error compiling rule: " + subtype + " is not a subtype of " + supertype);
	}

	private RelationshipDefinition findRelationshipDefinition (Collection<CompilationUnit> cus,String name,int slotCount) {
		for (RelationshipDefinition rel:getRels(cus,true)) {
			if (rel.getName().equals(name) && rel.getSlotDeclarations().size()==slotCount) {
				return rel;
			}
		}
		return null;
	}
	// cross ref functions in function invocations with function declarations or import statements
	// keep public for unit testing
	public void resolveFunctionRefs (Collection<CompilationUnit> cus) throws CompilerException {
		
		final Collection<RelationshipDefinition> declaredRels = this.getRels(cus, true);
		
		// collect function references
		// careful with duplicates here: we could get the same expressions in multiple rules
		final Collection<FunctionInvocation> referencedFunctions = new ArrayList<FunctionInvocation>(); 
		// do not count rule heads!
		class ReferencedFunctionCollector extends AbstractASTVisitor {
			private Rule context = null;
			@Override
			public boolean visit(FunctionInvocation x) {
				if (context==null || context.getHead()!=x) {
					referencedFunctions.add(x);
				}
				return super.visit(x);
			}
			@Override
			public boolean visit(Rule r) {
				context = r;
				return super.visit(r);
			}
			@Override
			public void endVisit(Rule r) {
				context = null;
				super.endVisit(r);
			}
		};
		for (CompilationUnit cu:cus) {
			cu.accept(new ReferencedFunctionCollector());
		}
		
		// cross ref
		for (FunctionInvocation ref:referencedFunctions) {
			for (RelationshipDefinition rel:declaredRels) {
				// TODO type check here? currently we only look for name and param number
				if (ref.getFunction().equals(rel.getName()) && ref.getParameters().size()==rel.getSlotDeclarations().size()) {
					LOGGER.debug("Mapping term " + ref + " defined at " + ref.getPosition() + " to relationship " + rel);
					ref.setRelationship(rel);
					break;
				}
			}
			// check whether the function has been imported
			if (ref.getRelationship()==null) {
				Method method = resolver.getFunction(ref.getContext(),ref.getFunction(),ref.getParameters().size());
				if (method!=null) {
					ref.setReferencedMethod(method);
					// TODO logging
				}
				else {
					throw new CompilerException("Cannot resolve function invocation " + ref);
				}
			}
			
		}
	}
	
	private Collection<RelationshipDefinition> getRels(Collection<CompilationUnit> cus,boolean addBuiltins) {
		List<RelationshipDefinition> rels = new ArrayList<RelationshipDefinition>();
		for (CompilationUnit cu:cus) {
			for (RelationshipDefinition rel:cu.getRelationshipDefinitions()) {
				rels.add(rel);
			}
		}
		if (addBuiltins) {
			for (RelationshipDefinition rel:BuiltIns.getBuiltInRels()) {
				rels.add(rel);
			}
		}
		return rels;
	}

}
