package io.github.skylot.raung.asm.impl.parser.utils;

import java.util.HashMap;
import java.util.Map;

import io.github.skylot.raung.common.RaungAccessFlags;

public class AccFlagsParser {

	private static final Map<String, Integer> FLAGS_MAP;

	static {
		Map<String, Integer> map = new HashMap<>(20);
		map.put("public", RaungAccessFlags.PUBLIC);
		map.put("private", RaungAccessFlags.PRIVATE);
		map.put("protected", RaungAccessFlags.PROTECTED);
		map.put("static", RaungAccessFlags.STATIC);
		map.put("final", RaungAccessFlags.FINAL);
		map.put("synchronized", RaungAccessFlags.SYNCHRONIZED);
		map.put("super", RaungAccessFlags.SUPER);
		map.put("synthetic", RaungAccessFlags.SYNTHETIC);
		map.put("varargs", RaungAccessFlags.VARARGS);
		map.put("abstract", RaungAccessFlags.ABSTRACT);
		map.put("enum", RaungAccessFlags.ENUM);
		map.put("bridge", RaungAccessFlags.BRIDGE);
		map.put("transient", RaungAccessFlags.TRANSIENT);
		map.put("constructor", RaungAccessFlags.CONSTRUCTOR);
		map.put("volatile", RaungAccessFlags.VOLATILE);
		FLAGS_MAP = map;
	}

	public static int parse(String token) {
		Integer flag = FLAGS_MAP.get(token);
		return flag == null ? -1 : flag;
	}
}
