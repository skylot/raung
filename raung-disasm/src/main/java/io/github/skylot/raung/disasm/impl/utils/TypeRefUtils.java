package io.github.skylot.raung.disasm.impl.utils;

import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.skylot.raung.common.asm.TypeRef;

public class TypeRefUtils {
	private static final Logger LOG = LoggerFactory.getLogger(TypeRefUtils.class);

	public static String formatPath(int typeRef, TypePath typePath) {
		TypeReference typeReference = new TypeReference(typeRef);
		int sort = typeReference.getSort();
		TypeRef raungTypeRef = TypeRef.getByValue(sort);
		if (raungTypeRef == null) {
			String sortStr = rawSortFormat(typeReference, sort);
			LOG.warn("TODO: Missing type reference info: {}", sortStr);
			return sortStr;
		}
		StringBuilder sb = new StringBuilder(raungTypeRef.getName());
		appendArgs(raungTypeRef, typeReference, sb);
		if (typePath != null) {
			sb.append(' ').append(typePath);
		}
		return sb.toString();
	}

	private static void appendArgs(TypeRef refType, TypeReference ref, StringBuilder sb) {
		switch (refType.getFormat()) {
			case NO_ARGS:
				break;
			case TYPE_PARAM_INDEX:
				sb.append(' ').append(ref.getTypeParameterIndex());
				break;
			case TYPE_PARAM_BOUND_INDEX:
				sb.append(' ').append(ref.getTypeParameterIndex()).append(' ').append(ref.getTypeParameterBoundIndex());
				break;
			case SUPER_TYPE_INDEX:
				sb.append(' ').append(ref.getSuperTypeIndex());
				break;
			case FORMAL_PARAM_INDEX:
				sb.append(' ').append(ref.getFormalParameterIndex());
				break;
			case EXCEPTION_INDEX:
				sb.append(' ').append(ref.getExceptionIndex());
				break;
			case TRY_CATCH_BLOCK_INDEX:
				sb.append(' ').append(ref.getTryCatchBlockIndex());
				break;
			case TYPE_ARGUMENT_INDEX:
				sb.append(' ').append(ref.getTypeArgumentIndex());
				break;
			default:
				throw new RaungDisasmException("Unsupported type reference sort: " + refType);
		}
	}

	@Deprecated
	private static String rawSortFormat(TypeReference typeReference, int sort) {
		switch (sort) {
			case TypeReference.CLASS_TYPE_PARAMETER:
				return "CLASS_TYPE_PARAMETER " + typeReference.getTypeParameterIndex();
			case TypeReference.METHOD_TYPE_PARAMETER:
				return "METHOD_TYPE_PARAMETER " + typeReference.getTypeParameterIndex();
			case TypeReference.CLASS_EXTENDS:
				return "CLASS_EXTENDS " + typeReference.getSuperTypeIndex();
			case TypeReference.CLASS_TYPE_PARAMETER_BOUND:
				return "CLASS_TYPE_PARAMETER_BOUND " + typeReference.getTypeParameterIndex()
						+ " " + typeReference.getTypeParameterBoundIndex();
			case TypeReference.METHOD_TYPE_PARAMETER_BOUND:
				return "METHOD_TYPE_PARAMETER_BOUND " + typeReference.getTypeParameterIndex()
						+ " " + typeReference.getTypeParameterBoundIndex();
			case TypeReference.FIELD:
				return "FIELD";
			case TypeReference.METHOD_RETURN:
				return "METHOD_RETURN";
			case TypeReference.METHOD_RECEIVER:
				return "METHOD_RECEIVER";
			case TypeReference.METHOD_FORMAL_PARAMETER:
				return "METHOD_FORMAL_PARAMETER " + typeReference.getFormalParameterIndex();
			case TypeReference.THROWS:
				return "THROWS " + typeReference.getExceptionIndex();
			case TypeReference.LOCAL_VARIABLE:
				return "LOCAL_VARIABLE";
			case TypeReference.RESOURCE_VARIABLE:
				return "RESOURCE_VARIABLE";
			case TypeReference.EXCEPTION_PARAMETER:
				return "EXCEPTION_PARAMETER " + typeReference.getTryCatchBlockIndex();
			case TypeReference.INSTANCEOF:
				return "INSTANCEOF";
			case TypeReference.NEW:
				return "NEW";
			case TypeReference.CONSTRUCTOR_REFERENCE:
				return "CONSTRUCTOR_REFERENCE";
			case TypeReference.METHOD_REFERENCE:
				return "METHOD_REFERENCE";
			case TypeReference.CAST:
				return "CAST " + typeReference.getTypeArgumentIndex();
			case TypeReference.CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
				return "CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT " + typeReference.getTypeArgumentIndex();
			case TypeReference.METHOD_INVOCATION_TYPE_ARGUMENT:
				return "METHOD_INVOCATION_TYPE_ARGUMENT " + typeReference.getTypeArgumentIndex();
			case TypeReference.CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
				return "CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT " + typeReference.getTypeArgumentIndex();
			case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT:
				return "METHOD_REFERENCE_TYPE_ARGUMENT " + typeReference.getTypeArgumentIndex();
			default:
				throw new RaungDisasmException("Unexpected type reference sort: " + sort);
		}
	}
}
