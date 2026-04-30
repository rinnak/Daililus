package com.example.daililus;

public class AuthState {
    private final boolean isLoading;
    private final boolean isSuccess;
    private final String error;
    public AuthState(boolean isLoading, boolean isSuccess, String error) {
        this.isLoading = isLoading;
        this.isSuccess = isSuccess;
        this.error = error;
    }
    public boolean isLoading() { return isLoading; }
    public boolean isSuccess() { return isSuccess; }
    public String getError() { return error; }
}

