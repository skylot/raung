package io.github.skylot.raung.asm.tests.api;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import io.github.skylot.raung.asm.RaungAsm;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {

	protected String asmCls() {
		String filePath = getTestPkg() + "/" + getTestName() + ".raung";
		String resPath = "/raung/" + filePath;
		InputStream stream = this.getClass().getResourceAsStream(resPath);
		assertThat(stream).describedAs("Raung file not found: %s", resPath).isNotNull();
		byte[] bytes = RaungAsm.create().executeForInputStream(stream);
		String result = parseWithASM(bytes);
		printCode(result);
		return result;
	}

	public String getTestName() {
		return this.getClass().getSimpleName();
	}

	public String getTestPkg() {
		return this.getClass().getPackage().getName()
				.replace("io.github.skylot.raung.asm.tests.integration.", "");
	}

	private static String parseWithASM(byte[] bytes) {
		StringWriter out = new StringWriter();
		TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(out));
		new ClassReader(bytes).accept(tcv, 0);
		return out.toString();
	}

	private void printCode(String code) {
		System.out.println("===================================");
		System.out.println(code);
		System.out.println("===================================");
	}
}
