package org.aurora.parser.expression;

import org.aurora.processor.AurExpressionNodeProcessor;
import org.aurora.scanner.Token;

public class AurGroupExpression extends AurExpressionNode {

    public final Token paren;
    public final AurExpressionNode expression;

    public AurGroupExpression(Token paren, AurExpressionNode expression) {
        this.paren = paren;
        this.expression = expression;
    }

    @Override
    public <T> T acceptProcessor(AurExpressionNodeProcessor<T> processor) {
        return processor.processGroupExpression(this);
    }
}
