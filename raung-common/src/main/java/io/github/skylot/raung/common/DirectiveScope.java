package io.github.skylot.raung.common;

public class DirectiveScope {

	public static final int SCOPE_CLASS = 1;
	public static final int SCOPE_FIELD = 2;
	public static final int SCOPE_METHOD = 4;

	public static final int ALL = SCOPE_CLASS | SCOPE_FIELD | SCOPE_METHOD;

	public static boolean hasRoot(int scope) {
		return (scope & SCOPE_CLASS) != 0;
	}

	public static boolean hasField(int scope) {
		return (scope & SCOPE_FIELD) != 0;
	}

	public static boolean hasMethod(int scope) {
		return (scope & SCOPE_METHOD) != 0;
	}
}
