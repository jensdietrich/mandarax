package org.mandarax.compiler.impl;

import java.util.*;
import org.apache.log4j.Logger;
import org.mandarax.compiler.CompilerException;
import org.mandarax.dsl.*;
import org.mandarax.dsl.util.Resolver;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import static org.mandarax.dsl.Position.*;
import static org.mandarax.dsl.Utils.*;

/**
 * Algorithm to organise the prerequisites in rules in order to optimise code generation. 
 * @author jens dietrich
 */
public class Scheduler {
	
	public static org.apache.log4j.Logger LOGGER = Logger.getLogger(Scheduler.class);
	public static final String ASSERTED_BY_COMPILER = "asserted_by_compiler" ;
	public static final String ASSERTED_BY_COMPILER_FOR_AGGREGATION = "asserted_by_compiler_for_aggregation" ;
	public static final String TYPE_NAME = "type_name" ; // used to mark variables as type names
	public static final String AGGREGATION_PROPERTY = "aggregation_property_name";
	public static final String AGGREGATION_REL_TYPE = "aggregation_rel_type";
	public static final String AGGREGATION_RETURN_TYPE = "aggregation_return_type";
	public static final String AGGREGATION_FUNCTION = "aggregation_function";
	public static final String AGGREGATION_INITIAL_VALUE = "aggregation_init_value";
	public static final String AGGREGATION_IS_NUMERIC_TYPE = "aggregation_is_numeric_type";
	
	private Resolver resolver = null;
	private Rule originalRule = null;
	private Rule rule = null;
	private FunctionDeclaration query = null;
	private List<Prereq> prereqs = null;
	
	
	// this are the expressions in the head of the rule that are not bound by query parameters,
	// they must be bound by the query, if not, an exception must be throws 
	// see also issue8/case1
	private Collection<Expression> mustBeBound = new HashSet<Expression>();
	
	// bound variables
	private Collection<Expression> boundVariables  = new HashSet<Expression>();
	
	// this map is used to store association between (complex) terms and (existing and newly introduced) variables
	// replacing them
	private Map<Expression,Variable> substitutions = new HashMap<Expression,Variable>();
	
	// the expression in the body currently investigated
	private Expression selected = null;
	
	// the counter generator, used to generate references to aggregation functions
	private Counter aggCounter = null;
	
	private List<FunctionInvocation> aggregationFunctions = null;
	
	private int counter=0;
	
	public Scheduler(Resolver resolver,Rule rule,FunctionDeclaration query,Counter aggCounter,List<FunctionInvocation> aggregations) throws CompilerException {
		super();
		this.resolver = resolver;
		this.rule = rule;
		this.originalRule = rule;
		this.query = query;
		this.prereqs = new ArrayList<Prereq>(rule.getBody().size());
		this.aggCounter = aggCounter;
		this.aggregationFunctions = aggregations;
		
		schedule();
	}

	/**
	 * Prepare the rule for computation.
	 * @throws CompilerException
	 */
	private void schedule() throws CompilerException {
		
		LOGGER.info("Scheduling prerequisites in " + rule + " for query " + this.query);
		
		// clone rule - we might have to add additional expressions to the body, see for instance issue8/case4 for an example
		String oldRule = rule.toString();
		
		rule = rule.clone();	
		
		initVariables();
	
		List<Expression> body = new ArrayList<Expression>();
		body.addAll(rule.getBody());
		
		// order: expressions, rel references, neg rel references 
		Collections.sort(body,new Comparator<Expression>(){
			@Override
			public int compare(Expression o1, Expression o2) {
				return getRank(o1)-getRank(o2);
			}
			private int getRank(Expression x) {
				if (isNegRel(x)) return 2;
				else if (isRel(x)) return 1;
				else return 0;
			}
		});
		
		
		Prereq last = null;
		
		// main algorithm
		while (!body.isEmpty()) {
			last = addAllResolved(body,last);
			last = addOneUnresolved(body,last);
		}
		
		
		LOGGER.debug("Finished scheduling rule " + oldRule + " for query " + query + " / relationship " + query.getRelationship());
		LOGGER.debug("Rule is now: " + this.rule);
		LOGGER.debug("Prerequisites: " + this.prereqs);
		
		// check whether all variables in head have been bound
		Collection<Expression> unbound = Collections2.filter(mustBeBound, new Predicate<Expression>() {
			@Override
			public boolean apply(Expression x) {
				return !x.isGroundWRT(boundVariables);
			}});
		
		if (!unbound.isEmpty()) {
			throw new CompilerException("Cannot compile rule " + this.originalRule + ", the following terms in the rule head cannot be bound: " + unbound);
			
		}
	}
	
