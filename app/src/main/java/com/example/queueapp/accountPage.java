package com.example.queueapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.QuerySnapshot;

public class accountPage extends AppCompatActivity {
    private EditText edtTxtemail;
    private EditText edtTxtpassword;
    private TextView txtAccInstruction;
    private Button btnLogin;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private static final String TAG = "accountPage";

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            adminPage.actionStart(accountPage.this);
        }
        initViews();
        initListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, accountPage.class);
        context.startActivity(intent);
    }

    private void initViews() {
        edtTxtemail = findViewById(R.id.edtTxtemail);
        edtTxtpassword = findViewById(R.id.edtTxtpassword);
        txtAccInstruction = findViewById(R.id.txtAccInstruction);
        txtAccInstruction.setText("Enter your email and password for login or register!");
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void initListener() {
        password = edtTxtpassword.getText().toString();
        email = edtTxtemail.getText().toString();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (epIsValid()) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(accountPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        adminPage.actionStart(accountPage.this);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(accountPage.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        txtAccInstruction.setText("Login fail!"+ System.lineSeparator() +"Please check your email and password");
                                    }
                                }
                            });
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (epIsValid()) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(accountPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        adminPage.actionStart(accountPage.this);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(accountPage.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        txtAccInstruction.setText("Register fail!");
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean epIsValid() {
        password = edtTxtpassword.getText().toString();
        email = edtTxtemail.getText().toString();
        boolean emailIsValid = email.matches("^[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}");
        int passwordLength = password.length();
        if (passwordLength >= 6 && emailIsValid) {
            return true;
        } else if (!emailIsValid) {
            txtAccInstruction.setText("Email is badly formatted!");
            return false;
        } else if (passwordLength < 6) {
            txtAccInstruction.setText("Password length must be greater than 6!");
            return false;
        }
        return false;
    }

}