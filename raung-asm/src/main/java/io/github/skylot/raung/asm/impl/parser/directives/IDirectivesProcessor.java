package io.github.skylot.raung.asm.impl.parser.directives;

import io.github.skylot.raung.asm.impl.parser.RaungParser;

public interface IDirectivesProcessor<T> {
	void process(RaungParser parser, T writer);
}
