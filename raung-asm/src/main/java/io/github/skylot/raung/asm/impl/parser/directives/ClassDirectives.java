package io.github.skylot.raung.asm.impl.parser.directives;

import java.util.EnumMap;
import java.util.Map;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.data.ClassData;
import io.github.skylot.raung.asm.impl.parser.data.FieldData;
import io.github.skylot.raung.asm.impl.parser.data.MethodData;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.Directive;

import static io.github.skylot.raung.common.Directive.CLASS;
import static io.github.skylot.raung.common.Directive.FIELD;
import static io.github.skylot.raung.common.Directive.IMPLEMENTS;
import static io.github.skylot.raung.common.Directive.METHOD;
import static io.github.skylot.raung.common.Directive.SIGNATURE;
import static io.github.skylot.raung.common.Directive.SOURCE;
import static io.github.skylot.raung.common.Directive.SUPER;
import static io.github.skylot.raung.common.Directive.VERSION;

public class ClassDirectives {

	private static final Map<Directive, IDirectivesProcessor<ClassData>> PROCESSOR_MAP;

	static {
		Map<Directive, IDirectivesProcessor<ClassData>> map = new EnumMap<>(Directive.class);
		map.put(VERSION, ClassDirectives::processVersion);
		map.put(CLASS, ClassDirectives::processClass);
		map.put(SUPER, ClassDirectives::processSuper);
		map.put(IMPLEMENTS, ClassDirectives::processInterface);
		map.put(SIGNATURE, ClassDirectives::processSignature);
		map.put(SOURCE, ClassDirectives::processSource);
		map.put(FIELD, ClassDirectives::processField);
		map.put(METHOD, ClassDirectives::processMethod);
		PROCESSOR_MAP = map;
	}

	public static void process(Directive token, RaungParser parser, ClassData cw) {
		IDirectivesProcessor<ClassData> processor = PROCESSOR_MAP.get(token);
		if (processor == null) {
			throw new RaungAsmException("Unexpected class directive", token.token());
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

	private static void processSignature(RaungParser parser, ClassData classData) {
		classData.setSignature(parser.readToken());
		parser.lineEnd();
	}

	private static void processSource(RaungParser parser, ClassData clsData) {
		clsData.setSource(parser.readString());
		parser.lineEnd();
	}

	private static void processField(RaungParser parser, ClassData classData) {
		FieldData field = FieldDirectives.parseField(parser);
		classData.getFields().add(field);
	}

	private static void processMethod(RaungParser parser, ClassData classData) {
		classData.visitCls();
		MethodData mth = MethodDirectives.parseMethod(classData, parser);
		classData.getMethods().add(mth);
	}
}
