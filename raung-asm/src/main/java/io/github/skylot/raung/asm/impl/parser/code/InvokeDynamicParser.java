package io.github.skylot.raung.asm.impl.parser.code;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.asm.HandleTag;

public class InvokeDynamicParser {

	public static void parse(MethodVisitor mv, RaungParser parser) {
		String name = parser.readToken();
		String descriptor = parser.readToken();
		Handle handle = null;
		List<Object> args = new ArrayList<>(3);
		while (true) {
			String token = parser.skipToToken();
			if (token == null) {
				throw new RaungAsmException("Missing '.end invokedynamic' directive");
			}
			if (token.equals(".end")) {
				parser.consumeToken("invokedynamic");
				break;
			}
			if (token.equals(".handle")) {
				handle = parseHandle(parser);
			} else if (token.equals(".arg")) {
				parseArg(parser, args);
			} else {
				throw new RaungAsmException("Unexpected token for 'invokedynamic' scope", token);
			}
		}
		mv.visitInvokeDynamicInsn(name, descriptor, handle, args.toArray(new Object[0]));
	}

	private static Handle parseHandle(RaungParser parser) {
		String tagStr = parser.readToken();
		HandleTag tag = HandleTag.getByName(tagStr);
		if (tag == null) {
			throw new RaungAsmException("Unknown handle tag", tagStr);
		}
		String owner = parser.readToken();
		String name = parser.readToken();
		String descriptor = parser.readToken();
		parser.lineEnd();
		boolean itf = tag == HandleTag.INVOKEINTERFACE;
		return new Handle(tag.getTag(), owner, name, descriptor, itf);
	}

	private static void parseArg(RaungParser parser, List<Object> args) {
		int index = parser.readInt();
		String token = parser.readToken();
		switch (token) {
			case ".handle":
				args.add(index, parseHandle(parser));
				break;
			case ".methodtype":
				args.add(index, parseMethodType(parser));
				break;
			default:
				throw new RaungAsmException("Unknown directive for 'invokedynamic' scope", token);
		}
	}

	private static Type parseMethodType(RaungParser parser) {
		return Type.getMethodType(parser.readToken());
	}
}
