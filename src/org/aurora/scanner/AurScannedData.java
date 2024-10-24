package org.aurora.scanner;

import org.aurora.component.AurIOComponent;

import java.util.ArrayList;
import java.util.List;

public class AurScannedData extends AurIOComponent<AurScannedData> {

    private final List<Token> tokens;

    public AurScannedData(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    public AurScannedData clone() {
        List<Token> clonedTokens = new ArrayList<>(tokens.size());
        for(Token token : tokens) {
            Token clonedToken = new Token(token.type(), token.lexeme(), token.literal(), token.line());
            clonedTokens.add(clonedToken);
        }

        return new AurScannedData(clonedTokens);
    }
}
