package io.github.skylot.raung.asm.impl.parser.code;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Label;
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
		parseOpcode(mth, mv, parser, opcode, format);
		parser.lineEnd();
		mth.addInsn();
	}

	private static void parseOpcode(MethodData mth, MethodVisitor mv, RaungParser parser,
			int opcode, JavaOpCodeFormat format) {
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

			case LOOKUP_SWITCH:
				parseLookupSwitch(mth, mv, parser);
				break;

			case UNKNOWN:
				throw new RaungAsmException("TODO: Missing format for opcode: "
						+ "0x" + Integer.toHexString(opcode) + " (" + JavaOpCodes.getName(opcode) + ")");

			default:
				throw new RaungAsmException("TODO: Missing parser for opcode: "
						+ "0x" + Integer.toHexString(opcode) + " (" + JavaOpCodes.getName(opcode) + ")");
		}
	}

	private static void parseLookupSwitch(MethodData mth, MethodVisitor mv, RaungParser parser) {
		parser.lineEnd();
		Label defLabel = null;
		List<Integer> keys = new ArrayList<>();
		List<RaungLabel> labels = new ArrayList<>();
		while (true) {
			String key = parser.skipToToken();
			if (key == null) {
				throw new RaungAsmException("Missing case in lookup switch");
			}
			if (key.equals(".end")) {
				parser.consumeToken("lookupswitch");
				break;
			}
			if (key.equals("default")) {
				defLabel = RaungLabel.ref(mth, parser.readToken().substring(1));
			} else {
				keys.add(Integer.parseInt(key));
				labels.add(RaungLabel.ref(mth, parser.readToken().substring(1)));
			}
		}
		if (defLabel == null) {
			throw new RaungAsmException("'default' case is required in lookupswtich");
		}
		mv.visitLookupSwitchInsn(defLabel, toIntArray(keys), labels.toArray(new Label[0]));
	}

	private static int[] toIntArray(List<Integer> list) {
		int size = list.size();
		int[] arr = new int[size];
		for (int i = 0; i < size; i++) {
			arr[i] = list.get(i);
		}
		return arr;
	}
}
