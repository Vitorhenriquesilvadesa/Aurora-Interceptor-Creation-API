package org.aurora.parser.statement;

import org.aurora.parser.expression.AurExpressionNode;
import org.aurora.processor.AurStatementNodeProcessor;

public class AurExpressionStatement extends AurStatementNode {

    public final AurExpressionNode expression;

    public AurExpressionStatement(AurExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public <T> T acceptProcessor(AurStatementNodeProcessor<T> processor) {
        return processor.processExpressionStatement(this);
    }
}
