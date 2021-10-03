package io.github.skylot.raung.asm.impl.parser.code;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import io.github.skylot.raung.asm.impl.parser.RaungParser;
import io.github.skylot.raung.asm.impl.parser.data.MethodData;
import io.github.skylot.raung.asm.impl.parser.data.RaungLabel;
import io.github.skylot.raung.asm.impl.utils.RaungAsmException;

public class SwitchParser {

	public static void parse(MethodData mth, MethodVisitor mv, RaungParser parser, String token) {
		parser.lineEnd();
		Label defLabel = null;
		List<Integer> keys = new ArrayList<>();
		List<RaungLabel> labels = new ArrayList<>();
		while (true) {
			String key = parser.skipToToken();
			if (key == null) {
				throw new RaungAsmException("Missing switch end");
			}
			if (key.equals(".end")) {
				parser.consumeToken(token);
				break;
			}
			if (key.equals("default")) {
				defLabel = RaungLabel.ref(mth, parser.readToken());
			} else {
				keys.add(Integer.parseInt(key));
				labels.add(RaungLabel.ref(mth, parser.readToken()));
			}
		}
		if (defLabel == null) {
			throw new RaungAsmException("'default' case is required in switch");
		}
		int[] keysArr = toIntArray(keys);
		check(keysArr);
		Label[] labelsArr = labels.toArray(new Label[0]);

		boolean canUseTableSwitch = isTableSwitch(keysArr);
		boolean isTableSwitch = token.equals("tableswitch");
		if (isTableSwitch && !canUseTableSwitch) {
			throw new RaungAsmException("Can't use tableswitch, keys are not sequential");
		}
		boolean autoSwitch = token.equals("switch");
		if (isTableSwitch || (autoSwitch && canUseTableSwitch)) {
			int min = keysArr[0];
			int max = keysArr[keysArr.length - 1];
			mv.visitTableSwitchInsn(min, max, defLabel, labelsArr);
		} else {
			mv.visitLookupSwitchInsn(defLabel, keysArr, labelsArr);
		}
	}

	private static boolean isTableSwitch(int[] keys) {
		int last = keys.length - 1;
		for (int i = 0; i < last; i++) {
			int a = keys[i];
			int b = keys[i + 1];
			if (a + 1 != b) {
				return false;
			}
		}
		return true;
	}

	private static int[] toIntArray(List<Integer> list) {
		int size = list.size();
		int[] arr = new int[size];
		for (int i = 0; i < size; i++) {
			arr[i] = list.get(i);
		}
		return arr;
	}

	private static void check(int[] keys) {
		int last = keys.length - 1;
		for (int i = 0; i < last; i++) {
			int a = keys[i];
			int b = keys[i + 1];
			if (a == b) {
				throw new RaungAsmException("Switch keys should be different: " + a);
			}
			if (a > b) {
				throw new RaungAsmException("Switch keys should be sorted: " + a + " occur before " + b);
			}
		}
	}
}
