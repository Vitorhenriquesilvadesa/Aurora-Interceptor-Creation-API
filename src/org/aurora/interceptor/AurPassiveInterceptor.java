package org.aurora.interceptor;

public interface AurPassiveInterceptor<I, O> {
    void beforeState(final I input);

    void afterState(final O input);

    String getName();
}
