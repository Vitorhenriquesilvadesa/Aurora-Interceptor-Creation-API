package org.aurora.compiler;

import org.aurora.component.AurIOComponent;
import org.aurora.pass.AurCompilationPass;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class CompilationPipeline {

    private final List<AurCompilationPass<? extends AurIOComponent, ? extends AurIOComponent>> passes = new ArrayList<>();

    public void insertStage(AurCompilationPass<? extends AurIOComponent, ? extends AurIOComponent> pass) {
        passes.add(pass);
    }

    public void run(AurIOComponent input) {
        AurIOComponent currentInput = input;
        for (AurCompilationPass<? extends AurIOComponent, ? extends AurIOComponent> pass : passes) {
            // System.out.println("Running " + pass.getDebugName() + ".");
            currentInput = runPass(pass, currentInput);
        }
    }

    public void runWithInterceptors(AurIOComponent input) {
        AurIOComponent currentInput = input;
        for (AurCompilationPass<? extends AurIOComponent, ? extends AurIOComponent> pass : passes) {
            System.out.println("Running " + pass.getDebugName() + " with interceptors.");
            currentInput = runPassWithInterceptors(pass, currentInput);
        }
    }

    @SuppressWarnings("unchecked")
    private <I extends AurIOComponent, O extends AurIOComponent> AurIOComponent runPass(AurCompilationPass<I, O> pass, AurIOComponent input) {
        if (!pass.getInputType().isInstance(input)) {
            throw new IllegalArgumentException("Input type mismatch. Expected: " + pass.getInputType() + ", but got: " + input.getClass());
        }
        return pass.run((I) input);
    }

    @SuppressWarnings("unchecked")
    private <I extends AurIOComponent, O extends AurIOComponent> AurIOComponent runPassWithInterceptors(AurCompilationPass<I, O> pass, AurIOComponent input) {
        if (!pass.getInputType().isInstance(input)) {
            throw new IllegalArgumentException("Input type mismatch. Expected: " + pass.getInputType() + ", but got: " + input.getClass());
        }
        return pass.runWithInterceptors((I) input);
    }
}
