package io.github.skylot.raung.asm.impl.parser.utils;

import java.util.HashMap;
import java.util.Map;

import io.github.skylot.raung.common.RaungAccessFlags;
import io.github.skylot.raung.common.RaungAccessFlags.Scope;

public class AccFlagsParser {

	private static final Map<String, AccFlagsInfo> FLAGS_MAP = new HashMap<>(20);

	static {
		add("public", RaungAccessFlags.PUBLIC, Scope.ANY);
		add("private", RaungAccessFlags.PRIVATE, Scope.ANY);
		add("protected", RaungAccessFlags.PROTECTED, Scope.ANY);
		add("static", RaungAccessFlags.STATIC, Scope.ANY);
		add("final", RaungAccessFlags.FINAL, Scope.ANY);
		add("synthetic", RaungAccessFlags.SYNTHETIC, Scope.ANY);
		add("super", RaungAccessFlags.SUPER, Scope.CLASS);

		add("interface", RaungAccessFlags.INTERFACE, Scope.CLASS);
		add("annotation", RaungAccessFlags.ANNOTATION, Scope.CLASS);
		add("module", RaungAccessFlags.MODULE, Scope.CLASS);

		add("enum", RaungAccessFlags.ENUM, Scope.CLASS, Scope.FIELD);

		add("abstract", RaungAccessFlags.ABSTRACT, Scope.CLASS, Scope.METHOD);

		add("constructor", RaungAccessFlags.CONSTRUCTOR, Scope.METHOD);
		add("synchronized", RaungAccessFlags.SYNCHRONIZED, Scope.METHOD);
		add("varargs", RaungAccessFlags.VARARGS, Scope.METHOD);
		add("bridge", RaungAccessFlags.BRIDGE, Scope.METHOD);
		add("native", RaungAccessFlags.NATIVE, Scope.METHOD);
		add("strict", RaungAccessFlags.STRICT, Scope.METHOD);

		add("transient", RaungAccessFlags.TRANSIENT, Scope.FIELD);
		add("volatile", RaungAccessFlags.VOLATILE, Scope.FIELD);
	}

	private static final class AccFlagsInfo {
		int accessFlag;
		int scopeFlags;

		public AccFlagsInfo(int accessFlag, int scopeFlags) {
			this.accessFlag = accessFlag;
			this.scopeFlags = scopeFlags;
		}
	}

	private static void add(String name, int accessFlag, Scope scope, Scope secondScope) {
		add(name, new AccFlagsInfo(accessFlag, scope.getFlag() | secondScope.getFlag()));
	}

	private static void add(String name, int accessFlag, Scope scope) {
		add(name, new AccFlagsInfo(accessFlag, scope.getFlag()));
	}

	private static void add(String name, AccFlagsInfo info) {
		FLAGS_MAP.put(name, info);
	}

	public static int parse(String token, Scope scope) {
		AccFlagsInfo info = FLAGS_MAP.get(token);
		if (info == null || (info.scopeFlags & scope.getFlag()) == 0) {
			return -1;
		}
		return info.accessFlag;
	}
}
