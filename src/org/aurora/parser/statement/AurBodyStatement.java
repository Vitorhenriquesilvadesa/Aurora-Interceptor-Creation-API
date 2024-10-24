package org.aurora.parser.statement;

import org.aurora.processor.AurStatementNodeProcessor;

import java.util.List;

public class AurBodyStatement extends AurStatementNode {

    public final List<AurStatementNode> statements;

    public AurBodyStatement(List<AurStatementNode> statements) {
        this.statements = statements;
    }

    @Override
    public <T> T acceptProcessor(AurStatementNodeProcessor<T> processor) {
        return processor.processBodyStatement(this);
    }
}
