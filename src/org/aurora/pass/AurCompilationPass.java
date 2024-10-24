package org.aurora.pass;

import org.aurora.component.AurIOComponent;
import org.aurora.interceptor.AurPassiveInterceptor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public abstract class AurCompilationPass<I extends AurIOComponent, O extends AurIOComponent> {

    private final List<AurPassiveInterceptor<I, O>> interceptors = new ArrayList<>();

    public AurCompilationPass<I, O> addInterceptor(AurPassiveInterceptor<I, O> interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    public final O run(I input) {
        return pass(input);
    }

    public final O runWithInterceptors(I input) {

        for (AurPassiveInterceptor<I, O> interceptor : interceptors) {
            interceptor.beforeState((I) input.clone());
        }

        O output = pass(input);

        for (AurPassiveInterceptor<I, O> interceptor : interceptors) {
            interceptor.afterState(output);
        }

        return output;
    }

    public abstract Class<I> getInputType();

    public abstract Class<O> getOutputType();

    public abstract String getDebugName();

    protected abstract O pass(I input);
}
