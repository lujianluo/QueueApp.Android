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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {

    private EditText edtTxtrestaurantId;
    private TextView txtBusinessEntry;
    private Button btnEnter;
    private static final String TAG = "mainPage";
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListener();
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
            // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            adminPage.actionStart(MainActivity.this);
        }
    }
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
    private void initViews() {
        edtTxtrestaurantId = findViewById(R.id.edtResID);
        btnEnter = findViewById(R.id.btnEnter);
        txtBusinessEntry = findViewById(R.id.txtBusinessEntey);
        txtBusinessEntry.setText("For business administration, please click here to join us!");
    }

    private void initListener(){
        txtBusinessEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountPage.actionStart(MainActivity.this);
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    String restaurantId = edtTxtrestaurantId.getText().toString();
                    restaurantPage.actionStart(MainActivity.this, restaurantId);
                }
            }
        });
    }
    private boolean isValid() {
        String userInput = edtTxtrestaurantId.getText().toString();
        boolean isNumber = android.text.TextUtils.isDigitsOnly(userInput);
        int length = userInput.length();
        if (isNumber && length != 0) {
            int num = Integer.parseInt(userInput);
            db.collection("Restaurant")
                    .whereEqualTo("RestaurantId", num)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot doc = task.getResult();
                                if (doc.isEmpty()) {
                                    Toast.makeText(MainActivity.this, "No record found", Toast.LENGTH_SHORT).show();
                                } else {
                                    restaurantPage.actionStart(MainActivity.this, userInput);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else if (length == 0) {
            Toast.makeText(MainActivity.this, "Please enter restaurantId!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isNumber) {
            Toast.makeText(MainActivity.this, "Accept number only!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return false;
        }
        return false;
    }

}