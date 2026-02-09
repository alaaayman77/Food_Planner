package com.example.foodplanner.presentation.auth.login;

public interface LoginPresenter {

    void handleLogin();
    void handleGoogleSignIn();
    void handleTwitterSignIn();
    void handleGuestMode();
    void onGoogleSignInResult(String idToken);
    void onTwitterSignInResult(com.google.firebase.auth.AuthResult authResult);
    void onGoogleSignInError(int statusCode);
    void onTwitterSignInError(Exception exception);
    void onDestroy();
}
