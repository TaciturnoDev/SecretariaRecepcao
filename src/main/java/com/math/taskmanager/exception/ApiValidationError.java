package com.math.taskmanager.exception;

import java.util.List;

public class ApiValidationError {

    private int status;
    private List<String> errors;

    public ApiValidationError(int status, List<String> errors) {
        this.status = status;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }
}
