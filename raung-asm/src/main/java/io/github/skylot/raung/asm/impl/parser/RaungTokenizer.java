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

import static io.github.skylot.raung.common.utils.StringCommonUtils.repeat;

public class RaungTokenizer implements Closeable {

	private final Reader reader;
	private final StringBuilder lineBuffer = new StringBuilder();
	private final StringBuilder tokenBuffer = new StringBuilder();

	private State savedState = State.NONE;
	private int line = 1;
	private int column = 1;

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
				int cp = reader.read();
				if (cp == -1) {
					if (state == State.TOKEN) {
						savedState = State.AT_FILE_END;
						return TokenType.TOKEN;
					}
					return TokenType.FILE_END;
				}
				column++;
				lineBuffer.appendCodePoint(cp);

				switch (cp) {
					case '\n':
					case '\r':
						return processEndLine(state, cp);

					case '#':
						state = processComment(buf, state);
						if (state == null) {
							return TokenType.TOKEN;
						}
						break;

					case '"':
						if (state != State.COMMENT) {
							buf.append('"');
							if (state == State.STRING) {
								savedState = State.NONE;
								return TokenType.TOKEN;
							}
							state = State.STRING;
						}
						break;

					case '\\':
						if (state == State.STRING_ESCAPE) {
							buf.append("\\\\"); // ignore this escape (will handle in value parser)
							state = State.STRING;
						} else {
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
			throw new RaungAsmException("Read error", e);
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
		if (state == State.STRING_ESCAPE) {
			buf.append('\\');
			state = State.STRING;
		}
		if (state == State.STRING) {
			if (cp < 0xFF) {
				buf.append((char) cp);
			} else {
				buf.append(new String(Character.toChars(cp)));
			}
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
		column = 1;
		lineBuffer.setLength(0);
	}

	private void consumeCharIf(char ch) throws IOException {
		Reader r = this.reader;
		r.mark(1);
		int cp = r.read();
		if (cp != ch) {
			r.reset();
		}
	}

	public String formatMsgForCurrentPosition(int offsetInToken, String msg, @Nullable String fileName) {
		int tokenLen = tokenBuffer.length();
		int lineOffset = column - tokenLen - 1 + offsetInToken;
		String lineNum = Integer.toString(line);
		String contextPadding = repeat(' ', lineNum.length() + 1) + '|';
		String markPadding = contextPadding + repeat(' ', lineOffset - 1);
		return repeat(' ', lineNum.length() + 1)
				+ "at " + fileName + ':' + lineNum + ':' + lineOffset + '\n'
				+ contextPadding + '\n'
				+ lineNum + " |" + readFullLine() + '\n'
				+ markPadding + '^' + repeat('~', tokenLen - 1 - offsetInToken) + '\n'
				+ markPadding + msg + '\n';
	}

	private String readFullLine() {
		if (savedState == State.AT_LINE_END || savedState == State.AT_FILE_END) {
			return lineBuffer.toString().replaceAll("[\n\r]", "");
		}
		try {
			while (true) {
				int cp = this.reader.read();
				if (cp == -1 || cp == '\n' || cp == '\r') {
					break;
				}
				lineBuffer.appendCodePoint(cp);
			}
		} catch (Exception e) {
			// ignore
		}
		return lineBuffer.toString();
	}
}
