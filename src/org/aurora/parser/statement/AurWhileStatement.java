package org.aurora.parser.statement;

import org.aurora.parser.expression.AurExpressionNode;
import org.aurora.processor.AurStatementNodeProcessor;

public class AurWhileStatement extends AurStatementNode {

    public final AurExpressionNode condition;
    public final AurStatementNode body;

    public AurWhileStatement(AurExpressionNode condition, AurStatementNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <T> T acceptProcessor(AurStatementNodeProcessor<T> processor) {
        return processor.processWhileStatement(this);
    }
}
