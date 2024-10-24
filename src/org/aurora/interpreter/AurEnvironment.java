package org.aurora.interpreter;

import org.aurora.exception.AurException;
import org.aurora.scanner.Token;
import org.aurora.type.AurValue;

import java.util.HashMap;
import java.util.Map;

public class AurEnvironment {

    private final Map<String, AurValue> variables;
    public AurEnvironment enclosing = null;

    public AurEnvironment() {
        variables = new HashMap<>();
    }

    public AurEnvironment(AurEnvironment enclosing) {
        this.enclosing = enclosing;
        variables = new HashMap<>();
    }

    public void define(Token name, AurValue value) {
        if (variables.containsKey(name.toString())) {
            System.out.println("Variable '" + name + "' already defined.");
            System.exit(1);
        }

        variables.put(name.lexeme(), value);
    }

    public void set(Token name, AurValue value) {
        if (variables.containsKey(name.lexeme())) {
            AurValue oldValue = variables.get(name.lexeme());

            if (oldValue.type == value.type) {
                variables.put(name.lexeme(), value);
            } else {
                System.out.println("Cannot assign '" + AurValueTypeConverter.valueTypeToString(value.type) +
                        "' to '" + AurValueTypeConverter.valueTypeToString(oldValue.type) + "'.");
                System.exit(1);
            }

        } else if (enclosing != null) {
            enclosing.set(name, value);
        } else {
            System.out.println("Undefined variable '" + name.lexeme() + "'.");
            System.exit(1);
        }
    }

    public AurValue get(Token name) {
        if (variables.containsKey(name.lexeme())) {
            return variables.get(name.lexeme());
        } else if (enclosing != null) {
            return enclosing.get(name);
        } else {
            System.out.println("Undefined variable '" + name.lexeme() + "'.");
            System.exit(1);
        }

        return null;
    }
}
