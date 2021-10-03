package io.github.skylot.raung.common;

import java.util.HashMap;
import java.util.Map;

import static io.github.skylot.raung.common.JavaOpCodeFormat.FIELD;
import static io.github.skylot.raung.common.JavaOpCodeFormat.IINC;
import static io.github.skylot.raung.common.JavaOpCodeFormat.INT;
import static io.github.skylot.raung.common.JavaOpCodeFormat.INVOKE_DYNAMIC;
import static io.github.skylot.raung.common.JavaOpCodeFormat.JUMP;
import static io.github.skylot.raung.common.JavaOpCodeFormat.LDC;
import static io.github.skylot.raung.common.JavaOpCodeFormat.METHOD;
import static io.github.skylot.raung.common.JavaOpCodeFormat.NEW_ARRAY;
import static io.github.skylot.raung.common.JavaOpCodeFormat.NO_ARGS;
import static io.github.skylot.raung.common.JavaOpCodeFormat.SWITCH;
import static io.github.skylot.raung.common.JavaOpCodeFormat.TYPE;
import static io.github.skylot.raung.common.JavaOpCodeFormat.UNKNOWN;
import static io.github.skylot.raung.common.JavaOpCodeFormat.VAR;

@SuppressWarnings("SpellCheckingInspection")
public class JavaOpCodes {
	private static final int OPCODES_COUNT = 0xCA;

	private static final String[] OPCODES = new String[OPCODES_COUNT];
	private static final Map<String, Integer> NAMES_MAP = new HashMap<>(0xCA);
	private static final JavaOpCodeFormat[] OPCODES_FORMAT = new JavaOpCodeFormat[OPCODES_COUNT];

