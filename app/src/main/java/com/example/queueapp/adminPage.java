package com.example.queueapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class adminPage extends AppCompatActivity {
    private TextView test;
    private FirebaseAuth mAuth;
    private Button test2;
    private boolean firstLogin;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "adminPage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        test = findViewById(R.id.test);
        test2 = findViewById(R.id.test2);
        mAuth = FirebaseAuth.getInstance();
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                MainActivity.actionStart(adminPage.this);
            }
        });

        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingPage.actionStart(adminPage.this, false);
            }
        });
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, adminPage.class);
        context.startActivity(intent);
    }


}