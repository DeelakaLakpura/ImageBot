package com.yashodha.imagebot.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yashodha.imagebot.HomeActivity;
import com.yashodha.imagebot.MainActivity;
import com.yashodha.imagebot.R;

public class RegisterActivity extends AppCompatActivity {

    EditText signUpNameEditTxt;
    EditText signUpEmailEditTxt;
    EditText signUppasswordEditTxt;
    EditText signUpConfirmPasswordEditTxt;
    TextView returnToLoginTextV;
    Button signUpBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this); // Add this line to initialize progressDialog
        signUpNameEditTxt = findViewById(R.id.signUpNameEditText);
        signUpEmailEditTxt = findViewById(R.id.signUpEmailEditText);
        signUppasswordEditTxt = findViewById(R.id.signUpPasswordEditText);
        signUpConfirmPasswordEditTxt = findViewById(R.id.signUpConfirmPasswordEditText);
        returnToLoginTextV = findViewById(R.id.loginReturnTextView);
        signUpBtn = findViewById(R.id.signUpButton); // Initialize signUpBtn here

        // Set OnClickListener for signUpBtn
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserAccount();
            }
        });


        returnToLoginTextV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i2);
            }
        });

        createAuthStateListener();
    }





    public void createUserAccount(){
        final String createName = signUpNameEditTxt.getText().toString().trim();
        final String createEmail = signUpEmailEditTxt.getText().toString().trim();
        String createPassword = signUppasswordEditTxt.getText().toString().trim();
        String createConfirmPassword = signUpConfirmPasswordEditTxt.getText().toString().trim();

        if(TextUtils.isEmpty(createEmail) || TextUtils.isEmpty(createPassword)){
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_LONG).show();
            return;
        }

        if (!createPassword.equals(createConfirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(createEmail, createPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createAuthStateListener() {
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }


}
