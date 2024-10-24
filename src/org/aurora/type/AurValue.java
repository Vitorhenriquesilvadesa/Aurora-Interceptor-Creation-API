package org.aurora.type;

import java.util.Objects;

public class AurValue {
    public final Object value;
    public final byte type;

    public AurValue(Object value, byte type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        if (type == AurValueType.STRING) {
            String strValue = ((String) value);
            return strValue.substring(1, strValue.length() - 1);
        }

        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AurValue aurValue = (AurValue) o;
        return Objects.equals(value, aurValue.value) && type == aurValue.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }
}
