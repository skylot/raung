package io.github.skylot.raung.disasm.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public enum RaungDisasmPreset implements Consumer<IRaungDisasm> {
	DEFAULT(
			"default",
			"Balanced options for read and edit") {
		@Override
		public void accept(IRaungDisasm args) {
			args.saveCatchNumber(false);
			args.autoFrames(false);
			args.autoSwitch(false);
		}
	},
	REBUILD(
			"rebuild",
			"Save additional data for rebuild result stability") {
		@Override
		public void accept(IRaungDisasm args) {
			args.saveCatchNumber(true);
			args.autoFrames(false);
			args.autoSwitch(false);
		}
	},
	AUTO(
			"auto",
			"Omit information that can be calculated automatically (maxs, frames, switches)") {
		@Override
		public void accept(IRaungDisasm args) {
			args.saveCatchNumber(false);
			args.autoFrames(true);
			args.autoSwitch(true);
		}
	};

	private final String name;
	private final String description;

	RaungDisasmPreset(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	private static final Map<String, RaungDisasmPreset> MAP;

	static {
		RaungDisasmPreset[] values = values();
		Map<String, RaungDisasmPreset> map = new HashMap<>(values.length);
		for (RaungDisasmPreset preset : values) {
			map.put(preset.name, preset);
		}
		MAP = map;
	}

	public static RaungDisasmPreset getByName(String name) {
		RaungDisasmPreset preset = MAP.get(name);
		if (preset == null) {
			throw new IllegalArgumentException("Unknown preset name: "
					+ "'" + name + "', possible values:\n" + buildFormattedList());
		}
		return preset;
	}

	public static String buildFormattedList() {
		RaungDisasmPreset[] values = values();
		List<String> lines = new ArrayList<>(values.length);
		for (RaungDisasmPreset preset : values) {
			lines.add(String.format(" %s - %s", preset.name, preset.description));
		}
		return String.join("\n", lines);
	}
}
