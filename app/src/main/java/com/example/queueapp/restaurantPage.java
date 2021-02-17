package com.example.queueapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class restaurantPage extends AppCompatActivity {

    private static final String TAG = "restaurantPage";
    private TextView txtRestaurantName;
    private RecyclerView displayView;
    private String restaurantId;
    private TextView txtSlotAPax;
    private TextView txtSlotACurrent;
    private TextView txtSlotAWaiting;
    private TextView txtSlotBPax;
    private TextView txtSlotBCurrent;
    private TextView txtSlotBWaiting;
    private TextView txtSlotCPax;
    private TextView txtSlotCCurrent;
    private TextView txtSlotCWaiting;

    @Override
    protected void onStart(){
        super.onStart();
        fbLoadData();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_page);
        initViews();
        receiveProps();
    }
    private void initViews(){
        txtRestaurantName = findViewById(R.id.restaurantName);
        txtSlotAPax = findViewById(R.id.slotAPax);
        txtSlotACurrent = findViewById(R.id.slotACurrent);
        txtSlotAWaiting = findViewById(R.id.slotAWaiting);
        txtSlotBPax = findViewById(R.id.slotBPax);
        txtSlotBCurrent = findViewById(R.id.slotBCurrent);
        txtSlotBWaiting = findViewById(R.id.slotBWaiting);
        txtSlotCPax = findViewById(R.id.slotCPax);
        txtSlotCCurrent = findViewById(R.id.slotCCurrent);
        txtSlotCWaiting = findViewById(R.id.slotCWaiting);

    }
    public static void actionStart(Context context, String data){
        Intent intent = new Intent(context, restaurantPage.class);
        intent.putExtra("RestaurantId", data);
        context.startActivity(intent);
    }
    public void receiveProps(){
        Intent intent = getIntent();
        restaurantId = intent.getStringExtra("RestaurantId");
    }
    public void fbLoadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference restaurantInfo = db.collection("Restaurant").document(restaurantId);
        restaurantInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String name = document.getString("RestaurantName");
                        txtRestaurantName.setText(restaurantId + " - " + name);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        CollectionReference queueInfo = db.collection("Restaurant").document(restaurantId).collection("QueueInfo");
        queueInfo.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot doc : value) {
                       switch (doc.getString("Identifier")) {
                           case "SlotA":
                               txtSlotACurrent.setText(doc.getLong("Current").toString());
                               txtSlotAWaiting.setText(doc.getLong("Waiting").toString());
                           case "SlotB":
                               txtSlotBCurrent.setText(doc.getLong("Current").toString());
                               txtSlotBWaiting.setText(doc.getLong("Waiting").toString());
                           case "SlotC":
                               txtSlotCCurrent.setText(doc.getLong("Current").toString());
                               txtSlotCWaiting.setText(doc.getLong("Waiting").toString());
                       }
                }
            }
        });

        CollectionReference queueSetting = db.collection("Restaurant").document(restaurantId).collection("QueueSetting");
        queueSetting.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot doc : value) {
                    String min = doc.getLong("MinPax").toString();
                    String max = doc.getLong("MaxPax").toString();
                    switch (doc.getString("Identifier")) {
                        case "SlotA":
                            txtSlotAPax.setText("SlotA - " + min + "-" + max + " Pax");
                        case "SlotB":
                            txtSlotBPax.setText("SlotB - " + min + "-" + max + " Pax");
                        case "SlotC":
                            txtSlotCPax.setText("SlotC - " + min + "-" + max + " Pax");
                    }
                }
            }
        });
    }
}