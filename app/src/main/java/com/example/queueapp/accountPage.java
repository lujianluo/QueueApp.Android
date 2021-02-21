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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class accountPage extends AppCompatActivity {
    private EditText edtTxtEmail;
    private EditText edtTxtPassword;
    private TextView txtAccInstruction;
    private Button btnLogin;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private String email;
    private String password;
    private static final String TAG = "accountPage";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        edtTxtEmail = findViewById(R.id.edtTxtemail);
        edtTxtPassword = findViewById(R.id.edtTxtpassword);
        txtAccInstruction = findViewById(R.id.txtAccInstruction);
        txtAccInstruction.setText("Enter your email and password for login or register!");
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void initListener() {
        password = edtTxtPassword.getText().toString();
        email = edtTxtEmail.getText().toString();
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
                                        Toast.makeText(accountPage.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        txtAccInstruction.setText("Login fail!" + System.lineSeparator() + "Please check your email and password");
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
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String uid = user.getUid();
                                        db.collection("record").document("idRecord")
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    Log.d(TAG, "onComplete: checking record" + document.getData());
                                                    if (document.exists()) {
                                                        int idCount = document.getLong("restaurantId").intValue();
                                                        int newId = idCount += 1;
                                                        String stringNewId = String.valueOf(newId);
                                                        Log.d(TAG, "onComplete: idCount: " + idCount + "newId: " + newId + stringNewId);
                                                        createFile(stringNewId, uid);
                                                    } else {
                                                        Log.d(TAG, "No such document");
                                                    }
                                                } else {
                                                    Log.d(TAG, "get failed with ", task.getException());
                                                }
                                            }
                                        });
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

    private void createFile(String newId, String currentUser) {
        DocumentReference record = db.collection("record").document("idRecord");
        DocumentReference home = db.collection("restaurant").document(newId);
        DocumentReference aInfo = db.collection("restaurant").document(newId).collection("queueInfo").document("slotA");
        DocumentReference bInfo = db.collection("restaurant").document(newId).collection("queueInfo").document("slotB");
        DocumentReference cInfo = db.collection("restaurant").document(newId).collection("queueInfo").document("slotC");
        DocumentReference aSetting = db.collection("restaurant").document(newId).collection("queueSetting").document("slotA");
        DocumentReference bSetting = db.collection("restaurant").document(newId).collection("queueSetting").document("slotB");
        DocumentReference cSetting = db.collection("restaurant").document(newId).collection("queueSetting").document("slotC");
        Log.d(TAG, "createFile: start batch");
        WriteBatch batch = db.batch();
        Map<String, Object> newRestaurant = new HashMap<>();
        newRestaurant.put("restaurantId", newId);
        newRestaurant.put("restaurantName", "Sample Name");
        newRestaurant.put("restaurantOwner", currentUser);
        batch.set(aInfo, new info("slotA", 0, 0, 0));
        batch.set(bInfo, new info("slotB", 0, 0, 0));
        batch.set(cInfo, new info("slotC", 0, 0, 0));
        batch.set(aSetting, new setting("slotA", 1, 2, 20));
        batch.set(bSetting, new setting("slotB", 3, 4, 20));
        batch.set(cSetting, new setting("slotC", 5, 6, 20));
        batch.update(record, "restaurantId", FieldValue.increment(1));

        batch.set(home, newRestaurant);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: batch complete");
                settingPage.actionStart(accountPage.this, true);
            }
        });
    }

    private boolean epIsValid() {
        password = edtTxtPassword.getText().toString();
        email = edtTxtEmail.getText().toString();
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