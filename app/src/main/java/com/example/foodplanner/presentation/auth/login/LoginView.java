package com.example.foodplanner.presentation.auth.login;

public interface LoginView {
    void showLoading();
    void hideLoading();
    void showError(String message);
    void showSuccess(String message);
    void navigateToHome();
    void setUsernameError(String error);
    void setPasswordError(String error);
    void clearErrors();
    String getUsername();
    String getPassword();
}
