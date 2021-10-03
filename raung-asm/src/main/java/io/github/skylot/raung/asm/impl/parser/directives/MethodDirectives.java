package io.github.skylot.raung.asm.impl.parser.directives;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.code.OpCodeParser;
import io.github.skylot.raung.asm.impl.parser.data.ClassData;
import io.github.skylot.raung.asm.impl.parser.data.MethodData;
import io.github.skylot.raung.asm.impl.parser.data.RaungLabel;
import io.github.skylot.raung.asm.impl.parser.data.RaungLocalVar;
import io.github.skylot.raung.asm.impl.utils.AsmLibException;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.Directive;
import io.github.skylot.raung.common.RaungAccessFlags.Scope;
import io.github.skylot.raung.common.asm.StackType;

import static io.github.skylot.raung.common.Directive.CATCH;
import static io.github.skylot.raung.common.Directive.END;
import static io.github.skylot.raung.common.Directive.LINE;
import static io.github.skylot.raung.common.Directive.LOCAL;
import static io.github.skylot.raung.common.Directive.MAX;
import static io.github.skylot.raung.common.Directive.SIGNATURE;
import static io.github.skylot.raung.common.Directive.STACK;
import static io.github.skylot.raung.common.Directive.THROW;

public class MethodDirectives {

	private static final Map<Directive, IDirectivesProcessor<MethodData>> PROCESSOR_MAP;

	static {
		Map<Directive, IDirectivesProcessor<MethodData>> map = new EnumMap<>(Directive.class);
		map.put(THROW, MethodDirectives::processThrows);
		map.put(SIGNATURE, MethodDirectives::processSignature);
		map.put(MAX, MethodDirectives::processMax);
		map.put(LINE, MethodDirectives::processLine);
		map.put(LOCAL, MethodDirectives::processLocal);
		map.put(STACK, MethodDirectives::processStack);
		map.put(CATCH, MethodDirectives::processCatch);
		PROCESSOR_MAP = map;
	}

	public static MethodData parseMethod(ClassData classData, RaungParser parser) {
		MethodData mth = new MethodData(classData);
		mth.setAccessFlags(parser.readAccessFlags(Scope.METHOD));
		String nameToken = parser.readToken();
		int argsStart = nameToken.indexOf('(');
		if (argsStart == -1) {
			throw new RaungAsmException("Incorrect method description", nameToken);
		}
		mth.setName(nameToken.substring(0, argsStart));
		mth.setDescriptor(nameToken.substring(argsStart));
		parser.lineEnd();
		while (true) {
			String token = parser.skipToToken();
			if (token == null) {
				throw new RaungAsmException("Unexpected method end");
			}
			switch (token.charAt(0)) {
				case '.':
					Directive directive = Directive.parseToken(token);
					if (directive == null) {
						throw new RaungAsmException("Unknown directive", token);
					}
					if (directive == END) {
						if (processEnd(mth, parser)) {
							return mth;
						}
					} else {
						process(directive, parser, mth);
					}
					break;

				case ':':
					processLabel(mth, parser, token);
					break;

				default:
					OpCodeParser.process(mth, mth.getAsmMethodVisitor(), parser, token);
					break;
			}
		}
	}

	private static boolean processEnd(MethodData mth, RaungParser parser) {
		String token = parser.readToken();
		switch (token) {
			case "local":
				processLocalEnd(parser, mth);
				return false;

			case "method":
				processMethodEnd(mth);
				return true;

			default:
				throw new RaungAsmException("Unexpected token for .end", token);
		}
	}

	private static void process(Directive token, RaungParser parser, MethodData methodData) {
		IDirectivesProcessor<MethodData> processor = PROCESSOR_MAP.get(token);
		if (processor == null) {
			throw new RaungAsmException("Not a method directive", token.token());
		}
		processor.process(parser, methodData);
	}

	private static void processThrows(RaungParser parser, MethodData methodData) {
		methodData.addThrow(parser.readType());
		parser.lineEnd();
	}

	private static void processSignature(RaungParser parser, MethodData methodData) {
		methodData.setSignature(parser.readToken());
		parser.lineEnd();
	}

	private static void processMax(RaungParser parser, MethodData methodData) {
		String type = parser.readToken();
		switch (type) {
			case "stack":
				methodData.setMaxStack(parser.readInt());
				break;
			case "locals":
				methodData.setMaxLocals(parser.readInt());
				break;
			default:
				throw new RaungAsmException("Unknown max type: '" + type + "'. Should be 'stack' or 'locals'");
		}
		parser.lineEnd();
	}

	private static void processLocal(RaungParser parser, MethodData methodData) {
		int varNum = parser.readInt();
		String name = parser.readString();
		String type = parser.readToken();
		String signature = parser.tryGetToken();
		if (signature != null) {
			parser.lineEnd();
		}
		RaungLabel startLabel = attachLabel(methodData, null);
		methodData.addLocalVar(new RaungLocalVar(varNum, name, type, signature, startLabel));
	}

	private static void processLocalEnd(RaungParser parser, MethodData methodData) {
		int varNum = parser.readInt();
		parser.lineEnd();
		RaungLabel endLabel = attachLabel(methodData, null);
		RaungLocalVar localVar = methodData.getLocalVar(varNum);
		if (localVar == null) {
			throw new RaungAsmException("Unknown local variable with number: " + varNum);
		}
		visitLocalVar(methodData, localVar, endLabel);
	}

