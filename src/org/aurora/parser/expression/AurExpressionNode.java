package org.aurora.parser.expression;

import org.aurora.processor.AurExpressionNodeProcessor;

public abstract class AurExpressionNode {

    public abstract <T> T acceptProcessor(AurExpressionNodeProcessor<T> processor);
}
