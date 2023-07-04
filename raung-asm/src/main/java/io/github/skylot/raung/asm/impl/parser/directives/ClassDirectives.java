package io.github.skylot.raung.asm.impl.parser.directives;

import java.util.EnumMap;
import java.util.Map;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.code.AnnotationParser;
import io.github.skylot.raung.asm.impl.parser.data.AutoOption;
import io.github.skylot.raung.asm.impl.parser.data.ClassData;
import io.github.skylot.raung.asm.impl.parser.data.FieldData;
import io.github.skylot.raung.asm.impl.parser.data.MethodData;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.Directive;
import io.github.skylot.raung.common.RaungAccessFlags.Scope;
import io.github.skylot.raung.common.utils.JavaVersion;

import static io.github.skylot.raung.common.Directive.ANNOTATION;
import static io.github.skylot.raung.common.Directive.AUTO;
import static io.github.skylot.raung.common.Directive.CLASS;
import static io.github.skylot.raung.common.Directive.FIELD;
import static io.github.skylot.raung.common.Directive.IMPLEMENTS;
import static io.github.skylot.raung.common.Directive.INNERCLASS;
import static io.github.skylot.raung.common.Directive.METHOD;
import static io.github.skylot.raung.common.Directive.NEST;
import static io.github.skylot.raung.common.Directive.OUTERCLASS;
import static io.github.skylot.raung.common.Directive.SIGNATURE;
import static io.github.skylot.raung.common.Directive.SOURCE;
import static io.github.skylot.raung.common.Directive.SUPER;
import static io.github.skylot.raung.common.Directive.TYPE_ANNOTATION;
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
		map.put(INNERCLASS, ClassDirectives::processInnerClass);
		map.put(OUTERCLASS, ClassDirectives::processOuterClass);
		map.put(NEST, ClassDirectives::processNest);
		map.put(SOURCE, ClassDirectives::processSource);
		map.put(AUTO, ClassDirectives::processAuto);
		map.put(FIELD, ClassDirectives::processField);
		map.put(METHOD, ClassDirectives::processMethod);
		map.put(ANNOTATION, AnnotationParser::process);
		map.put(TYPE_ANNOTATION, AnnotationParser::processTypeAnnotation);
		PROCESSOR_MAP = map;
	}

	public static void process(Directive token, RaungParser parser, ClassData cw) {
		IDirectivesProcessor<ClassData> processor = PROCESSOR_MAP.get(token);
		if (processor == null) {
			throw new RaungAsmException("Unexpected class directive", token.token());
		}
		processor.process(parser, cw);
	}

	private static void processVersion(RaungParser parser, ClassData classData) {
		checkClassHeader(classData, VERSION);
		int clsVersion;
		String version = "";
		try {
			version = parser.readToken();
			int point = version.indexOf('.');
			if (point == -1) {
				clsVersion = Integer.parseInt(version);
			} else {
				int major = Integer.parseInt(version.substring(0, point));
				int minor = Integer.parseInt(version.substring(point + 1));
				clsVersion = JavaVersion.getRaw(major, minor);
			}
			classData.setVersion(clsVersion);
		} catch (Exception e) {
			throw new RaungAsmException("Failed to parse class version number: " + version, e);
		}
		parser.lineEnd();
	}

	private static void processClass(RaungParser parser, ClassData classData) {
		checkClassHeader(classData, CLASS);
		classData.setAccessFlags(parser.readAccessFlags(Scope.CLASS));
		classData.setName(parser.readToken());
		parser.lineEnd();
	}

	private static void processSuper(RaungParser parser, ClassData classData) {
		checkClassHeader(classData, SUPER);
		classData.setSuperCls(parser.readType());
		parser.lineEnd();
	}

	private static void processInterface(RaungParser parser, ClassData classData) {
		checkClassHeader(classData, INNERCLASS);
		classData.getInterfaces().add(parser.readType());
		parser.lineEnd();
	}

	private static void processSignature(RaungParser parser, ClassData classData) {
		checkClassHeader(classData, SIGNATURE);
		classData.setSignature(parser.readToken());
		parser.lineEnd();
	}

	private static void processSource(RaungParser parser, ClassData classData) {
		checkClassHeader(classData, SOURCE);
		classData.setSource(parser.readString());
		parser.lineEnd();
	}

	private static void processInnerClass(RaungParser parser, ClassData classData) {
		int accessFlags = parser.readAccessFlags(Scope.CLASS);
		String inner = parser.readToken();
		String outer = parser.readType();
		String name = parser.tryGetToken();
		if (name != null) {
			parser.lineEnd();
		} else {
			name = classData.getName();
		}
		classData.visitCls().visitInnerClass(name, outer, inner, accessFlags);
	}

	private static void processOuterClass(RaungParser parser, ClassData classData) {
		String owner = parser.readType();
		String name = parser.readToken();
		String descriptor = parser.readToken();
		classData.visitCls().visitOuterClass(owner, name, descriptor);
	}

	private static void processAuto(RaungParser parser, ClassData classData) {
		checkClassHeader(classData, AUTO);
		String token = parser.readToken();
		switch (token) {
			case "disable":
				classData.setAuto(AutoOption.DISABLE);
				break;
			case "maxs":
				classData.setAuto(AutoOption.MAXS);
				break;
			case "frames":
				classData.setAuto(AutoOption.FRAMES);
				break;

			default:
				throw new RaungAsmException("Unknown auto type: " + token + ", should be one of: 'disable', 'maxs' or 'frames'");
		}
	}

	private static void checkClassHeader(ClassData classData, Directive directive) {
		if (classData.isVisited()) {
			throw new RaungAsmException(directive.token() + " directive should be placed before class body (i.e fields or methods)");
		}
	}

	private static void processNest(RaungParser parser, ClassData classData) {
		String ref = parser.readToken();
		switch (ref) {
			case "host":
				classData.visitCls().visitNestHost(parser.readType());
				break;
			case "member":
				classData.visitCls().visitNestMember(parser.readType());
				break;
			default:
				throw new RaungAsmException("Unknown nest ref type: '" + ref + "', expected 'host' or 'member'");
		}
	}

	private static void processField(RaungParser parser, ClassData classData) {
		FieldData field = FieldDirectives.parseField(classData, parser);
		classData.getFields().add(field);
	}

	private static void processMethod(RaungParser parser, ClassData classData) {
		classData.visitCls();
		MethodData mth = MethodDirectives.parseMethod(classData, parser);
		classData.getMethods().add(mth);
	}
}
