package org.aurora.interpreter;

import org.aurora.component.AurIOComponent;

public class AurInterpretResult extends AurIOComponent<AurInterpretResult> {

    private final int result;

    public AurInterpretResult(int result) {
        this.result = result;
    }

    @Override
    public AurInterpretResult clone() {
        return new AurInterpretResult(result);
    }
}
