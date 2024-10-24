package org.aurora.processor;

import org.aurora.parser.statement.*;

public interface AurStatementNodeProcessor<T> {

    T processIfStatement(AurIfStatement statement);

    T processExpressionStatement(AurExpressionStatement statement);

    T processBodyStatement(AurBodyStatement statement);

    T processPrintStatement(PrintStatement statement);

    T processVariableDeclaration(VariableDeclarationStatement statement);

    T processWhileStatement(AurWhileStatement statement);
}
