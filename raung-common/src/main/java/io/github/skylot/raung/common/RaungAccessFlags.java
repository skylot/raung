package io.github.skylot.raung.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaungAccessFlags {
	public static final int PUBLIC = 0x1;
	public static final int PRIVATE = 0x2;
	public static final int PROTECTED = 0x4;
	public static final int STATIC = 0x8;
	public static final int FINAL = 0x10;
	public static final int SYNCHRONIZED = 0x20;
	public static final int SUPER = 0x20;
	public static final int VOLATILE = 0x40;
	public static final int BRIDGE = 0x40;
	public static final int TRANSIENT = 0x80;
	public static final int VARARGS = 0x80;
	public static final int NATIVE = 0x100;
	public static final int INTERFACE = 0x200;
	public static final int ABSTRACT = 0x400;
	public static final int STRICT = 0x800;
	public static final int SYNTHETIC = 0x1000;
	public static final int ANNOTATION = 0x2000;
	public static final int ENUM = 0x4000;
	public static final int MODULE = 0x8000;
	public static final int MANDATED = 0x8000;
	public static final int CONSTRUCTOR = 0x10000;
	public static final int DECLARED_SYNCHRONIZED = 0x20000;

	private static final List<AccFlagsInfo> FLAGS_LIST = new ArrayList<>();
	private static final Map<String, AccFlagsInfo> FLAGS_MAP = new HashMap<>();

	public enum Scope {
		CLASS(1),
		FIELD(2),
		METHOD(4),
		PARAM(8),
		ANY(15);

		private final int flag;

		Scope(int flag) {
			this.flag = flag;
		}

		public int getFlag() {
			return flag;
		}
	}

	private static final class AccFlagsInfo {
		String name;
		int accessFlag;
		int scopeFlags;

		public AccFlagsInfo(String name, int accessFlag, int scopeFlags) {
			this.name = name;
			this.accessFlag = accessFlag;
			this.scopeFlags = scopeFlags;
		}
	}

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
		add("mandated", RaungAccessFlags.MANDATED, Scope.PARAM);
	}

	private static void add(String name, int accessFlag, Scope scope) {
		add(new AccFlagsInfo(name, accessFlag, scope.getFlag()));
	}

	private static void add(String name, int accessFlag, Scope scope, Scope secondScope) {
		add(new AccFlagsInfo(name, accessFlag, scope.getFlag() | secondScope.getFlag()));
	}

	private static void add(AccFlagsInfo info) {
		FLAGS_LIST.add(info);
		FLAGS_MAP.put(info.name, info);
	}

	public static int parseToken(String token, Scope scope) {
		AccFlagsInfo info = FLAGS_MAP.get(token);
		if (info == null || (info.scopeFlags & scope.getFlag()) == 0) {
			return -1;
		}
		return info.accessFlag;
	}

	public static String format(int flags, Scope scope) {
		if (flags == 0) {
			return "";
		}
		int remFlags = flags;
		int scopeFlag = scope.getFlag();
		StringBuilder sb = new StringBuilder();
		for (AccFlagsInfo flag : FLAGS_LIST) {
			if (hasFlag(remFlags, flag.accessFlag) && hasFlag(flag.scopeFlags, scopeFlag)) {
				remFlags &= ~flag.accessFlag;
				sb.append(flag.name).append(' ');
			}
		}
		if (remFlags != 0) {
			sb.append("0x").append(Integer.toHexString(remFlags)).append(' ');
		}
		return sb.toString();
	}

	private static boolean hasFlag(int flags, int flagValue) {
		return (flags & flagValue) != 0;
	}
}
