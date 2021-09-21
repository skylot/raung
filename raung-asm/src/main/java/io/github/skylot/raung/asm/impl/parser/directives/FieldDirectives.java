package io.github.skylot.raung.asm.impl.parser.directives;

import java.util.EnumMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.data.AnnotationData;
import io.github.skylot.raung.asm.impl.parser.data.FieldData;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.DirectiveToken;

import static io.github.skylot.raung.common.DirectiveToken.ANNOTATION;
import static io.github.skylot.raung.common.DirectiveToken.END;
import static io.github.skylot.raung.common.DirectiveToken.SIGNATURE;

public class FieldDirectives {

	private static final Map<DirectiveToken, IDirectivesProcessor<FieldData>> PROCESSOR_MAP;

	static {
		PROCESSOR_MAP = new EnumMap<>(DirectiveToken.class);
		PROCESSOR_MAP.put(SIGNATURE, FieldDirectives::processSignature);
		PROCESSOR_MAP.put(ANNOTATION, FieldDirectives::processAnnotation);
	}

	public static void process(DirectiveToken token, RaungParser parser, FieldData cw) {
		IDirectivesProcessor<FieldData> processor = PROCESSOR_MAP.get(token);
		if (processor == null) {
			throw new RaungAsmException("Unexpected field directive: " + token.token());
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
			Object value = parseValue(valueToken);
			ann.getValues().put(nameToken, value);
		}
	}

	@NotNull
	private static Object parseValue(String valueToken) {
		Object value;
		char firstChar = valueToken.charAt(0);
		if (firstChar == '"') {
			// String
			value = valueToken.substring(1, valueToken.length() - 1);
		} else if (firstChar == '-' || Character.isDigit(firstChar)) {
			if (valueToken.contains(".")) {
				value = Double.parseDouble(valueToken);
			} else {
				value = Integer.parseInt(valueToken);
			}
		} else {
			// TODO: parse other types (add type descriptors?)
			value = valueToken;
		}
		return value;
	}
}
