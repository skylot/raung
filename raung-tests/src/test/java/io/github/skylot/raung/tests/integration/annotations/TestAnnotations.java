package io.github.skylot.raung.tests.integration.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import io.github.skylot.raung.tests.api.IntegrationTest;

public class TestAnnotations extends IntegrationTest {

	public static class TestCls {
		@Nullable
		@A(bool = false)
		private static Field field;

		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.FIELD, ElementType.METHOD })
		public @interface A {
			boolean bool();
		}

		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.TYPE_PARAMETER, ElementType.PARAMETER, ElementType.METHOD, ElementType.LOCAL_VARIABLE })
		public @interface B {
			int i() default 7;
		}

		public static class C<K, @B(i = 2) V> {
		}

		@SuppressWarnings("ModifierOrder")
		public @B void test1(@B(i = 0) int i, @Nullable String s) {
		}

		public <V> void test2() {
			@SuppressWarnings("unchecked")
			// @Nullable // TODO: support local var annotations
			V[][] copy = (@Nullable V[][]) new Object[1][2];
			System.out.println(Arrays.deepToString(copy));
		}
	}

	@Test
	public void test() {
		runChecksWithInnerClasses(TestCls.class);
	}
}
