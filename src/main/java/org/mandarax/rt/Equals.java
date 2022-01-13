
package org.mandarax.rt;

/**
 * Compares objects and primitives for equality.
 * @author jens dietrich
 */
final public class Equals {
	public static boolean compare(Object o1,Object o2) {
		if (o1==null) return (o2==null);
		else return o1.equals(o2);
	}
	public static boolean compare(int v1,int v2) {
		return v1==v2;
	}
	public static boolean compare(short v1,short v2) {
		return v1==v2;
	}
	public static boolean compare(long v1,long v2) {
		return v1==v2;
	}
	public static boolean compare(byte v1,byte v2) {
		return v1==v2;
	}
	public static boolean compare(char v1,char v2) {
		return v1==v2;
	}
	public static boolean compare(double v1,double v2) {
		return v1==v2;
	}
	public static boolean compare(float v1,float v2) {
		return v1==v2;
	}
	public static boolean compare(boolean v1,boolean v2) {
		return v1==v2;
	}
}
