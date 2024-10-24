package org.aurora.scanner;

import org.aurora.type.AurValue;

public record Token(TokenType type, String lexeme, AurValue literal, int line) {

    @Override
    public String toString() {
        return "<" + type.toString() + ", '" + lexeme + "'>";
    }
}
