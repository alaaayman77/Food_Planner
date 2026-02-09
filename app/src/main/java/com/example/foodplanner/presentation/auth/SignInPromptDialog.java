package com.example.foodplanner.presentation.auth;



import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.foodplanner.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SignInPromptDialog extends DialogFragment {

    public interface SignInPromptListener {
        void onSignInClicked();
        void onContinueAsGuestClicked();
    }

    private SignInPromptListener listener;
    private String message;
    private String featureName;

    public static SignInPromptDialog newInstance(String featureName, String message) {
        SignInPromptDialog dialog = new SignInPromptDialog();
        Bundle args = new Bundle();
        args.putString("feature", featureName);
        args.putString("message", message);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            featureName = getArguments().getString("feature");
            message = getArguments().getString("message");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signin_prompt_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView titleText = view.findViewById(R.id.dialog_title);
        TextView messageText = view.findViewById(R.id.dialog_message);
        MaterialButton signInButton = view.findViewById(R.id.btn_sign_in);
        MaterialButton continueButton = view.findViewById(R.id.btn_continue_guest);

        if (featureName != null) {
            titleText.setText(featureName);
        }

        if (message != null) {
            messageText.setText(message);
        }

        signInButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSignInClicked();
            }
            dismiss();
        });

        continueButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContinueAsGuestClicked();
            }
            dismiss();
        });
    }

    public void setListener(SignInPromptListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}