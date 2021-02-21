package com.example.queueapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class adminPage extends AppCompatActivity {
    private TextView test;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        test = findViewById(R.id.test);
        mAuth = FirebaseAuth.getInstance();
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                MainActivity.actionStart(adminPage.this);
            }
        });
    }
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, adminPage.class);
        context.startActivity(intent);
    }
}