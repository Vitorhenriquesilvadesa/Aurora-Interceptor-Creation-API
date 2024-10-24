package org.aurora.parser.expression;

import org.aurora.processor.AurExpressionNodeProcessor;
import org.aurora.scanner.Token;

public class AurLiteralExpression extends AurExpressionNode {

    public final Token literal;

    public AurLiteralExpression(Token literal) {
        this.literal = literal;
    }

    @Override
    public <T> T acceptProcessor(AurExpressionNodeProcessor<T> processor) {
        return processor.processLiteralExpression(this);
    }

    @Override
    public String toString() {
        return "Literal(" + literal.literal() + ")";
    }
}
