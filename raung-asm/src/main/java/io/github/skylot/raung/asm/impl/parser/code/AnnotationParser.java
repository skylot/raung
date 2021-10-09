package io.github.skylot.raung.asm.impl.parser.code;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.data.CommonData;
import io.github.skylot.raung.asm.impl.parser.data.MethodData;
import io.github.skylot.raung.asm.impl.parser.data.TypeRefPathData;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;
import io.github.skylot.raung.common.AnnotationType;
import io.github.skylot.raung.common.asm.TypeRef;

import static io.github.skylot.raung.common.Directive.END;

public class AnnotationParser {

	public static void process(RaungParser parser, CommonData data) {
		boolean visible = parseVisibility(parser);
		String type = parser.readType();
		AnnotationVisitor av = data.visitAnnotation(type, visible);
		parse(av, parser, AnnotationType.NORMAL);
	}

	public static void processParamAnnotation(RaungParser parser, MethodData methodData) {
		int parameter = parser.readInt();
		boolean visible = parseVisibility(parser);
		String type = parser.readType();
		AnnotationVisitor av = methodData.getAsmMethodVisitor().visitParameterAnnotation(parameter, type, visible);
		parse(av, parser, AnnotationType.PARAM);
	}

	public static void processTypeAnnotation(RaungParser parser, CommonData data) {
		boolean visible = parseVisibility(parser);
		String type = parser.readType();
		parser.lineEnd();
		TypeRefPathData pathData = parseRef(parser);
		int typeRefValue = pathData.getTypeRef().getValue();
		AnnotationVisitor av = data.visitTypeAnnotation(typeRefValue, pathData.getPath(), type, visible);
		parse(av, parser, AnnotationType.TYPE);
	}

	public static void processAnnotationDefaultValue(RaungParser parser, MethodData methodData) {
		parser.lineEnd();
		AnnotationVisitor av = methodData.getAsmMethodVisitor().visitAnnotationDefault();
		// read only one value
		parseValue(av, parser, null);
		av.visitEnd();
		parser.lineEnd();
		parser.consumeToken(".end");
		parser.consumeToken(AnnotationType.DEFAULT.getName());
		parser.lineEnd();
	}

	private static boolean parseVisibility(RaungParser parser) {
		String visType = parser.readToken();
		switch (visType) {
			case "runtime":
				return true;
			case "build":
				return false;
			default:
				throw new RaungAsmException("Unknown annotation type: " + visType + ". Should be 'build' or 'runtime'");
		}
	}

	private static void parse(AnnotationVisitor av, RaungParser parser, AnnotationType type) {
		while (true) {
			String nameToken = parser.skipToToken();
			if (nameToken == null) {
				throw new RaungAsmException("Unexpected end of file");
			}
			if (nameToken.equals(END.token())) {
				parser.consumeToken(type.getName());
				av.visitEnd();
				parser.lineEnd();
				return;
			}
			if (nameToken.startsWith(".")) {
				processDirective(av, parser, nameToken);
			} else {
				parser.consumeToken("=");
				parseValue(av, parser, nameToken);
			}
			parser.lineEnd();
		}
	}

	private static void parseValue(AnnotationVisitor av, RaungParser parser, @Nullable String nameToken) {
		String valueToken = parser.readToken();
		if (valueToken.startsWith(".")) {
			processSubValue(av, parser, nameToken, valueToken);
		} else {
			av.visit(nameToken, ValueParser.process(valueToken));
		}
	}

	private static void processSubValue(AnnotationVisitor av, RaungParser parser, @Nullable String name, String valueToken) {
		switch (valueToken) {
			case ".enum":
				String owner = parser.readType();
				String value = parser.readToken();
				av.visitEnum(name, owner, value);
				break;

			case ".array":
				parser.lineEnd();
				AnnotationVisitor arrayVisitor = av.visitArray(name);
				parseSubArray(arrayVisitor, parser);
				arrayVisitor.visitEnd();
				break;

			default:
				throw new RaungAsmException("Unknown annotation sub value directive", valueToken);
		}
	}

	private static void processDirective(AnnotationVisitor av, RaungParser parser, String nameToken) {
		switch (nameToken) {

			default:
				throw new RaungAsmException("Unknown annotation directive", nameToken);
		}
	}

	private static TypeRefPathData parseRef(RaungParser parser) {
		parser.consumeToken(".ref");
		String refType = parser.readToken();
		TypeRef ref = TypeRef.getByName(refType);
		if (ref == null) {
			throw new RaungAsmException("Unknown type reference sort", refType);
		}
		int sort = ref.getValue();
		TypeRefPathData path = new TypeRefPathData();
		parseTypeReference(parser, ref, sort, path);
		String token = parser.tryGetToken();
		if (token != null) {
			path.setPath(TypePath.fromString(token));
			parser.lineEnd();
		}
		return path;
	}

	private static void parseTypeReference(RaungParser parser, TypeRef ref, int sort, TypeRefPathData path) {
		switch (ref.getFormat()) {
			case NO_ARGS:
				path.setTypeRef(TypeReference.newTypeReference(sort));
				break;
			case TYPE_PARAM_INDEX:
				path.setTypeRef(TypeReference.newTypeParameterReference(sort, parser.readInt()));
				break;
			case TYPE_PARAM_BOUND_INDEX:
				path.setTypeRef(TypeReference.newTypeParameterBoundReference(sort, parser.readInt(), parser.readInt()));
				break;
			case SUPER_TYPE_INDEX:
				path.setTypeRef(TypeReference.newSuperTypeReference(parser.readInt()));
				break;
			case FORMAL_PARAM_INDEX:
				path.setTypeRef(TypeReference.newFormalParameterReference(parser.readInt()));
				break;
			case EXCEPTION_INDEX:
				path.setTypeRef(TypeReference.newExceptionReference(parser.readInt()));
				break;
			case TRY_CATCH_BLOCK_INDEX:
				path.setTypeRef(TypeReference.newTryCatchReference(parser.readInt()));
				break;
			case TYPE_ARGUMENT_INDEX:
				path.setTypeRef(TypeReference.newTypeArgumentReference(sort, parser.readInt()));
				break;
		}
	}

	private static void parseSubArray(AnnotationVisitor av, RaungParser parser) {
		while (true) {
			String token = parser.readToken();
			if (token.startsWith(".")) {
				if (token.equals(END.token())) {
					parser.consumeToken("array");
					return;
				}
				processSubValue(av, parser, null, token);
			} else {
				av.visit(null, ValueParser.process(token));
			}
			parser.lineEnd();
		}
	}
}
