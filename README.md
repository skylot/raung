# Raung

[![Build](https://github.com/skylot/raung/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/skylot/raung/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.skylot/raung-asm?style=flat)](https://search.maven.org/search?q=raung)
[![GitHub](https://img.shields.io/github/license/skylot/raung)](https://github.com/skylot/raung/blob/main/LICENSE)

**raung** - yet another assembler/disassembler for java bytecode.

Similar to [Krakatau](https://github.com/Storyyeller/Krakatau) and [Smali](https://github.com/JesusFreke/smali),
based on [ASM](https://asm.ow2.io/) library.

:exclamation: Warning: this project at a very early stage of development, many features not yet finished and syntax not stable.


### Syntax example:
```
.version 52
.class public HelloWorld
.source "HelloWorld.java"

.method public static main([Ljava/lang/String;)V
	.max stack 2
	.max locals 1

	.local 0 "args" [Ljava/lang/String;
	.line 4
	getstatic java/lang/System out Ljava/io/PrintStream;
	ldc "Hello, World!"
	invokevirtual java/io/PrintStream println (Ljava/lang/String;)V
	.line 5
	return
.end method
```

---------------------------------------
*Licensed under the MIT License*
