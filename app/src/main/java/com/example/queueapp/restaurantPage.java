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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Map;

public class restaurantPage extends AppCompatActivity {

    private static final String TAG = "restaurantPage";
    private TextView txtRestaurantName;
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
    private Spinner slotSpinner;
    private Button btnCheck;
    private Button btnQueue;
    private EditText edtTxtPhone;
    private TextView txtInstruction;
    private TextView txtQueueInst;
    private TextView txtCheckInst;
    private Button btnSubmit;
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onStart(){
        super.onStart();
        fbLoadData();
        initViews();
        initListener();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_page);
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
        edtTxtPhone = findViewById(R.id.edtTxtphone);
        btnCheck =findViewById(R.id.btnCheck);
        btnQueue = findViewById(R.id.btnQueue);
        edtTxtPhone = findViewById(R.id.edtTxtphone);
        txtInstruction = findViewById(R.id.txtInstruction);
        txtQueueInst = findViewById(R.id.txtQueueInst);
        txtCheckInst = findViewById(R.id.txtCheckInst);
        btnSubmit = findViewById(R.id.btnSubmit);
        initSpinner();
    }
    public void resetView(){
        txtCheckInst.setVisibility(View.VISIBLE);
        txtQueueInst.setVisibility(View.VISIBLE);
        txtInstruction.setVisibility(View.INVISIBLE);
        edtTxtPhone.setVisibility(View.INVISIBLE);
        btnSubmit.setVisibility(View.INVISIBLE);
        slotSpinner.setVisibility(View.INVISIBLE);
    }
    public void initListener(){
        btnQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView();
                txtCheckInst.setVisibility(View.INVISIBLE);
                txtQueueInst.setVisibility(View.INVISIBLE);
                edtTxtPhone.setVisibility(View.VISIBLE);
                txtInstruction.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                slotSpinner.setVisibility(View.VISIBLE);
                txtInstruction.setText("Enter your phone number" + System.lineSeparator() + "Select you preferred slot" + System.lineSeparator() + "Then press 'Submit' button!");
                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String selectedSlot = slotSpinner.getSelectedItem().toString();
                            String waitingKey = selectedSlot + "Waiting";
                            int waiting = getQueueInfo(waitingKey);
                            String limitKey = selectedSlot + "Limit";
                            int limit = getLimit(limitKey);
                            if (waiting < limit) {
                                if(isValidate(false)) {
                                    checkExistQueue(false);
                                }
                            } else {
                                txtInstruction.setText("Queue limit reach" + System.lineSeparator() + "Please select another slot" + System.lineSeparator() + "Or try again later!");
                            }
                        }
                    });
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView();
                txtQueueInst.setVisibility(View.INVISIBLE);
                txtCheckInst.setVisibility(View.INVISIBLE);
                edtTxtPhone.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                txtInstruction.setVisibility(View.VISIBLE);
                txtInstruction.setText("Enter your phone number" + System.lineSeparator() + "Then press 'Submit' button!");
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isValidate(true)){
                            checkExistQueue(true);
                        }
                    }
                });
            }
        });
    }
    public void queueFirebase (int newNumber, String phone, String selectedSlot){
        WriteBatch batch = db.batch();
        Map<String, Object> record = new HashMap<>();
        record.put("Contact", phone);
        record.put("Identifier", selectedSlot);
        record.put("QueueNumber", newNumber);
        record.put("isCompleted", false);
        DocumentReference infoRef = db.collection("Restaurant").document(restaurantId).collection("QueueInfo").document(selectedSlot);
        DocumentReference recordRef = db.collection("Restaurant").document(restaurantId).collection("QueueRecord").document(phone);
        batch.set(recordRef, record);
        batch.update(infoRef, "Waiting", FieldValue.increment(1));
        batch.update(infoRef, "Issued", FieldValue.increment(1));
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Upload success");
                txtInstruction.setText("Get ticket Success!");
            }
        });
    }
    public void saveQueueInfo(String key, int value){
        SharedPreferences queueInfo = getSharedPreferences("queueInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = queueInfo.edit();
        editor.putInt (key, value);
        editor.apply();
        Log.i(TAG,"queueInfo saved");
    }
    public void saveQueueSetting (String key, int value){
        SharedPreferences queueSetting = getSharedPreferences("queueSetting", MODE_PRIVATE);
        SharedPreferences.Editor editor = queueSetting.edit();
        editor.putInt(key, value);
        editor.apply();
        Log.i(TAG, "queueSetting saved");
    }
    public int getQueueInfo(String key){
        SharedPreferences queueInfo = getSharedPreferences("queueInfo", MODE_PRIVATE);
        int issued = queueInfo.getInt(key, 0);
        return issued;
    }
    public int getLimit(String key){
        SharedPreferences queueSetting = getSharedPreferences("queueSetting", MODE_PRIVATE);
        int limit = queueSetting.getInt(key, 0);
        return limit;
    }
    public void updateView(){
        SharedPreferences queueInfo = getSharedPreferences("queueInfo", MODE_PRIVATE);
        String slotACurrent = String.valueOf(queueInfo.getInt("slotACurrent",0));
        txtSlotACurrent.setText("Current: " + slotACurrent);
        String slotBCurrent = String.valueOf(queueInfo.getInt("slotBCurrent",0));
        txtSlotBCurrent.setText("Current: " + slotBCurrent);
        String slotCCurrent = String.valueOf(queueInfo.getInt("slotCCurrent",0));
        txtSlotCCurrent.setText("Current: " + slotCCurrent);
        String slotAWaiting = String.valueOf(queueInfo.getInt("slotAWaiting", 0));
        txtSlotAWaiting.setText("Waiting: " + slotAWaiting);
        String slotBWaiting = String.valueOf(queueInfo.getInt("slotBWaiting", 0));
        txtSlotBWaiting.setText("Waiting: " + slotBWaiting);
        String slotCWaiting = String.valueOf(queueInfo.getInt("slotCWaiting", 0));
        txtSlotCWaiting.setText("Waiting: " + slotCWaiting);
        Log.i(TAG, "View update success");
    }
    public void checkExistQueue (boolean isQueueing){
        Log.i(TAG, "checking Existing Queue");
        String phone =edtTxtPhone.getText().toString();
        db.collection("Restaurant").document(restaurantId).collection("QueueRecord")
                .whereEqualTo("Contact", phone).whereEqualTo("isCompleted", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: is successful");
                            QuerySnapshot document = task.getResult();
                            String phone = edtTxtPhone.getText().toString();
                            if (document.isEmpty()){
                                if(!isQueueing) {
                                    String selectedSlot = slotSpinner.getSelectedItem().toString();
                                    String newSlot = selectedSlot.substring(0, 1).toUpperCase() + selectedSlot.substring(1);
                                    String issuedKey = selectedSlot + "Issued";
                                    int issued = getQueueInfo(issuedKey);
                                    int newNumber = issued += 1;
                                    queueFirebase(newNumber, phone, newSlot);
                                    queueingPage.actionStart(restaurantPage.this, restaurantId, phone);
                                } else {
                                    txtInstruction.setText("No record found, please get a ticket first!");
                                }
                            } else {
                                    queueingPage.actionStart(restaurantPage.this, restaurantId, phone);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public boolean isValidate(boolean check){
        Log.d(TAG, "isValidate: try validate");
        if (!check) {
            String selected = slotSpinner.getSelectedItem().toString();
            if (selected == null) {
                txtInstruction.setText("Please select your preferred slot");
                return false;
            }
        }
        String phone = edtTxtPhone.getText().toString();
        boolean isNumber = android.text.TextUtils.isDigitsOnly(phone);
        int length = phone.length();
        if (isNumber && length == 8) {
            return true;
        } else {
            if (length == 0) {
                txtInstruction.setText("Please enter phone number!");
                return false;
            } else if (length != 8) {
                txtInstruction.setText("Please enter 8 charaters phone number!");
                return false;
            } else if (!isNumber) {
                txtInstruction.setText("Accept number only!");
                return false;
            }
            return false;
        }
    }
    public void initSpinner (){
        slotSpinner = findViewById(R.id.slotSpinner);
        final ArrayList <String> slots = new ArrayList<>();
        slots.add("slotA");
        slots.add("slotB");
        slots.add("slotC");
        ArrayAdapter<String> slotsAdapter = new ArrayAdapter<>(
                restaurantPage.this,
                android.R.layout.simple_spinner_dropdown_item,
                slots
        );
        slotsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slotSpinner.setAdapter(slotsAdapter);
    }

    public static void actionStart(Context context, String restaurantId){
        Intent intent = new Intent(context, restaurantPage.class);
        intent.putExtra("restaurantId", restaurantId);
        context.startActivity(intent);
    }
    public void receiveProps(){
        Intent intent = getIntent();
        restaurantId = intent.getStringExtra("restaurantId");
    }
    public void fbLoadData() {
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
                    int Current = doc.getLong("Current").intValue();
                    int Waiting = doc.getLong("Waiting").intValue();
                    int Issued = doc.getLong("Issued").intValue();
                    switch (doc.getString("Identifier")) {
                           case "SlotA":
                               saveQueueInfo("slotACurrent", Current);
                               saveQueueInfo("slotAWaiting", Waiting);
                               saveQueueInfo("slotAIssued", Issued);
                           case "SlotB":
                               saveQueueInfo("slotBCurrent", Current);
                               saveQueueInfo("slotBWaiting", Waiting);
                               saveQueueInfo("slotBIssued", Issued);

                        case "SlotC":
                               saveQueueInfo("slotCCurrent", Current);
                               saveQueueInfo("slotCWaiting", Waiting);
                            saveQueueInfo("slotCIssued", Issued);

                    }
                    updateView();
                }
            }
        });

        CollectionReference queueSetting = db.collection("Restaurant").document(restaurantId).collection("QueueSetting");
//        queueSetting.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e);
//                    return;
//                }
//                for (QueryDocumentSnapshot doc : value) {
//                    String min = doc.getLong("MinPax").toString();
//                    String max = doc.getLong("MaxPax").toString();
//                    switch (doc.getString("Identifier")) {
//                        case "SlotA":
//                            txtSlotAPax.setText("SlotA - " + min + "-" + max + " Pax");
//                        case "SlotB":
//                            txtSlotBPax.setText("SlotB - " + min + "-" + max + " Pax");
//                        case "SlotC":
//                            txtSlotCPax.setText("SlotC - " + min + "-" + max + " Pax");
//                    }
//                }
//            }
//        });
                queueSetting.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String min = document.getLong("MinPax").toString();
                                String max = document.getLong("MaxPax").toString();
                                int limit = document.getLong("QueueLimit").intValue();
                                switch (document.getString("Identifier")) {
                                    case "SlotA":
                                        txtSlotAPax.setText("SlotA - " + min + "-" + max + " Pax");
                                        saveQueueSetting("slotALimit", limit);
                                    case "SlotB":
                                        txtSlotBPax.setText("SlotB - " + min + "-" + max + " Pax");
                                        saveQueueSetting("slotBLimit", limit);
                                    case "SlotC":
                                        txtSlotCPax.setText("SlotC - " + min + "-" + max + " Pax");
                                        saveQueueSetting("slotCLimit", limit);
                                }
                                Log.d(TAG, "queue setting loaded");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}