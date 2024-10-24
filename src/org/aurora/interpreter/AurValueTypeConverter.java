package org.aurora.interpreter;

import org.aurora.type.AurValueType;

import java.util.HashMap;
import java.util.Map;

public final class AurValueTypeConverter {

    private static final Map<Byte, String> valueTypeToString;

    static {
        valueTypeToString = new HashMap<>();
        valueTypeToString.put(AurValueType.INT, "int");
        valueTypeToString.put(AurValueType.FLOAT, "float");
        valueTypeToString.put(AurValueType.CHAR, "char");
        valueTypeToString.put(AurValueType.STRING, "string");
        valueTypeToString.put(AurValueType.BOOL, "bool");
        valueTypeToString.put(AurValueType.NULL, "null");
    }

    private AurValueTypeConverter() {
    }

    public static String valueTypeToString(Byte type) {
        return valueTypeToString.get(type);
    }
}
