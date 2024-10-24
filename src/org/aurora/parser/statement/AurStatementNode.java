package org.aurora.parser.statement;

import org.aurora.processor.AurStatementNodeProcessor;

public abstract class AurStatementNode {

    public abstract <T> T acceptProcessor(AurStatementNodeProcessor<T> processor);
}
