
package org.mandarax.dsl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Utilities to map strings to operators and vice versa.
 * @author jens dietrich
 */

public class Utils {
	
	public final static Map<Expression,Expression> NO_SUBTITUTIONS = Collections.unmodifiableMap(new HashMap<Expression,Expression>(0));
	final static List<Expression> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<Expression>(0));
	
	private static BiMap<String,BinOp> binOps =HashBiMap.<String,BinOp>create(21);
	private static BiMap<String,UnOp> unOps =HashBiMap.<String,UnOp>create(21);
	
	static {
		binOps.put("+",BinOp.PLUS);
		binOps.put("-",BinOp.MINUS);
		binOps.put("*",BinOp.TIMES);
		binOps.put("/",BinOp.DIV);
		binOps.put("%",BinOp.MOD);
		binOps.put("==",BinOp.EQ);
		binOps.put("!=",BinOp.NEQ);
		binOps.put("|",BinOp.OR);
		binOps.put("&",BinOp.AND);
		binOps.put("<",BinOp.LT);
		binOps.put(">",BinOp.GT);
		binOps.put("<=",BinOp.LTE);
		binOps.put(">=",BinOp.GTE);
		binOps.put(">>>",BinOp.SHIFT_RRR);
		binOps.put(">>",BinOp.SHIFT_RR);
		binOps.put("<<",BinOp.SHIFT_LL);
		binOps.put("+",BinOp.PLUS);
		binOps.put("-",BinOp.MINUS);
		binOps.put("*",BinOp.TIMES);
		binOps.put("%",BinOp.MOD);
		binOps.put("/",BinOp.DIV);	
		
		unOps.put("-",UnOp.MINUS);	
		unOps.put("!",UnOp.NOT);
		unOps.put("~",UnOp.COMPL);
	}
	public static BinOp binOpForName(String t) {
		BinOp op = binOps.get(t);
		if (op==null)
			throw new IllegalArgumentException("no binary operator found for name " + t);
		return op;
	}
	public static String nameForBinOp(BinOp op) {
		String name = binOps.inverse().get(op);
		if (name==null)
			throw new IllegalArgumentException("no name found for operator " + op);
		return name;
	}
	public static UnOp unOpForName(String t) {
		UnOp op = unOps.get(t);
		if (op==null)
			throw new IllegalArgumentException("no unary operator found for name " + t);
		return op;
	}
	public static String nameForUnOp(UnOp op) {
		String name = unOps.inverse().get(op);
		if (name==null)
			throw new IllegalArgumentException("no name found for operator " + op);
		return name;
	}

	public static <F,T> List<T> transformList(List<F> fromList, Function<? super F,? extends T> function) {
		List<T> result = new ArrayList<T>(fromList.size());
		for (F e:fromList) {
			result.add(function.apply(e));
		}
		return result;
	}
	
	// utility for code generation
	public static String getDefaultValue(Class type) {
		if (type==null) return null;
		else if (type==Integer.class || type==Integer.TYPE) return new Integer(0).toString();
		else if (type==Short.class || type==Short.TYPE) return new Short((short) 0).toString();
		else if (type==Long.class || type==Long.TYPE) return new Long(0).toString();
		else if (type==Byte.class || type==Byte.TYPE) return new Byte((byte) 0).toString();
		else if (type==Character.class || type==Character.TYPE) return new Character((char) 0).toString();
		else if (type==Double.class || type==Double.TYPE) return new Double(0).toString();
		else if (type==Float.class || type==Float.TYPE) return new Float(0).toString();
		else if (type==Boolean.class || type==Boolean.TYPE) return Boolean.FALSE.toString();
		return "null";
	}
	
	// utility for code generation
	public static String getDefaultValue(String n) {
		if (Integer.class.getName().equals(n) || Integer.TYPE.getName().equals(n)) return new Integer(0).toString();
		else if (Short.class.getName().equals(n) || Short.TYPE.getName().equals(n)) return new Short((short) 0).toString();
		else if (Long.class.getName().equals(n) || Long.TYPE.getName().equals(n)) return new Long(0).toString();
		else if (Byte.class.getName().equals(n) || Byte.TYPE.getName().equals(n)) return new Byte((byte) 0).toString();
		else if (Character.class.getName().equals(n) || Character.TYPE.getName().equals(n)) return new Character((char) 0).toString();
		else if (Double.class.getName().equals(n) || Double.TYPE.getName().equals(n)) return new Double(0).toString();
		else if (Float.class.getName().equals(n) || Float.TYPE.getName().equals(n)) return new Float(0).toString();
		else if (Boolean.class.getName().equals(n) || Boolean.TYPE.getName().equals(n)) return Boolean.FALSE.toString();
		return "null";
	}
	
	// utility for code generation
	public static boolean isNumericType(String n) {
		return	 	Integer.class.getName().equals(n) || Integer.TYPE.getName().equals(n)
				||	Short.class.getName().equals(n) || Short.TYPE.getName().equals(n)
				||	Long.class.getName().equals(n) || Long.TYPE.getName().equals(n)
				||	Double.class.getName().equals(n) || Double.TYPE.getName().equals(n)
				||	Float.class.getName().equals(n) || Float.TYPE.getName().equals(n);
	}
	
	
}
