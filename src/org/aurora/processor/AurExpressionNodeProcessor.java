package org.aurora.processor;

import org.aurora.parser.expression.*;
import org.aurora.parser.expression.AurVariableGetExpression;

public interface AurExpressionNodeProcessor<T> {

    T processLiteralExpression(AurLiteralExpression expression);

    T processBinaryExpression(AurBinaryExpression expression);

    T processUnaryExpression(AurUnaryExpression expression);

    T processLogicalExpression(AurLogicalExpression expression);

    T processGroupExpression(AurGroupExpression expression);

    T processVariableGetExpression(AurVariableGetExpression expression);

    T processAssignmentExpression(AurAssignmentExpression expression);
}
