package org.aurora.parser.expression;

import org.aurora.processor.AurExpressionNodeProcessor;
import org.aurora.scanner.Token;

public class AurBinaryExpression extends AurExpressionNode {

    public final AurExpressionNode left;
    public final Token operator;
    public final AurExpressionNode right;

    public AurBinaryExpression(AurExpressionNode left, Token operator, AurExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <T> T acceptProcessor(AurExpressionNodeProcessor<T> processor) {
        return processor.processBinaryExpression(this);
    }
}
