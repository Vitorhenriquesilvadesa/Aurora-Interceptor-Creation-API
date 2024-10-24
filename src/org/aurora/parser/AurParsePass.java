package org.aurora.parser;

import org.aurora.exception.AurParseException;
import org.aurora.parser.expression.*;
import org.aurora.parser.statement.*;
import org.aurora.pass.AurCompilationPass;
import org.aurora.scanner.AurScannedData;
import org.aurora.scanner.Token;
import org.aurora.scanner.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.aurora.scanner.TokenType.*;

public class AurParsePass extends AurCompilationPass<AurScannedData, AurParsedData> {


    private List<Token> tokens;
    private int current;

    @Override
    public Class<AurScannedData> getInputType() {
        return AurScannedData.class;
    }

    @Override
    public Class<AurParsedData> getOutputType() {
        return AurParsedData.class;
    }

    @Override
    public String getDebugName() {
        return "Parse Pass";
    }

    @Override
    protected AurParsedData pass(AurScannedData input) {
        return parseTokens(input);
    }

    private void resetInternalState(AurScannedData input) {
        tokens = input.getTokens();
        current = 0;
    }

    private AurParsedData parseTokens(AurScannedData input) {
        resetInternalState(input);

        List<AurStatementNode> statements = new ArrayList<>();

        while (!isAtEnd()) {
            AurStatementNode statementNode = declaration();
            statements.add(statementNode);
        }

        return new AurParsedData(statements);
    }

    private AurStatementNode declaration() {

        if (match(IDENTIFIER)) return variableDeclaration();

        return statement();
    }

    private AurStatementNode variableDeclaration() {
        if (check(IDENTIFIER)) {
            Token type = previous();
            consume(IDENTIFIER, "Expect variable name after type.");
            Token name = previous();
            consume(EQUAL, "Expect '=' after variable name, but got '" + peek().lexeme() + "'.");
            AurExpressionNode expression = expression();
            consume(SEMICOLON, "Expect ';' after initializer.");

            return new VariableDeclarationStatement(type, name, expression);
        }

        backTrack();
        return expressionStatement();
    }

    private void backTrack() {
        current--;
    }

    private AurStatementNode statement() {
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        if (match(FOR)) return forStatement();
        if (match(LEFT_BRACE)) return bodyStatement();
        if (match(PRINT)) return printStatement();

        return expressionStatement();
    }

    private AurStatementNode forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        AurStatementNode initializer = null;

        if (!match(SEMICOLON)) {
            if (match(IDENTIFIER)) {
                if (match(IDENTIFIER)) {
                    backTrack();
                    initializer = variableDeclaration();
                } else {
                    initializer = expressionStatement();
                }
            }
        }

        AurExpressionNode condition = null;

        if (!check(SEMICOLON)) {
            condition = expression();
        }

        consume(SEMICOLON, "Expect ')' after loop condition.");

        AurExpressionNode increment = null;

        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }

        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        AurStatementNode body = statement();

        if (increment != null) {
            body = new AurBodyStatement(Arrays.asList(body, new AurExpressionStatement(increment)));
        }

        if (condition == null) {
            condition = new AurLiteralExpression(new Token(TRUE, "true", null, 0));
        }

        body = new AurWhileStatement(condition, body);

        if (initializer != null) {
            body = new AurBodyStatement(Arrays.asList(initializer, body));
        }

        return body;
    }

    private AurStatementNode whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        AurExpressionNode condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");

        AurStatementNode body = statement();

        return new AurWhileStatement(condition, body);
    }

    private AurStatementNode printStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'print'.");
        AurExpressionNode expression = expression();
        consume(RIGHT_PAREN, "Expect ')' after argument.");
        consume(SEMICOLON, "Expect ';' after call.");
        return new PrintStatement(expression);
    }

    private AurStatementNode bodyStatement() {
        List<AurStatementNode> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE)) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after body.");

        return new AurBodyStatement(statements);
    }

    private AurStatementNode expressionStatement() {
        AurExpressionNode expression = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new AurExpressionStatement(expression);
    }

    private AurStatementNode ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        AurExpressionNode condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after 'if' condition.");
        AurStatementNode thenBranch = statement();
        AurStatementNode elseBranch = null;

        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new AurIfStatement(condition, thenBranch, elseBranch);
    }

    private AurExpressionNode expression() {
        return assignment();
    }

    private AurExpressionNode assignment() {
        AurExpressionNode expression = or();

        if (match(EQUAL)) {
            Token equals = previous();
            AurExpressionNode value = assignment();

            if (expression instanceof AurVariableGetExpression) {
                Token name = ((AurVariableGetExpression) expression).name;
                return new AurAssignmentExpression(name, equals, value);
            }

            error("Invalid assignment target.");
        }

        return expression;
    }

    private AurExpressionNode or() {
        AurExpressionNode expressionNode = and();

        while (match(OR)) {
            Token operator = previous();
            AurExpressionNode right = and();
            expressionNode = new AurLogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private AurExpressionNode and() {
        AurExpressionNode expressionNode = equality();

        while (match(AND)) {
            Token operator = previous();
            AurExpressionNode right = equality();
            expressionNode = new AurLogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private AurExpressionNode equality() {

        AurExpressionNode expressionNode = comparison();

        while (match(EQUAL_EQUAL, MARK_EQUAL)) {
            Token operator = previous();
            AurExpressionNode right = comparison();
            expressionNode = new AurLogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private AurExpressionNode comparison() {

        AurExpressionNode expressionNode = term();

        if (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            AurExpressionNode right = term();
            expressionNode = new AurLogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private AurExpressionNode term() {
        AurExpressionNode expressionNode = factor();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            AurExpressionNode right = factor();
            expressionNode = new AurBinaryExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private AurExpressionNode factor() {
        AurExpressionNode expressionNode = unary();

        while (match(STAR, SLASH)) {
            Token operator = previous();
            AurExpressionNode right = factor();
            expressionNode = new AurBinaryExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private AurExpressionNode unary() {
        if (match(MINUS, MARK)) {
            Token operator = previous();
            AurExpressionNode expressionNode = unary();
            return new AurUnaryExpression(operator, expressionNode);
        }

        return literal();
    }

    private AurExpressionNode literal() {
        if (match(INT, FLOAT, TRUE, FALSE, STRING, CHAR)) {
            return new AurLiteralExpression(previous());
        }

        if (match(IDENTIFIER)) {
            return new AurVariableGetExpression(previous());
        }

        if (match(LEFT_PAREN)) {
            return group();
        }

        error("Invalid expression: " + peek().type());
        return null;
    }

    private AurExpressionNode group() {
        Token paren = previous();
        AurExpressionNode expressionNode = expression();
        consume(RIGHT_PAREN, "Expect ')' after group expression.");

        return new AurGroupExpression(paren, expressionNode);
    }

    private void consume(TokenType type, String message) {
        if (!match(type)) {
            error(message);
        }
    }

    private void error(String message) {
        throw new AurParseException(message);
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void advance() {
        current++;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    private Token peek() {
        return tokens.get(current);
    }
}
