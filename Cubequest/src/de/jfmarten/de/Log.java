package de.jfmarten.de;

public class Log {

	public static boolean DEBUG = true;

	public static void i(Object o, String s) {
		System.out.println("[" + o.getClass().getSimpleName() + "]:" + s);
	}

	public static void d(Object o, String s) {
		if (DEBUG)
			System.out.println("[" + o.getClass().getSimpleName() + "]:" + s);
	}
}
