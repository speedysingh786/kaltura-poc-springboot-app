package com.nessathon.validation;

public class AppError {

    private String errorCode;
    private String errorLabel;

    public AppError(String errorCode, String errorLabel) {

        this.errorCode = errorCode;
        this.errorLabel = errorLabel;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorLabel() {
        return errorLabel;
    }
}
