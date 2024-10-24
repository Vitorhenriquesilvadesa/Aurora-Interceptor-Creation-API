package org.aurora.parser.statement;

import org.aurora.parser.expression.AurExpressionNode;
import org.aurora.processor.AurStatementNodeProcessor;

public class AurIfStatement extends AurStatementNode {

    public final AurExpressionNode condition;
    public final AurStatementNode thenStatement;
    public final AurStatementNode elseStatement;

    public AurIfStatement(AurExpressionNode condition, AurStatementNode thenStatement, AurStatementNode elseStatement) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public <T> T acceptProcessor(AurStatementNodeProcessor<T> processor) {
        return processor.processIfStatement(this);
    }
}
