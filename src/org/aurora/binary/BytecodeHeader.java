package org.aurora.binary;

public record BytecodeHeader(String version, int bytecodeSize, String hash) {
}
