package io.github.skylot.raung.disasm.impl.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public class ListUtils {

	public static <T> List<T> fromNullable(@Nullable List<T> list) {
		return list == null ? Collections.emptyList() : list;
	}

	public static <T> List<T> addToNullable(@Nullable List<T> list, T obj) {
		List<T> result = list == null ? new ArrayList<>() : list;
		result.add(obj);
		return result;
	}
}
