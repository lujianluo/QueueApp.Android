package com.example.queueapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class queueingPage extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "queueingPage";
    private String restaurantId;
    private String contact;
    private TextView txtSlot;
    private TextView txtNumber;
    private TextView txtPhone;
    private TextView txtCurrent;
    private Button btnCancel;

    protected void onStart(){
        super.onStart();
        fbLoadData();
        initViews();
        initListener();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queueing_page);
        receiveProps();
    }
    public static void actionStart(Context context, String restaurantId, String contact){
        Intent intent = new Intent(context, queueingPage.class);
        intent.putExtra("restaurantId", restaurantId);
        intent.putExtra("contact", contact);
        context.startActivity(intent);
    }
    public void receiveProps(){
        Intent intent = getIntent();
        restaurantId = intent.getStringExtra("restaurantId");
        contact = intent.getStringExtra("contact");
    }
    public void initViews(){
        txtSlot = findViewById(R.id.txtSlot);
        txtNumber = findViewById(R.id.txtNumber);
        txtPhone = findViewById(R.id.txtPhone);
        txtPhone.setText("Contact:" + System.lineSeparator() + contact);
        txtCurrent = findViewById(R.id.txtCurrent);
        btnCancel = findViewById(R.id.btnCancel);
    }
    public void saveStringData(String key, String value){
        SharedPreferences stringData = getSharedPreferences("stringData", MODE_PRIVATE);
        SharedPreferences.Editor editor = stringData.edit();
        editor.putString (key, value);
        editor.commit();
        Log.i(TAG,"data saved");
    }
    public String loadStringData(String key){
        SharedPreferences stringData = getSharedPreferences("stringData", MODE_PRIVATE);
        String data = stringData.getString(key, "0");
        return data;
    }
    public void initListener(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Restaurant").document(restaurantId).collection("QueueRecord").document(contact)
                        .update("isCompleted", true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: update success");
                                Toast.makeText(queueingPage.this, "Cancel success!", Toast.LENGTH_SHORT).show();
                                restaurantPage.actionStart(queueingPage.this, restaurantId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: update fail");
                                Toast.makeText(queueingPage.this, "Cancel fail!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    public void fbLoadData(){
        db.collection("Restaurant").document(restaurantId).collection("QueueRecord").document(contact)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String identifier = document.getString("Identifier");
                        String number = document.getLong("QueueNumber").toString();
                        saveStringData("identifier", identifier);
                        txtSlot.setText("Your Slot: " + System.lineSeparator() +identifier);
                        txtNumber.setText("Your Number: " + System.lineSeparator() + number);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        String identifier = loadStringData("identifier");
        Log.d(TAG, "fbLoadData: identifier: " + identifier);
        db.collection("Restaurant").document(restaurantId).collection("QueueInfo").document(identifier)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            String current = snapshot.getLong("Current").toString();
                            txtCurrent.setText("Now Calling: " + System.lineSeparator() + current);
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });


    }
}