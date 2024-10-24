package org.aurora.semantics;

import org.aurora.parser.AurParsedData;
import org.aurora.pass.AurCompilationPass;

public class AurSemanticAnalyzerPass extends AurCompilationPass<AurParsedData, AurParsedData> {

    @Override
    public Class<AurParsedData> getInputType() {
        return AurParsedData.class;
    }

    @Override
    public Class<AurParsedData> getOutputType() {
        return AurParsedData.class;
    }

    @Override
    public String getDebugName() {
        return "Semantic Analyzer Pass";
    }

    @Override
    protected AurParsedData pass(AurParsedData input) {
        return null;
    }
}
