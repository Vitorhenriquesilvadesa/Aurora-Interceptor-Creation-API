package org.aurora.util;

import org.aurora.component.AurValidationComponent;

import java.util.Arrays;
import java.util.List;

public class FileExtensionValidator extends AurValidationComponent<String> {


    private final List<String> allowedExtensions;

    public FileExtensionValidator(String... allowedExtensions) {
        this.allowedExtensions = Arrays.asList(allowedExtensions);
    }

    @Override
    public void validate(String fileName) throws IllegalArgumentException {
        String fileExtension = getFileExtension(fileName);

        if (fileExtension == null || !allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException("Invalid file extension: ." + fileExtension + ". Allowed extensions are: " + allowedExtensions);
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return null;
    }
}