	private Prereq addOneUnresolved(List<Expression> body, Prereq last) throws CompilerException {
		if (body.isEmpty()) return last;
		
		selected = null;
		// TODO: optimise - sort first
		for (Expression x:body) {
			if (x instanceof FunctionInvocation && ((FunctionInvocation)x).isDefinedByRelationship()) {
				selected = x;
				break;
			}
		}
		if (selected==null) {
			throw new CompilerException("Cannot find expression in body that can be used to bind variables");
		}
		else {
			body.remove(selected);
		}
		
		Collection<Expression> newVariables = getUnresolvedVariables(selected,body);
		
		Prereq prereq = new Prereq();
		prereq.setPrevious(last);
		prereq.setExpression(selected);
		
		prereq.setNewlyBoundVariables(newVariables);
		boundVariables.addAll(newVariables);
		Collection<Expression> bound = new LinkedHashSet<Expression>();
		bound.addAll(boundVariables);
		prereq.setBoundVariables(bound);
		prereqs.add(prereq);
		
		LOGGER.debug("Adding prerequisite " + prereq.getExpression() + ", newely bound variables: " + prereq.getNewlyBoundVariables());
		
		return prereq;
		
	}
	// get the parts not yet resolved in an expression
	private Collection<Expression> getUnresolvedVariables(Expression expression,List<Expression> body) {
		Collection<Expression> unresolved = new LinkedHashSet<Expression>();
		boolean mustApplySubstitutions = false;
		for (Expression child:expression.getChildren()) {
			if (!child.isGroundWRT(boundVariables)) {
				LOGGER.debug("Found unresolved term " + child + " in expression " + expression);
				if (!(child instanceof Variable)) {
					// unbound complex term - reuse by introduce new variable
					Variable v = this.substitutions.get(child);
					if (v==null) {
						v = createVariable(child);
						mustApplySubstitutions = true;
						unresolved.add(v);
						v.setProperty(ASSERTED_BY_COMPILER, true);
					}
				}
				else {
					unresolved.add(child);
				}
			}
		}
		
		if (mustApplySubstitutions) {
			applySubstitutions(body);
		}
		
		return unresolved;
	}

	// add the prereqs for which all variables are known
	private Prereq addAllResolved(List<Expression> body, Prereq last) throws CompilerException {
		for (Iterator<Expression> iter = body.iterator();iter.hasNext();) {
			Expression expression = iter.next();
			if (expression.isGroundWRT(boundVariables)) {
				expression = substituteAggregation(expression);
				selected=expression;
				Prereq prereq = new Prereq();
				prereq.setPrevious(last);
				last = prereq;
				prereq.setNewlyBoundVariables(new ArrayList<Expression>());
				prereq.setExpression(expression);
				prereqs.add(prereq);
				iter.remove();
				Collection<Expression> bound = new LinkedHashSet<Expression>();
				bound.addAll(boundVariables);
				prereq.setBoundVariables(bound);
				
				LOGGER.debug("Adding prerequisite " + prereq.getExpression() + ", newely bound variables: " + prereq.getNewlyBoundVariables());
			}
		}
		return last;
	}

