package io.github.skylot.raung.asm.impl.parser.directives;

import java.util.EnumMap;
import java.util.Map;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.data.ClassData;
import io.github.skylot.raung.asm.impl.parser.data.FieldData;
import io.github.skylot.raung.asm.impl.parser.data.MethodData;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.DirectiveToken;

import static io.github.skylot.raung.common.DirectiveToken.CLASS;
import static io.github.skylot.raung.common.DirectiveToken.END;
import static io.github.skylot.raung.common.DirectiveToken.FIELD;
import static io.github.skylot.raung.common.DirectiveToken.INTERFACE;
import static io.github.skylot.raung.common.DirectiveToken.METHOD;
import static io.github.skylot.raung.common.DirectiveToken.SOURCE;
import static io.github.skylot.raung.common.DirectiveToken.SUPER;
import static io.github.skylot.raung.common.DirectiveToken.VERSION;

public class ClassDirectives {

	private static final Map<DirectiveToken, IDirectivesProcessor<ClassData>> PROCESSOR_MAP;

	static {
		Map<DirectiveToken, IDirectivesProcessor<ClassData>> map = new EnumMap<>(DirectiveToken.class);
		map.put(VERSION, ClassDirectives::processVersion);
		map.put(CLASS, ClassDirectives::processClass);
		map.put(SUPER, ClassDirectives::processSuper);
		map.put(INTERFACE, ClassDirectives::processInterface);
		map.put(SOURCE, ClassDirectives::processSource);
		map.put(FIELD, ClassDirectives::processField);
		map.put(METHOD, ClassDirectives::processMethod);
		PROCESSOR_MAP = map;
	}

	public static void process(DirectiveToken token, RaungParser parser, ClassData cw) {
		IDirectivesProcessor<ClassData> processor = PROCESSOR_MAP.get(token);
		if (processor == null) {
			throw new RaungAsmException("Unexpected class directive: " + token.token());
		}
		processor.process(parser, cw);
	}

	private static void processVersion(RaungParser parser, ClassData clsData) {
		int version = parser.readInt();
		clsData.setVersion(version);
		parser.lineEnd();
	}

	private static void processClass(RaungParser parser, ClassData clsData) {
		clsData.setAccessFlags(parser.readAccessFlags());
		clsData.setName(parser.readToken());
		parser.lineEnd();
	}

	private static void processSuper(RaungParser parser, ClassData classData) {
		classData.setSuperCls(parser.readType());
		parser.lineEnd();
	}

	private static void processInterface(RaungParser parser, ClassData classData) {
		classData.getInterfaces().add(parser.readType());
		parser.lineEnd();
	}

	private static void processSource(RaungParser parser, ClassData clsData) {
		clsData.setSource(parser.readString());
		parser.lineEnd();
	}

	private static void processField(RaungParser parser, ClassData classData) {
		FieldData field = new FieldData();
		field.setAccessFlags(parser.readAccessFlags());
		field.setName(parser.readToken());
		field.setType(parser.readType());
		classData.getFields().add(field);
		parser.lineEnd();
		DirectiveToken nextToken = DirectiveToken.parseToken(parser.peekToken());
		if (DirectiveToken.isFieldSubDirective(nextToken)) {
			while (true) {
				DirectiveToken directive = parser.readDirective();
				if (directive == null) {
					throw new RaungAsmException("Missing '.end field' directive");
				}
				if (directive == END) {
					parser.consumeToken("field");
					parser.lineEnd();
					return;
				}
				FieldDirectives.process(directive, parser, field);
			}
		}
	}

	private static void processMethod(RaungParser parser, ClassData classData) {
		MethodData mth = new MethodData();
		mth.setAccessFlags(parser.readAccessFlags());
		mth.setName(parser.readToken());
		parser.lineEnd();
		parser.consumeToken(END.token());
		parser.consumeToken("method");
	}
}
