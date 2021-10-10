package io.github.skylot.raung.asm.impl.parser.code;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.data.MethodData;
import io.github.skylot.raung.asm.impl.parser.data.RaungLabel;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.JavaOpCodeFormat;
import io.github.skylot.raung.common.JavaOpCodes;

public class OpCodeParser {

	public static void process(MethodData mth, MethodVisitor mv, RaungParser parser, String token) {
		int opcode = JavaOpCodes.getOpcode(token);
		if (opcode == -1) {
			throw new RaungAsmException("Unknown opcode", token);
		}
		JavaOpCodeFormat format = JavaOpCodes.getFormat(opcode);
		parseOpcode(mth, mv, parser, opcode, token, format);
		parser.lineEnd();
		mth.addInsn();
	}

	private static void parseOpcode(MethodData mth, MethodVisitor mv, RaungParser parser,
			int opcode, String token, JavaOpCodeFormat format) {
		switch (format) {
			case NO_ARGS:
				mv.visitInsn(opcode);
				break;

			case INT:
				mv.visitIntInsn(opcode, parser.readInt());
				break;

			case TYPE:
				mv.visitTypeInsn(opcode, parser.readToken());
				break;

			case FIELD:
				mv.visitFieldInsn(opcode, parser.readToken(), parser.readToken(), parser.readToken());
				break;

			case METHOD:
				String owner = null;
				boolean isInterface = opcode == Opcodes.INVOKEINTERFACE;
				if (!isInterface) {
					// read optional 'interface' token
					String nextToken = parser.readToken();
					if (nextToken.equals("interface")) {
						isInterface = true;
					} else {
						owner = nextToken;
					}
				}
				if (owner == null) {
					owner = parser.readToken();
				}
				String name = parser.readToken();
				String descriptor = parser.readToken();
				mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
				break;

			case VAR:
				mv.visitVarInsn(opcode, parser.readInt());
				break;

			case JUMP:
				String labelName = parser.readToken();
				mv.visitJumpInsn(opcode, RaungLabel.ref(mth, labelName));
				break;

			case LDC:
				mv.visitLdcInsn(ValueParser.process(parser.readToken()));
				break;

			case IINC:
				mv.visitIincInsn(parser.readInt(), parser.readInt());
				break;

			case NEW_ARRAY:
				mv.visitIntInsn(Opcodes.NEWARRAY, parser.readInt());
				break;

			case NEW_MULTI_ARRAY:
				mv.visitMultiANewArrayInsn(parser.readType(), parser.readInt());
				break;

			case SWITCH:
				SwitchParser.parse(mth, mv, parser, token);
				break;

			case INVOKE_DYNAMIC:
				InvokeDynamicParser.parse(mv, parser);
				break;

			case UNKNOWN:
				throw new RaungAsmException("TODO: Missing format for opcode: "
						+ "0x" + Integer.toHexString(opcode) + " (" + JavaOpCodes.getName(opcode) + ")");

			default:
				throw new RaungAsmException("TODO: Missing parser for opcode: "
						+ "0x" + Integer.toHexString(opcode) + " (" + JavaOpCodes.getName(opcode) + ")");
		}
	}
}
