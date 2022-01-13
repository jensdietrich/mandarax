package org.mandarax.compiler.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;
import org.mandarax.MandaraxException;
import org.mandarax.compiler.CompilerException;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;

/**
 * Utility to manage templates.
 * @author jens dietrich
 */
public class Templates {
	public static org.apache.log4j.Logger LOGGER = Logger.getLogger(Templates.class);
	
	// constants for template names
	
	public final static String RULE_INVOCATION = "RuleInvocation";
	public final static String EXTERNAL_FACTS_INVOCATION = "ExternalFactsInvocation";
	public final static String RELATIONSHIP_TYPE = "RelationshipType";
	public final static String RELATIONSHIP_QUERY_INTERFACE = "RelationshipQueryInterface";
	public final static String RELATIONSHIP_QUERY_IMPLEMENTATION = "RelationshipQueryImplementation";
	
	public final static String MIN = "min";
	public final static String MAX = "max";
	public final static String SUM = "sum";
	public final static String COUNT = "count";
	public final static String AVG = "avg";
	
	static TemplateRegistry registry = new SimpleTemplateRegistry();
	
	static {
		try {
			getTemplate(RULE_INVOCATION);
			getTemplate(EXTERNAL_FACTS_INVOCATION);
			getTemplate(RELATIONSHIP_TYPE);
			getTemplate(RELATIONSHIP_QUERY_INTERFACE);
			getTemplate(RELATIONSHIP_QUERY_IMPLEMENTATION);
			
			getTemplate(MIN);
			getTemplate(MAX);
			getTemplate(SUM);
			getTemplate(COUNT);
			getTemplate(AVG);
		}
		catch (Exception x) {
			LOGGER.error("Error parsing template", x);
		}
	}
	
	public static CompiledTemplate getTemplate(String name) throws MandaraxException {
		synchronized (Templates.class) {
			if (registry.contains(name)) {
				return registry.getNamedTemplate(name);
			}
			else {
				CompiledTemplate template = null;
				try {
					InputStream in = Templates.class.getResourceAsStream("/org/mandarax/compiler/impl/templates/"+name+".mv");
					BufferedReader breader = new BufferedReader(new InputStreamReader(in));
					String line = null;
					StringBuffer buffer = new StringBuffer();
					while ((line=breader.readLine())!=null) {
						buffer.append(line);
						buffer.append('\n');
					}
					breader.close();
				
					template = TemplateCompiler.compileTemplate(buffer.toString());
					registry.addNamedTemplate(name,template);
					return template;
				}
				catch (Exception x) {
					throw new CompilerException("Cannot load template " + name,x);
				}
			}
		}
	}
	
}
