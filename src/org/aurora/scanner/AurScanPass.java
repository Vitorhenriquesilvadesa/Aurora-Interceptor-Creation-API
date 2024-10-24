package org.aurora.scanner;

import org.aurora.type.AurValue;
import org.aurora.pass.AurCompilationPass;
import org.aurora.type.AurValueType;
import org.aurora.util.AurFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.aurora.scanner.TokenType.*;

public class AurScanPass extends AurCompilationPass<AurFile, AurScannedData> {

    private int line;
    private List<Token> tokens;
    private String source;
    private int start;
    private int current;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("public", PUBLIC);
        keywords.put("locked", LOCKED);
        keywords.put("if", IF);
        keywords.put("else", ELSE);
        keywords.put("true", TRUE);
        keywords.put("false", FALSE);
        keywords.put("print", PRINT);
        keywords.put("for", FOR);
        keywords.put("while", WHILE);
    }

    @Override
    public Class<AurFile> getInputType() {
        return AurFile.class;
    }

    @Override
    public Class<AurScannedData> getOutputType() {
        return AurScannedData.class;
    }

    @Override
    public String getDebugName() {
        return "Scan Pass";
    }

    @Override
    protected AurScannedData pass(AurFile input) {
        return scanTokens(input);
    }

    private AurScannedData scanTokens(AurFile input) {
        resetInternalState(input);

        while (!isAtEnd()) {
            syncCursors();
            scanToken();
        }

        makeToken(EOF, "EOF", null);

        return new AurScannedData(tokens);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();

        switch (c) {

            case '+':
                makeToken(PLUS);
                break;

            case '-':
                makeToken(MINUS);
                break;

            case '*':
                makeToken(STAR);
                break;

            case '/':
                makeToken(SLASH);
                break;

            case '!':
                if (match('=')) {
                    makeToken(MARK_EQUAL);
                } else {
                    makeToken(MARK);
                }
                break;

            case '&':
                if (match('&')) {
                    makeToken(AND);
                } else {
                    makeToken(BITWISE_AND);
                }
                break;

            case '|':
                if (match('|')) {
                    makeToken(OR);
                } else {
                    makeToken(BITWISE_OR);
                }
                break;

            case '=':
                if (match('=')) {
                    makeToken(EQUAL_EQUAL);
                } else {
                    makeToken(EQUAL);
                }
                break;

            case '>':
                if (match('=')) {
                    makeToken(GREATER_EQUAL);
                } else {
                    makeToken(GREATER);
                }
                break;

            case '<':
                if (match('=')) {
                    makeToken(LESS_EQUAL);
                } else {
                    makeToken(LESS);
                }
                break;

            case '(':
                makeToken(LEFT_PAREN);
                break;

            case ')':
                makeToken(RIGHT_PAREN);
                break;

            case '{':
                makeToken(LEFT_BRACE);
                break;

            case '}':
                makeToken(RIGHT_BRACE);
                break;

            case ';':
                makeToken(SEMICOLON);
                break;

            case ' ':
            case '\t':
            case '\r':
                break;

            case '\n':
                line++;
                break;

            case '"':
                string();
                break;

            case '\'':
                character();
                break;

            default:

                if (isDigit(c)) {
                    number();
                    break;
                }

                if (isAlpha(c)) {
                    identifier();
                    break;
                }
        }
    }

    private void character() {
        char c = peek();
        advance();
        advance();
        makeToken(CHAR, "" + c, new AurValue(c, AurValueType.CHAR));
    }

    private void string() {
        while (!match('"')) {
            advance();
        }

        String text = source.substring(start, current);
        makeToken(STRING, text, new AurValue(text, AurValueType.STRING));
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, IDENTIFIER);

        if (type != IDENTIFIER) {
            if (type != TRUE && type != FALSE) {
                makeToken(type, null);
            } else {
                makeToken(type, new AurValue(Boolean.parseBoolean(text), AurValueType.BOOL));
            }
        } else {
            makeToken(type, null);
        }
    }

    private void number() {
        while (isDigit(peek()) || peek() == '_') advance();
        if (peek() == '.' && isDigit(peekNext())) {
            do {
                advance();
            } while (isDigit(peek()));

            AurValue value = new AurValue(
                    Float.parseFloat(source.substring(start, current).replaceAll("_", "")),
                    AurValueType.FLOAT);

            makeToken(FLOAT, value);

        } else {
            AurValue value = new AurValue(
                    Integer.parseInt(source.substring(start, current).replaceAll("_", "")),
                    AurValueType.INT);

            makeToken(INT, value);
        }
    }

    private char peekNext() {
        return source.charAt(current + 1);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean match(char c) {
        if (isAtEnd()) return false;
        if (peek() != c) return false;
        advance();
        return true;
    }

    private char peek() {
        return source.charAt(current);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void syncCursors() {
        start = current;
    }

    private void resetInternalState(AurFile file) {
        start = 0;
        current = 0;
        line = 1;
        tokens = new ArrayList<>();
        source = file.getSource();
    }

    private void makeToken(TokenType type) {
        makeToken(type, null);
    }

    private void makeToken(TokenType type, AurValue literal) {
        String lexeme = source.substring(start, current);
        makeToken(type, lexeme, literal);
    }

    private void makeToken(TokenType type, String lexeme, AurValue literal) {
        Token token = new Token(type, lexeme, literal, line);
        tokens.add(token);
    }
}
