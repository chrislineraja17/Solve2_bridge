package com.solve_bridge.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText etEmail, etPassword;
    Button btnLogin, btnGoogleSignIn;
    TextView tvRegister, forgotPassword;
    ImageView showPassword;
    boolean isPasswordVisible = false;

    FirebaseAuth mAuth;
    CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG, "onCreate: User already logged in, redirecting to HomeActivity");
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        tvRegister = findViewById(R.id.tvRegister);
        forgotPassword = findViewById(R.id.forgotPassword);
        showPassword = findViewById(R.id.showPassword);

        credentialManager = CredentialManager.create(this);

        showPassword.setImageResource(R.drawable.ic_eye_off);
        showPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPassword.setImageResource(R.drawable.ic_eye_off);
                isPasswordVisible = false;
            } else {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPassword.setImageResource(R.drawable.ic_eye);
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d(TAG, "btnLogin: Attempting login for " + email);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "btnLogin: Login successful");
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = "Login Failed";
                            if (task.getException() instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                Log.e(TAG, "btnLogin: Firebase Auth Error Code: " + errorCode);
                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL": errorMessage = "Invalid email format."; break;
                                    case "ERROR_USER_NOT_FOUND": errorMessage = "Account not found. Please register."; break;
                                    case "ERROR_WRONG_PASSWORD": errorMessage = "Incorrect password."; break;
                                    default: errorMessage = task.getException().getMessage();
                                }
                            } else if (task.getException() != null) {
                                errorMessage = task.getException().getMessage();
                            }
                            Log.e(TAG, "btnLogin error: " + errorMessage);
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }

    private void signInWithGoogle() {
        Log.d(TAG, "signInWithGoogle: Request started");
        
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(this, request, null, ContextCompat.getMainExecutor(this),
                new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignInResult(result.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "Google Error: " + e.getMessage());
                        String message = e.getMessage();
                        if (e instanceof NoCredentialException) {
                            message = "No accounts found. Check SHA-1 in Firebase Console.";
                        }
                        String finalMessage = message;
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, finalMessage, Toast.LENGTH_LONG).show());
                    }
                }
        );
    }

    private void handleSignInResult(Credential credential) {
        try {
            if (credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                String idToken = googleIdTokenCredential.getIdToken();
                Log.d(TAG, "handleSignInResult: idToken length: " + (idToken != null ? idToken.length() : 0));

                AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
                mAuth.signInWithCredential(authCredential)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Firebase sign in successful");
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Log.e(TAG, "Firebase sign in failed: " + error);
                                Toast.makeText(MainActivity.this, "Authentication Failed: " + error, Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Log.e(TAG, "Unexpected credential type: " + credential.getType());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing credential", e);
        }
    }
}
