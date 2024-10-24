package org.aurora.parser.expression;

import org.aurora.processor.AurExpressionNodeProcessor;
import org.aurora.scanner.Token;

public class AurAssignmentExpression extends AurExpressionNode {

    public final Token name;
    public final Token equals;
    public final AurExpressionNode value;

    public AurAssignmentExpression(Token name, Token equals, AurExpressionNode value) {
        this.name = name;
        this.equals = equals;
        this.value = value;
    }

    @Override
    public <T> T acceptProcessor(AurExpressionNodeProcessor<T> processor) {
        return processor.processAssignmentExpression(this);
    }
}
