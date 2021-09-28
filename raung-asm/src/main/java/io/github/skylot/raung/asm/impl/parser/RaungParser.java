package io.github.skylot.raung.asm.impl.parser;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

import org.jetbrains.annotations.Nullable;

import io.github.skylot.raung.asm.impl.RaungAsmBuilder;
import io.github.skylot.raung.asm.impl.parser.RaungTokenizer.TokenType;
import io.github.skylot.raung.asm.impl.parser.data.ClassData;
import io.github.skylot.raung.asm.impl.parser.directives.ClassDirectives;
import io.github.skylot.raung.asm.impl.parser.utils.AccFlagsParser;
import io.github.skylot.raung.asm.impl.utils.AsmLibException;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.Directive;

public class RaungParser implements Closeable {
	private final RaungAsmBuilder args;
	private final RaungTokenizer tokenizer;
	@Nullable
	private final String fileName;

	private final Queue<String> tokensAhead = new ArrayDeque<>();

	public RaungParser(RaungAsmBuilder args, InputStream in, @Nullable String fileName) {
		this.args = args;
		this.tokenizer = new RaungTokenizer(Objects.requireNonNull(in));
		this.fileName = fileName;
	}

	public ClassData parse() {
		try {
			ClassData classData = new ClassData();
			while (true) {
				Directive directive = readDirective();
				if (directive == null) {
					break;
				}
				ClassDirectives.process(directive, this, classData);
			}
			return classData;
		} catch (AsmLibException e) {
			throw new RaungAsmException("Asm lib error. " + e.getMessage() + " at " + formatPosition(), e);
		} catch (Exception e) {
			throw new RaungAsmException("Parse error: " + e.getMessage() + " at " + formatPosition(), e);
		}
	}

	@Override
	public void close() throws IOException {
		tokenizer.close();
	}

	public RaungAsmBuilder getArgs() {
		return args;
	}

	public String formatPosition() {
		return String.format("%s:%s", fileName, tokenizer.tokenStartPosition());
	}

	private TokenType consumeNext() {
		if (!tokensAhead.isEmpty()) {
			return TokenType.TOKEN;
		}
		return tokenizer.next();
	}

	private String consumeToken() {
		if (!tokensAhead.isEmpty()) {
			return tokensAhead.poll();
		}
		return tokenizer.getToken();
	}

	private void pushTokenBack(String token) {
		tokensAhead.offer(token);
	}

	public void consumeToken(String expectedToken) {
		String token = readToken();
		if (!token.equals(expectedToken)) {
			throw new RaungAsmException(
					String.format("Expected '%s' instead '%s' at %s", expectedToken, token, formatPosition()));
		}
	}

	public String tryGetToken() {
		if (consumeNext() == TokenType.TOKEN) {
			return consumeToken();
		}
		return null;
	}

	public String peekToken() {
		String token = skipToToken();
		pushTokenBack(token);
		return token;
	}

	@Nullable
	public Directive readDirective() {
		String token = skipToToken();
		if (token == null) {
			return null;
		}
		Directive directive = Directive.parseToken(token);
		if (directive == null) {
			throw new RaungAsmException("Unknown directive", token);
		}
		return directive;
	}

	@Nullable
	public String skipToToken() {
		while (true) {
			TokenType tokenType = consumeNext();
			if (tokenType == TokenType.TOKEN) {
				return consumeToken();
			}
			if (tokenType == TokenType.FILE_END) {
				return null;
			}
		}
	}

	public String readToken() {
		TokenType tokenType = consumeNext();
		if (tokenType == TokenType.TOKEN) {
			return consumeToken();
		}
		throw new RaungAsmException("Expect token but got " + tokenType);
	}

	public String readType() {
		// TODO check type correctness
		return readToken();
	}

	public int readInt() {
		String token = readToken();
		try {
			return Integer.parseInt(token);
		} catch (NumberFormatException e) {
			throw new RaungAsmException("Expect integer number but got", token);
		}
	}

	public String readString() {
		String token = readToken();
		if (token.charAt(0) == '"') {
			token = token.substring(1);
		}
		int last = token.length() - 1;
		if (token.charAt(last) == '"') {
			token = token.substring(0, last);
		}
		return token;
	}

	public int readAccessFlags() {
		int accFlags = 0;
		while (true) {
			String token = readToken();
			int flag = AccFlagsParser.parse(token);
			if (flag == -1) {
				pushTokenBack(token);
				break;
			}
			accFlags |= flag;
		}
		return accFlags;
	}

	public void lineEnd() {
		if (consumeNext() == TokenType.TOKEN) {
			throw new RaungAsmException("Unexpected token", consumeToken());
		}
	}
}
