package org.aurora.compiler;

public final class AurInstructionCode {

    private AurInstructionCode() {
    }

    public static final byte RETURN = 0;
    public static final byte LOAD_CONST = 1;
    public static final byte ADD = 2;
    public static final byte SUB = 3;
    public static final byte DIV = 4;
    public static final byte MUL = 5;
    public static final byte NEGATE = 6;
    public static final byte INVERSE = 7;
    public static final byte GREATER = 8;
    public static final byte LESS = 9;
    public static final byte GREATER_EQUAL = 10;
    public static final byte LESS_EQUAL = 11;
    public static final byte EQUAL_EQUAL = 12;
    public static final byte MARK_EQUAL = 13;
    public static final byte AND = 14;
    public static final byte OR = 15;
    public static final byte PRINT = 16;
    public static final byte JUMP = 17;
    public static final byte JUMP_IF_FALSE = 18;
    public static final byte DEFINE = 19;
    public static final byte STORE = 20;
    public static final byte LOAD = 21;
    public static final byte LOOP = 22;
}
