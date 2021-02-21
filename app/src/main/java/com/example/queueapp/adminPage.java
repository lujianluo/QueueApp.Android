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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class adminPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnSetting;
    private Button btnSignOut;
    private TextView txtSlotACurrent;
    private TextView txtSlotAIssued;
    private TextView txtSlotAWaiting;
    private TextView txtSlotBCurrent;
    private TextView txtSlotBIssued;
    private TextView txtSlotBWaiting;
    private TextView txtSlotCCurrent;
    private TextView txtSlotCIssued;
    private TextView txtSlotCWaiting;
    private Button btnReset;
    private Button btnSlotA;
    private Button btnSlotB;
    private Button btnSlotC;
    private TextView txtRestaurantId;
    private TextView txtContact;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "adminPage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        initViews();
        initListener();
        loadData();
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, adminPage.class);
        context.startActivity(intent);
    }

    private void initViews() {
        txtSlotACurrent = findViewById(R.id.txtSlotACurrentAdmin);
        txtSlotAIssued = findViewById(R.id.txtSlotAIssuedAdmin);
        txtSlotAWaiting = findViewById(R.id.txtSlotAWaitingAdmin);
        txtSlotBCurrent = findViewById(R.id.txtSlotBCurrentAdmin);
        txtSlotBIssued = findViewById(R.id.txtSlotBIssuedAdmin);
        txtSlotBWaiting = findViewById(R.id.txtSlotBWaitingAdmin);
        txtSlotCCurrent = findViewById(R.id.txtSlotCCurrentAdmin);
        txtSlotCIssued = findViewById(R.id.txtSlotCIssuedAdmin);
        txtSlotCWaiting = findViewById(R.id.txtSlotCWaitingAdmin);
        btnSetting = findViewById(R.id.btnSetting);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnReset = findViewById(R.id.btnReset);
        btnSlotA = findViewById(R.id.btnSlotACall);
        btnSlotB = findViewById(R.id.btnSlotBCall);
        btnSlotC = findViewById(R.id.btnSlotCCall);
        txtContact = findViewById(R.id.txtContact);
        txtRestaurantId = findViewById(R.id.txtRestaurantId);
    }

    private void initListener() {
        String restaurantId = loadData("restaurantId");
        Log.d(TAG, "initListener: listener loaded id: " + restaurantId);
        DocumentReference aInfo = db.collection("restaurant").document(restaurantId).collection("queueInfo").document("slotA");
        DocumentReference bInfo = db.collection("restaurant").document(restaurantId).collection("queueInfo").document("slotB");
        DocumentReference cInfo = db.collection("restaurant").document(restaurantId).collection("queueInfo").document("slotC");
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                MainActivity.actionStart(adminPage.this);
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingPage.actionStart(adminPage.this, false);
            }
        });
        btnSlotA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slotAWaiting = loadData("slotAWaiting");
                int intSlotAWaiting = Integer.parseInt(slotAWaiting);
                if (intSlotAWaiting > 0) {
                    String current = loadData("slotACurrent");
                    int intCurrent = Integer.parseInt(current);
                    int callNumber = intCurrent += 1;
                    Log.d(TAG, "onClick: callNumber: " + callNumber);
                    db.collection("restaurant").document(restaurantId).collection("queueRecord")
                            .whereEqualTo("identifier", "slotA")
                            .whereEqualTo("queueNumber", callNumber)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: task completed");
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String contact = document.getString("contact");
                                            txtContact.setText("Contact of ticket called: " + contact);
                                            saveData("newContact", contact);
                                            Log.d(TAG, "onComplete: contact save:" + contact);
                                            Toast.makeText(adminPage.this, "Contact: " + contact, Toast.LENGTH_SHORT).show();
                                            update(aInfo, restaurantId);
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                        Toast.makeText(adminPage.this, "No Record Found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(adminPage.this,"No ticket to call!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSlotB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slotBWaiting = loadData("slotBWaiting");
                int intSlotBWaiting = Integer.parseInt(slotBWaiting);
                if (intSlotBWaiting > 0) {
                    String current = loadData("slotBCurrent");
                    int intCurrent = Integer.parseInt(current);
                    int callNumber = intCurrent += 1;
                    Log.d(TAG, "onClick: callNumber: " + callNumber);
                    db.collection("restaurant").document(restaurantId).collection("queueRecord")
                            .whereEqualTo("identifier", "slotB")
                            .whereEqualTo("queueNumber", callNumber)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: task completed");
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String contact = document.getString("contact");
                                            txtContact.setText("Contact: " + contact);
                                            saveData("newContact", contact);
                                            Log.d(TAG, "onComplete: contact save:" + contact);
                                            Toast.makeText(adminPage.this, "Contact: " + contact, Toast.LENGTH_SHORT).show();
                                            update(bInfo, restaurantId);
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                        Toast.makeText(adminPage.this, "No Record Found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(adminPage.this,"No ticket to call!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSlotC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slotCWaiting = loadData("slotCWaiting");
                int intSlotCWaiting = Integer.parseInt(slotCWaiting);
                if (intSlotCWaiting > 0) {
                    String current = loadData("slotCCurrent");
                    int intCurrent = Integer.parseInt(current);
                    int callNumber = intCurrent += 1;
                    Log.d(TAG, "onClick: callNumber: " + callNumber);
                    db.collection("restaurant").document(restaurantId).collection("queueRecord")
                            .whereEqualTo("identifier", "slotC")
                            .whereEqualTo("queueNumber", callNumber)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: task completed");
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String contact = document.getString("contact");
                                            txtContact.setText("Contact: " + contact);
                                            saveData("newContact", contact);
                                            Log.d(TAG, "onComplete: contact save:" + contact);
                                            Toast.makeText(adminPage.this, "Contact: " + contact, Toast.LENGTH_SHORT).show();
                                            update(cInfo, restaurantId);
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                        Toast.makeText(adminPage.this, "No Record Found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(adminPage.this,"No ticket to call!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteBatch batch = db.batch();
                batch.update(aInfo, "current", 0);
                batch.update(aInfo, "waiting", 0);
                batch.update(aInfo, "issued", 0);
                batch.update(bInfo, "current", 0);
                batch.update(bInfo, "waiting", 0);
                batch.update(bInfo, "issued", 0);
                batch.update(cInfo, "current", 0);
                batch.update(cInfo, "waiting", 0);
                batch.update(cInfo, "issued", 0);
                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       Toast.makeText(adminPage.this,"Reset successfully!",Toast.LENGTH_SHORT).show();
                    }
                });
                db.collection("restaurant").document(restaurantId).collection("queueRecord")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        String docId = document.getId();
                                        db.collection("restaurant").document(restaurantId).collection("queueRecord").document(docId)
                                                .delete();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });
    }

    private void update(DocumentReference change, String restaurantId) {
        Log.d(TAG, "update: called");
        String contact = loadData("newContact");
        DocumentReference record = db.collection("restaurant").document(restaurantId).collection("queueRecord").document(contact);
        WriteBatch batch = db.batch();
        batch.update(change, "current", FieldValue.increment(1));
        batch.update(change, "waiting", FieldValue.increment(-1));
        batch.delete(record);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(adminPage.this, "Called successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        db.collection("restaurant")
                .whereEqualTo("restaurantOwner", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getString("restaurantId");
                                txtRestaurantId.setText("Restaurant ID - " + id);
                                saveData("restaurantId", id);
                                listenQueue(id);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void listenQueue(String id) {
        db.collection("restaurant").document(id).collection("queueInfo")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            String waiting = doc.getLong("waiting").toString();
                            String issued = doc.getLong("issued").toString();
                            String current = doc.getLong("current").toString();
                            String identifier = doc.getString("identifier");
                            switch (identifier) {
                                case "slotA":
                                    txtSlotAWaiting.setText("Waiting: " + waiting);
                                    txtSlotACurrent.setText("Current: " + current);
                                    txtSlotAIssued.setText("Issued :" + issued);
                                    saveData("slotACurrent", current);
                                    saveData("slotAWaiting", waiting);
                                case "slotB":
                                    txtSlotBCurrent.setText("Current: " + current);
                                    txtSlotBIssued.setText("Issued: " + issued);
                                    txtSlotBWaiting.setText("Waiting: " + waiting);
                                    saveData("slotBCurrent", current);
                                    saveData("slotBWaiting", waiting);
                                case "slotC":
                                    txtSlotCCurrent.setText("Current: " + current);
                                    txtSlotCIssued.setText("Issued: " + issued);
                                    txtSlotCWaiting.setText("Waiting: " + waiting);
                                    saveData("slotCCurrent", current);
                                    saveData("slotCWaiting", waiting);
                            }
                        }
                        Log.d(TAG, "queueUpdated");
                    }
                });
    }

    private void saveData(String key, String value) {
        SharedPreferences data = getSharedPreferences("adminData", MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);
        editor.commit();
        Log.i(TAG, "data saved" + key + value);
    }

    private String loadData(String key) {
        SharedPreferences data = getSharedPreferences("adminData", MODE_PRIVATE);
        String value = data.getString(key, "0");
        Log.i(TAG, "loadData: " + key + value);
        return value;
    }
}