	private void initVariables() throws CompilerException {
		
		// add references to object declaration 
		for (ObjectDeclaration objDecl:rule.getContext().getObjectDeclarations()) {
			Variable v = new Variable(objDecl.getPosition(),rule.getContext(),objDecl.getName());
			v.setType(resolver.getType(rule.getContext(),objDecl.getType()));
			boundVariables.add(v);
		}
		
		// add expressions from rule head that are query parameters
		boolean[] sign = query.getSignature();
		
		// mark all variables as bound
		for (int i=0;i<sign.length;i++) {
			Expression param = rule.getHead().getParameters().get(i);
			if (sign[i]) {
				if (param instanceof Variable) {
					boundVariables.add(param);
				}			
			}
		}
		
		// replace aggregations by function invocations - this can create new complex terms
		FunctionInvocation head = (FunctionInvocation)this.substituteAggregation(rule.getHead());
		if (head!=rule.getHead()) {
			Map<Expression,Expression> substitutionsMap = new HashMap<Expression,Expression>();
			substitutionsMap.put(rule.getHead(),head);
			rule = rule.substitute(substitutionsMap);
		}
		
		// now deal with complex terms with free variables in head: replace by newly generated variables
		for (int i=0;i<sign.length;i++) {
			Expression param = rule.getHead().getParameters().get(i);
			if (sign[i]) {
				if (!(param instanceof Variable) &&  !param.isGround()) {
					// see also issue8/case4
					Variable var = createVariable(param);
					boundVariables.add(var);
					BinaryExpression constraint = new BinaryExpression(NO_POSITION,rule.getContext(),BinOp.EQ,var,param);
					constraint.setProperty(ASSERTED_BY_COMPILER,true);
					constraint.setType(Boolean.class);
					LOGGER.debug("Substitute term in head of rule " + rule.getId() + ": " + param + " -> " + var);
					LOGGER.debug("Creating new constraint in rule " + rule.getId() + ": " + constraint);
					rule.addToBody(constraint);					
				}
				
			}
			else if (!param.isGroundWRT(this.boundVariables)) {
				mustBeBound.add(param);				
			}
		}
		
		if (!substitutions.isEmpty()) {
			applySubstitutions(rule.getBody());
			// also apply to head
			rule = new Rule(rule.getPosition(),rule.getContext(),rule.getId(),rule.getBody(),(FunctionInvocation)rule.getHead().substitute(substitutions));
		}
		
		
	}
	private Variable createVariable(Expression toBeReplaced) {
		Variable v = new Variable(NO_POSITION,toBeReplaced.getContext(),createVarName());
		v.setType(toBeReplaced.getType());
		LOGGER.debug("Substitute term in body of rule " + rule.getId() + ": " + toBeReplaced + " -> " + v);
		substitutions.put(toBeReplaced,v);
		return v;
	}
	
	private String createVarName() {
		return "__t" + (counter++);
	}
	
	private void applySubstitutions(List<Expression> body) {
//		// apply to rule
//		List<Expression> newBody = new ArrayList<Expression>(rule.getBody().size());
//		for (Expression b:rule.getBody()) {
//			if (b.getProperty(ASSERTED_BY_COMPILER)==null) {
//				newBody.add(b.substitute(substitutions));
//			}
//			else {
//				newBody.add(b.clone());
//			}
//		}
//		
//		String oldRuleTxt = rule.toString();
//		rule =  new Rule(rule.getPosition(),rule.getContext(),rule.getId(),newBody,(FunctionInvocation)rule.getHead().substitute(substitutions));
		
		// apply to body
		if (body!=null) {
			for (int i=0;i<body.size();i++) {
				Expression b = body.get(i);
				if (b.getProperty(ASSERTED_BY_COMPILER)==null) {
					body.set(i,b.substitute(substitutions));
				}
			}
		}
		if (selected!=null && selected.getProperty(ASSERTED_BY_COMPILER)==null) {
			selected=selected.substitute(substitutions);
		}
		
		rule =  new Rule(rule.getPosition(),rule.getContext(),rule.getId(),transformList(rule.getBody(),new Function<Expression,Expression>(){

			@Override
			public Expression apply(Expression x) {
				return (x.getProperty(ASSERTED_BY_COMPILER)==null)?x.substitute(substitutions):x;
			}}),(FunctionInvocation)rule.getHead().substitute(substitutions));
		
		
		LOGGER.debug("Applying substitution to rule " + rule);
	}

	/**
	 * Get the list of prerequisites.
	 * @return
	 */
	public List<Prereq> getPrerequisites() {
//		for (Prereq p:prereqs) {
//			Expression x = p.getExpression();
//			System.out.println(x + " : " + x.getTypeName());
//			for (Expression c:x.getAllChildren()) {
//				System.out.println(" " + c + " : " + c.getTypeName());
//			}
//		}
		return prereqs;
	}
	/**
	 * Get the rule optimised for code generation. 
	 * This means that the order of clauses in the body might have changed, and that there could be additional elements in the body.
	 * @return
	 */
	public Rule getRule() {
		return rule;
	}
	
