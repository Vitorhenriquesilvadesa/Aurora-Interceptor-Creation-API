package org.aurora.util;

import org.aurora.component.AurIOComponent;
import org.aurora.component.AurValidationComponent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class AurFile extends AurIOComponent<AurFile> {

    private final String source;

    public AurFile(String path) {
        AurValidationComponent<String> validator = new FileExtensionValidator("aurora");
        validator.validate(path);
        source = extract(path);
    }

    public AurFile(AurFile file) {
        this.source = file.source;
    }

    public String getSource() {
        return source;
    }

    private String extract(String path) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    @Override
    public AurIOComponent clone() {
        return new AurFile(this);
    }
}
