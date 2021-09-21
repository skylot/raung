package io.github.skylot.raung.asm.impl.parser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.skylot.raung.asm.impl.utils.RaungAsmException;

public class RaungTokenizer implements Closeable {

	private final Reader reader;
	private StringBuilder tokenBuffer = new StringBuilder();

	private State savedState = State.NONE;
	private int line = 1;
	private int column;

	public RaungTokenizer(InputStream in) {
		this.reader = new BufferedReader(new InputStreamReader(in));
	}

	public String getToken() {
		return tokenBuffer.toString();
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	public enum TokenType {
		LINE_END,
		FILE_END,
		TOKEN
	}

	public enum State {
		NONE,
		STRING,
		STRING_ESCAPE,
		COMMENT,
		TOKEN,
		AT_LINE_END,
		AT_FILE_END,
		AT_ASSIGN,
	}

	public TokenType next() {
		StringBuilder buf = this.tokenBuffer;
		buf.setLength(0);
		TokenType returnType = processSavedState();
		if (returnType != null) {
			return returnType;
		}
		State state = savedState;
		try {
			while (true) {
				int cp = consumeChar();
				switch (cp) {
					case -1:
						if (state == State.TOKEN) {
							savedState = State.AT_FILE_END;
							return TokenType.TOKEN;
						}
						return TokenType.FILE_END;

					case '\n':
					case '\r':
						return processEndLine(state, cp);

					case '#':
						state = processComment(buf, state);
						if (state == null) {
							return TokenType.TOKEN;
						}
						break;

					case '\"':
						if (state == State.STRING_ESCAPE) {
							state = State.STRING;
						}
						buf.append('\"');
						break;

					case '\\':
						if (state == State.STRING) {
							state = State.STRING_ESCAPE;
						}
						break;

					default:
						state = appendChar(buf, state, cp);
						if (state == null) {
							return TokenType.TOKEN;
						}
						break;
				}
			}
		} catch (IOException e) {
			throw new RaungAsmException("Read error at " + tokenStartPosition(), e);
		}
	}

	@Nullable
	private TokenType processSavedState() {
		switch (savedState) {
			case AT_FILE_END:
				savedState = State.NONE;
				return TokenType.FILE_END;

			case AT_LINE_END:
				savedState = State.NONE;
				newLine();
				return TokenType.LINE_END;

			case AT_ASSIGN:
				savedState = State.NONE;
				tokenBuffer.append('=');
				return TokenType.TOKEN;
		}
		return null;
	}

	@NotNull
	private TokenType processEndLine(State state, int cp) throws IOException {
		consumeCharIf(cp == '\n' ? '\r' : '\n');
		switch (state) {
			case COMMENT:
				savedState = State.AT_LINE_END;
				return TokenType.LINE_END;

			case TOKEN:
				savedState = State.AT_LINE_END;
				return TokenType.TOKEN;

			default:
				newLine();
				return TokenType.LINE_END;
		}
	}

	@Nullable
	private State processComment(StringBuilder buf, State state) {
		if (state == State.TOKEN) {
			savedState = State.COMMENT;
			return null;
		}
		if (state == State.STRING) {
			buf.append('#');
			return state;
		}
		return State.COMMENT;
	}

	@Nullable
	private State appendChar(StringBuilder buf, State state, int cp) {
		if (state == State.COMMENT) {
			// skip to line end
			return state;
		}
		boolean space;
		if (cp < 0xFF) {
			char ch = (char) cp;
			if (ch == '=') {
				if (state == State.TOKEN) {
					savedState = State.AT_ASSIGN;
					return null;
				} else {
					buf.append(ch);
					return null;
				}
			}
			space = ch <= 32;
			if (!space) {
				buf.append(ch);
			}
		} else {
			space = Character.isWhitespace(cp);
			if (!space) {
				buf.append(new String(Character.toChars(cp)));
			}
		}

		if (state == State.TOKEN) {
			if (space) {
				return null;
			}
		} else {
			if (!space) {
				state = State.TOKEN;
			}
		}
		return state;
	}

	private void newLine() {
		line++;
		column = 0;
	}

	private int consumeChar() throws IOException {
		column++;
		return this.reader.read();
	}

	private void consumeCharIf(char ch) throws IOException {
		Reader r = this.reader;
		r.mark(1);
		int cp = r.read();
		if (cp != ch) {
			r.reset();
		}
	}

	public String tokenStartPosition() {
		return String.format("%d:%d", line, column - tokenBuffer.length());
	}
}