	static {
		add(0x0, "nop", NO_ARGS);
		add(0x1, "aconst_null", NO_ARGS);
		add(0x2, "iconst_m1", NO_ARGS);
		add(0x3, "iconst_0", NO_ARGS);
		add(0x4, "iconst_1", NO_ARGS);
		add(0x5, "iconst_2", NO_ARGS);
		add(0x6, "iconst_3", NO_ARGS);
		add(0x7, "iconst_4", NO_ARGS);
		add(0x8, "iconst_5", NO_ARGS);
		add(0x9, "lconst_0", NO_ARGS);
		add(0xa, "lconst_1", NO_ARGS);
		add(0xb, "fconst_0", NO_ARGS);
		add(0xc, "fconst_1", NO_ARGS);
		add(0xd, "fconst_2", NO_ARGS);
		add(0xe, "dconst_0", NO_ARGS);
		add(0xf, "dconst_1", NO_ARGS);
		add(0x10, "bipush", INT);
		add(0x11, "sipush", INT);
		add(0x12, "ldc", LDC);
		add(0x13, "ldc_w", LDC);
		add(0x14, "ldc2_w", LDC);
		add(0x15, "iload", VAR);
		add(0x16, "lload", VAR);
		add(0x17, "fload", VAR);
		add(0x18, "dload", VAR);
		add(0x19, "aload", VAR);
		add(0x1a, "iload_0", NO_ARGS);
		add(0x1b, "iload_1", NO_ARGS);
		add(0x1c, "iload_2", NO_ARGS);
		add(0x1d, "iload_3", NO_ARGS);
		add(0x1e, "lload_0", NO_ARGS);
		add(0x1f, "lload_1", NO_ARGS);
		add(0x20, "lload_2", NO_ARGS);
		add(0x21, "lload_3", NO_ARGS);
		add(0x22, "fload_0", NO_ARGS);
		add(0x23, "fload_1", NO_ARGS);
		add(0x24, "fload_2", NO_ARGS);
		add(0x25, "fload_3", NO_ARGS);
		add(0x26, "dload_0", NO_ARGS);
		add(0x27, "dload_1", NO_ARGS);
		add(0x28, "dload_2", NO_ARGS);
		add(0x29, "dload_3", NO_ARGS);
		add(0x2a, "aload_0", NO_ARGS);
		add(0x2b, "aload_1", NO_ARGS);
		add(0x2c, "aload_2", NO_ARGS);
		add(0x2d, "aload_3", NO_ARGS);
		add(0x2e, "iaload", NO_ARGS);
		add(0x2f, "laload", NO_ARGS);
		add(0x30, "faload", NO_ARGS);
		add(0x31, "daload", NO_ARGS);
		add(0x32, "aaload", NO_ARGS);
		add(0x33, "baload", NO_ARGS);
		add(0x34, "caload", NO_ARGS);
		add(0x35, "saload", NO_ARGS);
		add(0x36, "istore", VAR);
		add(0x37, "lstore", VAR);
		add(0x38, "fstore", VAR);
		add(0x39, "dstore", VAR);
		add(0x3a, "astore", VAR);
		add(0x3b, "istore_0", NO_ARGS);
		add(0x3c, "istore_1", NO_ARGS);
		add(0x3d, "istore_2", NO_ARGS);
		add(0x3e, "istore_3", NO_ARGS);
		add(0x3f, "lstore_0", NO_ARGS);
		add(0x40, "lstore_1", NO_ARGS);
		add(0x41, "lstore_2", NO_ARGS);
		add(0x42, "lstore_3", NO_ARGS);
		add(0x43, "fstore_0", NO_ARGS);
		add(0x44, "fstore_1", NO_ARGS);
		add(0x45, "fstore_2", NO_ARGS);
		add(0x46, "fstore_3", NO_ARGS);
		add(0x47, "dstore_0", NO_ARGS);
		add(0x48, "dstore_1", NO_ARGS);
		add(0x49, "dstore_2", NO_ARGS);
		add(0x4a, "dstore_3", NO_ARGS);
		add(0x4b, "astore_0", NO_ARGS);
		add(0x4c, "astore_1", NO_ARGS);
		add(0x4d, "astore_2", NO_ARGS);
		add(0x4e, "astore_3", NO_ARGS);
		add(0x4f, "iastore", NO_ARGS);
		add(0x50, "lastore", NO_ARGS);
		add(0x51, "fastore", NO_ARGS);
		add(0x52, "dastore", NO_ARGS);
		add(0x53, "aastore", NO_ARGS);
		add(0x54, "bastore", NO_ARGS);
		add(0x55, "castore", NO_ARGS);
		add(0x56, "sastore", NO_ARGS);
		add(0x57, "pop", NO_ARGS);
		add(0x58, "pop2", NO_ARGS);
		add(0x59, "dup", NO_ARGS);
		add(0x5a, "dup_x1", NO_ARGS);
		add(0x5b, "dup_x2", NO_ARGS);
		add(0x5c, "dup2", NO_ARGS);
		add(0x5d, "dup2_x1", NO_ARGS);
		add(0x5e, "dup2_x2", NO_ARGS);
		add(0x5f, "swap", NO_ARGS);
		add(0x60, "iadd", NO_ARGS);
		add(0x61, "ladd", NO_ARGS);
		add(0x62, "fadd", NO_ARGS);
		add(0x63, "dadd", NO_ARGS);
		add(0x64, "isub", NO_ARGS);
		add(0x65, "lsub", NO_ARGS);
		add(0x66, "fsub", NO_ARGS);
		add(0x67, "dsub", NO_ARGS);
		add(0x68, "imul", NO_ARGS);
		add(0x69, "lmul", NO_ARGS);
		add(0x6a, "fmul", NO_ARGS);
		add(0x6b, "dmul", NO_ARGS);
		add(0x6c, "idiv", NO_ARGS);
		add(0x6d, "ldiv", NO_ARGS);
		add(0x6e, "fdiv", NO_ARGS);
		add(0x6f, "ddiv", NO_ARGS);
		add(0x70, "irem", NO_ARGS);
		add(0x71, "lrem", NO_ARGS);
		add(0x72, "frem", NO_ARGS);
		add(0x73, "drem", NO_ARGS);
		add(0x74, "ineg", NO_ARGS);
		add(0x75, "lneg", NO_ARGS);
		add(0x76, "fneg", NO_ARGS);
		add(0x77, "dneg", NO_ARGS);
		add(0x78, "ishl", NO_ARGS);
		add(0x79, "lshl", NO_ARGS);
		add(0x7a, "ishr", NO_ARGS);
		add(0x7b, "lshr", NO_ARGS);
		add(0x7c, "iushr", NO_ARGS);
		add(0x7d, "lushr", NO_ARGS);
		add(0x7e, "iand", NO_ARGS);
		add(0x7f, "land", NO_ARGS);
		add(0x80, "ior", NO_ARGS);
		add(0x81, "lor", NO_ARGS);
		add(0x82, "ixor", NO_ARGS);
		add(0x83, "lxor", NO_ARGS);
		add(0x84, "iinc", IINC);
		add(0x85, "i2l", NO_ARGS);
		add(0x86, "i2f", NO_ARGS);
		add(0x87, "i2d", NO_ARGS);
		add(0x88, "l2i", NO_ARGS);
		add(0x89, "l2f", NO_ARGS);
		add(0x8a, "l2d", NO_ARGS);
		add(0x8b, "f2i", NO_ARGS);
		add(0x8c, "f2l", NO_ARGS);
		add(0x8d, "f2d", NO_ARGS);
		add(0x8e, "d2i", NO_ARGS);
		add(0x8f, "d2l", NO_ARGS);
		add(0x90, "d2f", NO_ARGS);
		add(0x91, "i2b", NO_ARGS);
		add(0x92, "i2c", NO_ARGS);
		add(0x93, "i2s", NO_ARGS);
		add(0x94, "lcmp", NO_ARGS);
		add(0x95, "fcmpl", NO_ARGS);
		add(0x96, "fcmpg", NO_ARGS);
		add(0x97, "dcmpl", NO_ARGS);
		add(0x98, "dcmpg", NO_ARGS);
		add(0x99, "ifeq", JUMP);
		add(0x9a, "ifne", JUMP);
		add(0x9b, "iflt", JUMP);
		add(0x9c, "ifge", JUMP);
		add(0x9d, "ifgt", JUMP);
		add(0x9e, "ifle", JUMP);
		add(0x9f, "if_icmpeq", JUMP);
		add(0xa0, "if_icmpne", JUMP);
		add(0xa1, "if_icmplt", JUMP);
		add(0xa2, "if_icmpge", JUMP);
		add(0xa3, "if_icmpgt", JUMP);
		add(0xa4, "if_icmple", JUMP);
		add(0xa5, "if_acmpeq", JUMP);
		add(0xa6, "if_acmpne", JUMP);
		add(0xa7, "goto", JUMP);
		add(0xa8, "jsr", UNKNOWN);
		add(0xa9, "ret", UNKNOWN);
		add(0xaa, "tableswitch", SWITCH);
		add(0xab, "lookupswitch", SWITCH);
		add(0xac, "ireturn", NO_ARGS);
		add(0xad, "lreturn", NO_ARGS);
		add(0xae, "freturn", NO_ARGS);
		add(0xaf, "dreturn", NO_ARGS);
		add(0xb0, "areturn", NO_ARGS);
		add(0xb1, "return", NO_ARGS);
		add(0xb2, "getstatic", FIELD);
		add(0xb3, "putstatic", FIELD);
		add(0xb4, "getfield", FIELD);
		add(0xb5, "putfield", FIELD);
		add(0xb6, "invokevirtual", METHOD);
		add(0xb7, "invokespecial", METHOD);
		add(0xb8, "invokestatic", METHOD);
		add(0xb9, "invokeinterface", METHOD);
		add(0xba, "invokedynamic", INVOKE_DYNAMIC);
		add(0xbb, "new", TYPE);
		add(0xbc, "newarray", NEW_ARRAY);
		add(0xbd, "anewarray", TYPE);
		add(0xbe, "arraylength", NO_ARGS);
		add(0xbf, "athrow", NO_ARGS);
		add(0xc0, "checkcast", TYPE);
		add(0xc1, "instanceof", TYPE);
		add(0xc2, "monitorenter", NO_ARGS);
		add(0xc3, "monitorexit", NO_ARGS);
		add(0xc4, "wide", UNKNOWN);
		add(0xc5, "multianewarray", UNKNOWN);
		add(0xc6, "ifnull", JUMP);
		add(0xc7, "ifnonnull", JUMP);

		alias("switch", 0xaa);
	}

	private static void add(int opcode, String name, JavaOpCodeFormat format) {
		OPCODES[opcode] = name;
		NAMES_MAP.put(name, opcode);
		OPCODES_FORMAT[opcode] = format;
	}

	private static void alias(String name, int opcode) {
		NAMES_MAP.put(name, opcode);
	}

	public static String getName(int opcode) {
		return OPCODES[opcode];
	}

	public static int getOpcode(String name) {
		Integer opcode = NAMES_MAP.get(name);
		if (opcode == null) {
			return -1;
		}
		return opcode;
	}

	public static JavaOpCodeFormat getFormat(int opcode) {
		return OPCODES_FORMAT[opcode];
	}
}
