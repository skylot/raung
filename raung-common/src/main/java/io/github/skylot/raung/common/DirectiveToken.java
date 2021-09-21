package io.github.skylot.raung.common;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

public enum DirectiveToken {
	VERSION(".version"),
	CLASS(".class"),
	SUPER(".super"),
	INTERFACE(".interface"),
	SOURCE(".source"),
	SIGNATURE(".signature"),
	FIELD(".field"),
	METHOD(".method"),
	ENUM(".enum"),
	ANNOTATION(".annotation"),
	END(".end");

	private final String token;

	DirectiveToken(String token) {
		this.token = token;
	}

	public String token() {
		return this.token;
	}

	private static final Map<String, DirectiveToken> TOKENS_MAP;

	static {
		DirectiveToken[] values = values();
		Map<String, DirectiveToken> map = new HashMap<>(values.length);
		for (DirectiveToken value : values) {
			map.put(value.token, value);
		}
		TOKENS_MAP = map;
	}

	@Nullable
	public static DirectiveToken parseToken(String str) {
		return TOKENS_MAP.get(str);
	}

	public static boolean isFieldSubDirective(DirectiveToken directive) {
		if (directive == null) {
			return false;
		}
		switch (directive) {
			case ANNOTATION:
			case SIGNATURE:
				return true;
			default:
				return false;
		}
	}
}
