package org.aurora.parser;

import org.aurora.component.AurIOComponent;
import org.aurora.parser.statement.AurStatementNode;

import java.util.List;

public class AurParsedData extends AurIOComponent<AurParsedData> {

    private final List<AurStatementNode> expressions;

    public AurParsedData(List<AurStatementNode> expressions) {
        this.expressions = expressions;
    }

    public List<AurStatementNode> getStatements() {
        return expressions;
    }

    @Override
    public AurParsedData clone() {
        return new AurParsedData(expressions);
    }
}
