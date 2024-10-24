package org.aurora.parser.statement;

import org.aurora.parser.expression.AurExpressionNode;
import org.aurora.processor.AurStatementNodeProcessor;
import org.aurora.scanner.Token;

public class VariableDeclarationStatement extends AurStatementNode {

    public final Token type;
    public final Token name;
    public final AurExpressionNode value;

    public VariableDeclarationStatement(Token type, Token name, AurExpressionNode value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    @Override
    public <T> T acceptProcessor(AurStatementNodeProcessor<T> processor) {
        return processor.processVariableDeclaration(this);
    }
}