	private boolean isRel(Expression x) {
		return (x instanceof FunctionInvocation) && ((FunctionInvocation)x).isDefinedByRelationship();
	}
	private boolean isNegRel(Expression x) {
		return (x instanceof FunctionInvocation && ((FunctionInvocation)x).isNaf()) ;
	}
	
	
	private Expression substituteAggregation(Expression expression) throws CompilerException {
		// find aggregations
		final Collection<Aggregation> aggregations = new HashSet<Aggregation>();
		ASTVisitor aggFinder = new AbstractASTVisitor() {
			@Override
			public boolean visit(Aggregation x) {
				aggregations.add(x);
				return super.visit(x);
			}
		};
		expression.accept(aggFinder);
		if (aggregations.isEmpty()) return expression;
		
		Map<Expression,Expression> substitutions = new HashMap<Expression,Expression>();
		for (Aggregation agg:aggregations) {
			RelationshipDefinition rel = agg.getExpression().getRelationship();
			String aggregationAttribute = null;
			boolean[] signature = new boolean[rel.getSlotDeclarations().size()];
			List<Expression> parameters = new ArrayList<Expression>();
			for (int i=0;i<signature.length;i++) {
				Expression term = agg.getExpression().getParameters().get(i);
				if (term.equals(agg.getVariable())) {
					signature[i] = false;
					aggregationAttribute = rel.getSlotDeclarations().get(i).getName();
				}
				else {
					signature[i] = term.isGroundWRT(this.boundVariables);
					if (signature[i]) {
						parameters.add(term);
					}
				}
			}
			if (rel==null) throw new CompilerException("Function in aggregation " + agg + " does not reference relationship");
			
			FunctionDeclaration query = rel.getQuery(signature);
			
			if (query==null) {
				StringBuffer msg = new StringBuffer("Cannot find suitable query for relationship " + rel + " and signature ");
				for (boolean s:signature) msg.append(s?"i":"o");
				throw new CompilerException(msg.toString());
			}
			
			String relInstanceClass = rel.getName() + DefaultCompiler.TYPE_EXTENSION + "Instances";
//			Variable classRef = new Variable(Position.NO_POSITION,expression.getContext(),relInstanceClass);
//			classRef.setProperty(TYPE_NAME, true);
			FunctionInvocation innerTerm = new FunctionInvocation(Position.NO_POSITION,expression.getContext(),relInstanceClass+"."+query.getName(),parameters);
			innerTerm.setProperty(ASSERTED_BY_COMPILER_FOR_AGGREGATION, true);
			
			// the classRef must not be bound - it is not a free variable
			//this.boundVariables.add(classRef);
			
			List<Expression> parameters2 = new ArrayList<Expression>(1);
			parameters2.add(innerTerm);
			
			// generate agg function name: 
			String aggName = agg.getFunction().name();
			String aggFunctName = "_" + aggName + "_" + this.aggCounter.getNext(aggName);
			
			
			FunctionInvocation fi = new FunctionInvocation(Position.NO_POSITION,expression.getContext(),aggFunctName,parameters2);
			fi.setProperty(ASSERTED_BY_COMPILER_FOR_AGGREGATION, true);
			fi.setProperty(AGGREGATION_PROPERTY,aggregationAttribute);
			fi.setProperty(AGGREGATION_REL_TYPE,rel.getName());
			fi.setProperty(AGGREGATION_FUNCTION, agg.getFunction().name());
			String aggTypeName = agg.getVariable().getTypeName();
			fi.setProperty(AGGREGATION_RETURN_TYPE,aggTypeName);
			fi.setProperty(AGGREGATION_INITIAL_VALUE,getDefaultValue(aggTypeName));
			fi.setProperty(AGGREGATION_IS_NUMERIC_TYPE,isNumericType(aggTypeName));
			
			// do some verification here - there are restrictions for non-numeric types
			if (agg.getFunction()!=AggregationFunction.count && !isNumericType(aggTypeName)) {
				// 1. only count, min and max are allowed for non-numeric types
				if (agg.getFunction()!=AggregationFunction.max && agg.getFunction()!=AggregationFunction.min) {
					throw new CompilerException("Cannot compile " + this.rule + " - only min and max aggregation functions are allowed for non numerical types");
				}
				// 2. if type is non-numeric, Comparable must be implemented
				Class aggType = agg.getVariable().getType();
				if (!Comparable.class.isAssignableFrom(aggType)) {
					throw new CompilerException("Cannot compile " + this.rule + " - aggregation is only supported for numeric types and Comparables, but the type of " + agg.getVariable() + " does not implement comparable: " + aggType);
				}
			}
			
			substitutions.put(agg, fi);
			
			aggregationFunctions.add(fi);
			LOGGER.debug("Code will be generated for this method representing the aggregation: " + agg + " : "+ fi);
		}

		return expression.substitute(substitutions);
	}


}
