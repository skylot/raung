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
		parseOpcode(mv, parser, opcode, format);
		parser.lineEnd();
	}

	private static void parseOpcode(MethodVisitor mv, RaungParser parser, int opcode, JavaOpCodeFormat format) {
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
				boolean iface = opcode == Opcodes.INVOKEINTERFACE;
				mv.visitMethodInsn(opcode, parser.readToken(), parser.readToken(), parser.readToken(), iface);
				break;

			case VAR:
				mv.visitVarInsn(opcode, parser.readInt());
				break;

			case JUMP:
				String labelName = parser.readToken().substring(1);
				RaungLabel label = new RaungLabel(labelName);
				mv.visitLabel(label);
				mv.visitJumpInsn(opcode, label);
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

			case UNKNOWN:
				throw new RaungAsmException("TODO: Missing format for opcode: "
						+ "0x" + Integer.toHexString(opcode) + " (" + JavaOpCodes.getName(opcode) + ")");
		}
	}
}
