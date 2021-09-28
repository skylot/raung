package io.github.skylot.raung.common;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import static io.github.skylot.raung.common.DirectiveScope.SCOPE_ALL;
import static io.github.skylot.raung.common.DirectiveScope.SCOPE_CLASS;
import static io.github.skylot.raung.common.DirectiveScope.SCOPE_METHOD;

@SuppressWarnings("SpellCheckingInspection")
public enum Directive {
	VERSION(".version", SCOPE_CLASS),
	CLASS(".class", SCOPE_CLASS),
	SUPER(".super", SCOPE_CLASS),
	IMPLEMENTS(".implements", SCOPE_CLASS),
	SOURCE(".source", SCOPE_CLASS),
	SIGNATURE(".signature", SCOPE_ALL),
	INNERCLASS(".innerclass", SCOPE_CLASS),
	AUTO(".auto", SCOPE_CLASS),
	FIELD(".field", SCOPE_CLASS),
	METHOD(".method", SCOPE_CLASS),
	ENUM(".enum", SCOPE_CLASS),
	ANNOTATION(".annotation", SCOPE_ALL),
	MAX(".max", SCOPE_METHOD),
	LINE(".line", SCOPE_METHOD),
	LOCAL(".local", SCOPE_METHOD),
	STACK(".stack", SCOPE_METHOD),
	END(".end", SCOPE_ALL);

	private final String token;
	private final int scope;

	Directive(String token, int scope) {
		this.token = token;
		this.scope = scope;
	}

	public String token() {
		return this.token;
	}

	private static final Map<String, Directive> TOKENS_MAP;

	static {
		Directive[] values = values();
		Map<String, Directive> map = new HashMap<>(values.length);
		for (Directive value : values) {
			map.put(value.token, value);
		}
		TOKENS_MAP = map;
	}

	@Nullable
	public static Directive parseToken(String str) {
		return TOKENS_MAP.get(str);
	}

	public boolean isAllowedInField() {
		return DirectiveScope.hasField(this.scope);
	}
}
