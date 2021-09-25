package io.github.skylot.raung.asm.impl.parser.directives;

import java.util.EnumMap;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.code.OpCodeParser;
import io.github.skylot.raung.asm.impl.parser.data.ClassData;
import io.github.skylot.raung.asm.impl.parser.data.MethodData;
import io.github.skylot.raung.asm.impl.parser.data.RaungLabel;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.Directive;

import static io.github.skylot.raung.common.Directive.END;
import static io.github.skylot.raung.common.Directive.LINE;
import static io.github.skylot.raung.common.Directive.LOCAL;
import static io.github.skylot.raung.common.Directive.MAX;
import static io.github.skylot.raung.common.Directive.SIGNATURE;

public class MethodDirectives {

	private static final Map<Directive, IDirectivesProcessor<MethodData>> PROCESSOR_MAP;

	static {
		Map<Directive, IDirectivesProcessor<MethodData>> map = new EnumMap<>(Directive.class);
		map.put(SIGNATURE, MethodDirectives::processSignature);
		map.put(MAX, MethodDirectives::processMax);
		map.put(LINE, MethodDirectives::processLine);
		map.put(LOCAL, MethodDirectives::processLocal);
		PROCESSOR_MAP = map;
	}

	public static MethodData parseMethod(ClassData classData, RaungParser parser) {
		MethodData mth = new MethodData(classData);
		mth.setAccessFlags(parser.readAccessFlags());
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
					OpCodeParser.process(mth, getAsmMthVisitor(mth), parser, token);
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
				MethodVisitor mv = getAsmMthVisitor(mth);
				mv.visitMaxs(mth.getMaxStack(), mth.getMaxLocals());
				mv.visitEnd();
				return true;

			default:
				throw new RaungAsmException("Unexpected token for .end", token);
		}
	}

	private static MethodVisitor getAsmMthVisitor(MethodData mth) {
		MethodVisitor mv = mth.getMethodVisitor();
		if (mv != null) {
			return mv;
		}
		ClassWriter cw = mth.getClassData().getAsmClassWriter();
		mv = cw.visitMethod(mth.getAccessFlags(), mth.getName(), mth.getDescriptor(),
				mth.getSignature(), mth.getExceptions().toArray(new String[0]));
		mth.setMethodVisitor(mv);
		return mv;
	}

	private static void process(Directive token, RaungParser parser, MethodData methodData) {
		IDirectivesProcessor<MethodData> processor = PROCESSOR_MAP.get(token);
		if (processor == null) {
			throw new RaungAsmException("Not a method directive", token.token());
		}
		processor.process(parser, methodData);
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
		// TODO: process labels
		// getAsmMthVisitor(methodData).visitLocalVariable(name, type, signature, startLabel, endLabel);
	}

	private static void processLocalEnd(RaungParser parser, MethodData methodData) {
		int varNum = parser.readInt();
		parser.lineEnd();
		// TODO: process labels
	}

	private static void processLine(RaungParser parser, MethodData methodData) {
		int line = parser.readInt();
		getAsmMthVisitor(methodData).visitLineNumber(line, makeLabel(methodData));
		parser.lineEnd();
	}

	private static void processLabel(MethodData mth, RaungParser parser, String token) {
		parser.lineEnd();
		Label label = new RaungLabel(token.substring(1));
		getAsmMthVisitor(mth).visitLabel(label);
	}

	private static Label makeLabel(MethodData mth) {
		Label label = new RaungLabel(String.format("L%d", mth.getInsnsCount()));
		getAsmMthVisitor(mth).visitLabel(label);
		return label;
	}
}
