package org.aurora.optimizer;

import org.aurora.compiler.AurInstructionCode;
import org.aurora.parser.AurParsedData;
import org.aurora.parser.expression.AurBinaryExpression;
import org.aurora.parser.expression.AurExpressionNode;
import org.aurora.parser.expression.AurGroupExpression;
import org.aurora.parser.expression.AurLiteralExpression;
import org.aurora.parser.statement.AurExpressionStatement;
import org.aurora.parser.statement.AurStatementNode;
import org.aurora.parser.statement.VariableDeclarationStatement;
import org.aurora.pass.AurCompilationPass;
import org.aurora.scanner.Token;
import org.aurora.scanner.TokenType;
import org.aurora.type.AurValue;
import org.aurora.type.AurValueType;

import java.util.ArrayList;
import java.util.List;

public class AurOptimizationPass extends AurCompilationPass<AurParsedData, AurParsedData> {

    @Override
    public Class<AurParsedData> getInputType() {
        return AurParsedData.class;
    }

    @Override
    public Class<AurParsedData> getOutputType() {
        return AurParsedData.class;
    }

    @Override
    public String getDebugName() {
        return "Optimization Pass";
    }

    @Override
    protected AurParsedData pass(AurParsedData input) {
        List<AurStatementNode> statements = new ArrayList<>();

        for (AurStatementNode node : input.getStatements()) {
            statements.add(optimize(node));
        }

        return new AurParsedData(statements);
    }

    private AurStatementNode optimize(AurStatementNode node) {
        if (node instanceof AurExpressionStatement) {
            AurExpressionNode expression = ((AurExpressionStatement) node).expression;
            return new AurExpressionStatement(optimize(expression));
        }
        if (node instanceof VariableDeclarationStatement statement) {
            AurExpressionNode value = optimize(statement.value);
            return new VariableDeclarationStatement(statement.type, statement.name, value);
        }

        return node;
    }

    private AurExpressionNode optimize(AurExpressionNode expression) {
        if (expression instanceof AurBinaryExpression binary) {
            return tryResolveBinary(binary);
        }
        if (expression instanceof AurGroupExpression group) {
            return tryResolveGroup(group);
        }

        return expression;
    }

    private AurExpressionNode tryResolveGroup(AurGroupExpression group) {
        AurExpressionNode optimizedExpression = optimize(group.expression);

        if (optimizedExpression instanceof AurLiteralExpression) {
            return optimizedExpression;
        }

        return group;
    }

    private AurExpressionNode tryResolveBinary(AurBinaryExpression binary) {
        AurExpressionNode left = optimize(binary.left);
        AurExpressionNode right = optimize(binary.right);

        if (left instanceof AurLiteralExpression leftLiteral && right instanceof AurLiteralExpression rightLiteral) {
            AurValue a = leftLiteral.literal.literal();
            AurValue b = rightLiteral.literal.literal();

            AurValue resultValue;

            switch (binary.operator.type()) {
                case TokenType.PLUS: {
                    resultValue = new AurValue((int) a.value + (int) b.value, AurValueType.INT);
                    break;
                }

                case TokenType.MINUS: {
                    resultValue = new AurValue((int) a.value - (int) b.value, AurValueType.INT);
                    break;
                }

                case TokenType.STAR: {
                    resultValue = new AurValue((int) a.value * (int) b.value, AurValueType.INT);
                    break;
                }
                case TokenType.SLASH: {
                    resultValue = new AurValue((int) a.value / (int) b.value, AurValueType.INT);
                    break;
                }

                default:
                    return binary;
            }

            switch (resultValue.type) {
                case AurValueType.INT:
                    return new AurLiteralExpression(new Token(TokenType.INT, null, resultValue, 0));
                case AurValueType.FLOAT:
                    return new AurLiteralExpression(new Token(TokenType.FLOAT, null, resultValue, 0));
                case AurValueType.CHAR:
                    return new AurLiteralExpression(new Token(TokenType.CHAR, null, resultValue, 0));
                case AurValueType.STRING:
                    return new AurLiteralExpression(new Token(TokenType.STRING, null, resultValue, 0));
            }
        }

        return new AurBinaryExpression(left, binary.operator, right);
    }
}