	private static void visitLocalVar(MethodData methodData, RaungLocalVar localVar, RaungLabel endLabel) {
		methodData.getAsmMethodVisitor().visitLocalVariable(localVar.getName(), localVar.getType(),
				localVar.getSignature(), localVar.getStartLabel(), endLabel, localVar.getNumber());
		localVar.setVisited(true);
	}

	private static void processLine(RaungParser parser, MethodData methodData) {
		int line = parser.readInt();
		methodData.getAsmMethodVisitor().visitLineNumber(line, attachLabel(methodData, null));
		parser.lineEnd();
	}

	private static void processLabel(MethodData mth, RaungParser parser, String labelName) {
		parser.lineEnd();
		RaungLabel existLabel = mth.getLabel(labelName);
		if (existLabel != null) {
			if (existLabel.getPos() != -1) {
				throw new RaungAsmException("Label already defined with name: " + labelName);
			}
			visitLabel(mth, existLabel);
		} else {
			attachLabel(mth, labelName);
		}
	}

	private static RaungLabel attachLabel(MethodData mth, @Nullable String name) {
		int pos = mth.getInsnsCount();
		RaungLabel existLabel = mth.getLabel(pos);
		if (existLabel != null) {
			return existLabel;
		}
		String labelName;
		if (name != null) {
			labelName = name;
		} else {
			// internal label (don't appear in code)
			labelName = String.format("#L%d", pos);
		}
		RaungLabel label = RaungLabel.makeNew(mth, labelName);
		return visitLabel(mth, label);
	}

	private static RaungLabel visitLabel(MethodData mth, RaungLabel label) {
		label.setPos(mth.getInsnsCount());
		mth.getAsmMethodVisitor().visitLabel(label);
		return label;
	}

	private static void processMethodEnd(MethodData mth) {
		MethodVisitor mv = mth.getAsmMethodVisitor();
		visitLocalVars(mth);
		try {
			mv.visitMaxs(mth.getMaxStack(), mth.getMaxLocals());
			mv.visitEnd();
		} catch (Exception e) {
			throw new AsmLibException("Failed to build method: " + mth.getName() + mth.getDescriptor()
					+ ". Error: " + e.getMessage(), e);
		}
	}

	private static void visitLocalVars(MethodData mth) {
		List<RaungLocalVar> notVisitedLocalVars = mth.getLocalVars().stream()
				.filter(lv -> !lv.isVisited())
				.collect(Collectors.toList());
		if (notVisitedLocalVars.isEmpty()) {
			return;
		}
		RaungLabel endLabel = attachLabel(mth, null);
		for (RaungLocalVar localVar : notVisitedLocalVars) {
			if (!localVar.isVisited()) {
				visitLocalVar(mth, localVar, endLabel);
			}
		}
	}

	private static void processStack(RaungParser parser, MethodData methodData) {
		String type = parser.readToken();
		MethodVisitor mv = methodData.getAsmMethodVisitor();
		switch (type) {
			case "same":
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				break;

			case "same1":
				Object value = parseStackType(parser.readToken(), parser, methodData);
				mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { value });
				break;

			case "chop":
				mv.visitFrame(Opcodes.F_CHOP, parser.readInt(), null, 0, null);
				break;

			case "append": {
				parser.lineEnd();
				List<Object> locals = new ArrayList<>();
				while (true) {
					String token = parser.skipToToken();
					if (token == null) {
						throw new RaungAsmException("Unexpected end of .stack directive");
					}
					if (token.equals(".end")) {
						parser.consumeToken("stack");
						break;
					}
					locals.add(parseStackType(token, parser, methodData));
				}
				mv.visitFrame(Opcodes.F_APPEND, locals.size(), locals.toArray(new Object[0]), 0, null);
				break;
			}
			case "full": {
				parser.lineEnd();
				List<Object> locals = new ArrayList<>();
				List<Object> stack = new ArrayList<>();
				while (true) {
					String token = parser.skipToToken();
					if (token == null) {
						throw new RaungAsmException("Unexpected end of .stack directive");
					}
					if (token.equals(".end")) {
						parser.consumeToken("stack");
						break;
					}
					switch (token) {
						case "local":
							parser.readInt();
							locals.add(parseStackType(parser.readToken(), parser, methodData));
							break;
						case "stack":
							parser.readInt();
							stack.add(parseStackType(parser.readToken(), parser, methodData));
							break;
					}
					parser.lineEnd();
				}
				mv.visitFrame(Opcodes.F_FULL,
						locals.size(), locals.toArray(new Object[0]),
						stack.size(), stack.toArray(new Object[0]));
				break;
			}

			default:
				throw new RaungAsmException("Unexpected stack type: " + type);
		}
		parser.lineEnd();
	}

	private static Object parseStackType(String token, RaungParser parser, MethodData methodData) {
		StackType type = StackType.getByName(token);
		if (type != null) {
			return type.getValue();
		}
		if (token.equals("Uninitialized")) {
			String label = parser.readToken();
			return RaungLabel.ref(methodData, label);
		}
		// should be type
		return token;
	}

	private static void processCatch(RaungParser parser, MethodData methodData) {
		String typeStr = parser.readToken();
		RaungLabel startLabel = RaungLabel.ref(methodData, parser.readToken());
		parser.consumeToken("..");
		RaungLabel endLabel = RaungLabel.ref(methodData, parser.readToken());
		parser.consumeToken("goto");
		RaungLabel handlerLabel = RaungLabel.ref(methodData, parser.readToken());
		String type = typeStr.equals("all") ? null : typeStr;
		methodData.getAsmMethodVisitor().visitTryCatchBlock(startLabel, endLabel, handlerLabel, type);
	}
}
