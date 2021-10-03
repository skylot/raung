package io.github.skylot.raung.asm.impl.parser.directives;

import java.util.EnumMap;
import java.util.Map;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.code.ValueParser;
import io.github.skylot.raung.asm.impl.parser.data.AnnotationData;
import io.github.skylot.raung.asm.impl.parser.data.FieldData;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.Directive;
import io.github.skylot.raung.common.RaungAccessFlags.Scope;

import static io.github.skylot.raung.common.Directive.ANNOTATION;
import static io.github.skylot.raung.common.Directive.END;
import static io.github.skylot.raung.common.Directive.SIGNATURE;

public class FieldDirectives {

	private static final Map<Directive, IDirectivesProcessor<FieldData>> PROCESSOR_MAP;

	static {
		PROCESSOR_MAP = new EnumMap<>(Directive.class);
		PROCESSOR_MAP.put(SIGNATURE, FieldDirectives::processSignature);
		PROCESSOR_MAP.put(ANNOTATION, FieldDirectives::processAnnotation);
	}

	public static FieldData parseField(RaungParser parser) {
		FieldData field = new FieldData();
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
		Directive nextToken = Directive.parseToken(parser.peekToken());
		if (nextToken == null || !nextToken.isAllowedInField()) {
			return field;
		}
		while (true) {
			Directive directive = parser.readDirective();
			if (directive == null) {
				return field;
			}
			if (directive == END) {
				parser.consumeToken("field");
				parser.lineEnd();
				return field;
			}
			if (!directive.isAllowedInField()) {
				throw new RaungAsmException(String.format(
						"Directive '%s' not allowed in field scope: %s",
						directive.token(), parser.formatPosition()));
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

	private static void processAnnotation(RaungParser parser, FieldData fieldData) {
		AnnotationData ann = new AnnotationData();
		fieldData.getAnnotations().add(ann);
		ann.setVisible(parser.readToken().equals("runtime"));
		ann.setType(parser.readToken());
		parser.lineEnd();
		while (true) {
			String nameToken = parser.readToken();
			if (nameToken.equals(END.token())) {
				parser.consumeToken("annotation");
				parser.lineEnd();
				return;
			}
			parser.consumeToken("=");
			String valueToken = parser.readToken();
			parser.lineEnd();
			Object value = ValueParser.process(valueToken);
			ann.getValues().put(nameToken, value);
		}
	}
}
