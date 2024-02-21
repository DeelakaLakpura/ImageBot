package com.yashodha.imagebot.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.yashodha.imagebot.R;
import com.yashodha.imagebot.User.RegisterActivity;
import com.yashodha.imagebot.User.ResetPassActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    EditText emailEditText;
    EditText passwordEditText;
    Button loginBtn;
    TextView resetBtn;
    TextView createAccountBtn;
    TextView createAcc;
    ProgressDialog progressDialog; // Add ProgressDialog variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginbutton);
        resetBtn = findViewById(R.id.resetPwTextView);
        createAccountBtn = findViewById(R.id.createAccTextView);
        firebaseAuth = FirebaseAuth.getInstance();
        createAcc = findViewById(R.id.createAccTextView);

        progressDialog = new ProgressDialog(this); // Initialize ProgressDialog

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ResetPassActivity.class);
                startActivity(i);
            }
        });

    }

    public void loginUser(){

        String email = emailEditText.getText().toString().trim();
        String password  = passwordEditText.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        // Show ProgressDialog
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Dismiss ProgressDialog
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }
}
