package com.cayekple.topiclearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {
    private EditText txtFullName, txtEmail, txtEducationLevel, txtPassword;
    private TextView tvSignIn;
    private Button btnRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        txtFullName = findViewById(R.id.txtFullname);
        txtEmail = findViewById(R.id.txtEmail);
        txtEducationLevel = findViewById(R.id.txtEducationalLevel);
        txtPassword = findViewById(R.id.txtPassword);

        btnRegister = findViewById(R.id.btnRegister);

        tvSignIn = findViewById(R.id.txtSignIn);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent signIn = new Intent(RegisterActivity.this, LoginActivity.class);
//                startActivity(signIn);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String pass = txtPassword.getText().toString();
                String fullName = txtFullName.getText().toString();
                String eduLevel = txtEducationLevel.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this, AccountSetting.class);
                                startActivity(intent);
                                finish();
                            }else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error: "+errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}