package org.aurora.parser.expression;

import org.aurora.processor.AurExpressionNodeProcessor;
import org.aurora.scanner.Token;

public class AurUnaryExpression extends AurExpressionNode {

    public final Token operator;
    public final AurExpressionNode expression;

    public AurUnaryExpression(Token operator, AurExpressionNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public <T> T acceptProcessor(AurExpressionNodeProcessor<T> processor) {
        return processor.processUnaryExpression(this);
    }

    @Override
    public String toString() {
        return "Unary('" + operator.lexeme() + "', " + expression.toString() + ")";
    }
}
