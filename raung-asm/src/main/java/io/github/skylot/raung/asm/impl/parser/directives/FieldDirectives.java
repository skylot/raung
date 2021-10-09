package io.github.skylot.raung.asm.impl.parser.directives;

import java.util.EnumMap;
import java.util.Map;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.code.AnnotationParser;
import io.github.skylot.raung.asm.impl.parser.code.ValueParser;
import io.github.skylot.raung.asm.impl.parser.data.ClassData;
import io.github.skylot.raung.asm.impl.parser.data.FieldData;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.Directive;
import io.github.skylot.raung.common.RaungAccessFlags.Scope;

import static io.github.skylot.raung.common.Directive.ANNOTATION;
import static io.github.skylot.raung.common.Directive.END;
import static io.github.skylot.raung.common.Directive.SIGNATURE;
import static io.github.skylot.raung.common.Directive.TYPE_ANNOTATION;

public class FieldDirectives {

	private static final Map<Directive, IDirectivesProcessor<FieldData>> PROCESSOR_MAP;

	static {
		Map<Directive, IDirectivesProcessor<FieldData>> map = new EnumMap<>(Directive.class);
		map.put(SIGNATURE, FieldDirectives::processSignature);
		map.put(ANNOTATION, AnnotationParser::process);
		map.put(TYPE_ANNOTATION, AnnotationParser::processTypeAnnotation);
		PROCESSOR_MAP = map;
	}

	public static FieldData parseField(ClassData classData, RaungParser parser) {
		FieldData field = new FieldData(classData);
		field.setAccessFlags(parser.readAccessFlags(Scope.FIELD));
		field.setName(parser.readToken());
		field.setType(parser.readType());
		String assign = parser.tryGetToken();
		if (assign != null) {
			if (!assign.equals("=")) {
				throw new RaungAsmException("Unexpected token after field definition", assign);
			}
			Object value = ValueParser.process(parser.readToken());
			field.setValue(value);
			parser.lineEnd();
		}
		parseDirectives(parser, field);
		field.getAsmVisitor().visitEnd();
		return field;
	}

	private static void parseDirectives(RaungParser parser, FieldData field) {
		Directive nextToken = Directive.parseToken(parser.peekToken());
		if (nextToken == null || !nextToken.isAllowedInField()) {
			return;
		}
		while (true) {
			Directive directive = parser.readDirective();
			if (directive == null) {
				return;
			}
			if (directive == END) {
				parser.consumeToken("field");
				parser.lineEnd();
				return;
			}
			if (!directive.isAllowedInField()) {
				throw new RaungAsmException("Directive '" + directive.token() + "' not allowed in field scope");
			}
			process(directive, parser, field);
		}
	}

	private static void process(Directive token, RaungParser parser, FieldData cw) {
		IDirectivesProcessor<FieldData> processor = PROCESSOR_MAP.get(token);
		if (processor == null) {
			throw new RaungAsmException("Unexpected field directive", token.token());
		}
		processor.process(parser, cw);
	}

	private static void processSignature(RaungParser parser, FieldData field) {
		field.setSignature(parser.readToken());
		parser.lineEnd();
	}
}
