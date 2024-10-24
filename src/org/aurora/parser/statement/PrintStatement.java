package org.aurora.parser.statement;

import org.aurora.parser.expression.AurExpressionNode;
import org.aurora.processor.AurStatementNodeProcessor;

public class PrintStatement extends AurStatementNode {
    public final AurExpressionNode value;

    public PrintStatement(AurExpressionNode value) {
        this.value = value;
    }

    @Override
    public <T> T acceptProcessor(AurStatementNodeProcessor<T> processor) {
        return processor.processPrintStatement(this);
    }
}
