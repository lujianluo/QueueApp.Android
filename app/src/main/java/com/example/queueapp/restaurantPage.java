package com.example.queueapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
//    SharedPreferences store = getPreferences(MODE_PRIVATE);

    private static final String TAG = "restaurantPage";
    private TextView txtRestaurantName;
    private String restaurantId;
    private String selectedSlot;
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
        initSpinner();
    }
    public void initListener(){
        btnQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slotSpinner.getVisibility() ==  View.INVISIBLE) {
                    edtTxtPhone.setVisibility(View.VISIBLE);
                    slotSpinner.setVisibility(View.VISIBLE);
                    txtInstruction.setText("Enter your phone number and select you preferred slot" + System.lineSeparator() + "Then press queue button one more time!");
                    txtInstruction.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getBaseContext(), "btn clicked", Toast.LENGTH_SHORT).show();
                    if (isValidate(false)) {
                        txtInstruction.setText("queue success");
                    }
                }

            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTxtPhone.getVisibility() == View.INVISIBLE) {
                    edtTxtPhone.setVisibility(View.VISIBLE);
                    txtInstruction.setText("Enter your phone number" + System.lineSeparator() + "Then press 'Check' button one more time!");
                    txtInstruction.setVisibility(View.VISIBLE);
                } else {
                    if (isValidate(true)){
                        txtInstruction.setText("check success");
                    }
                }
            }
        });
    }
    public boolean isValidate(boolean check){
        if (check == false) {
            String selected = slotSpinner.getSelectedItem().toString();
            if (selected == null) {
                txtInstruction.setText("Please select your preferred slot");
                return false;
            }
        }
        String phone = edtTxtPhone.getText().toString();
        boolean isNumber = android.text.TextUtils.isDigitsOnly(phone);
        int length = phone.length();
        if (isNumber == true && length == 8) {
            return true;
        } else {
            if (length == 0) {
                txtInstruction.setText("Please enter phone number!");
                return false;
            } else if (length != 8) {
                txtInstruction.setText("Please enter 8 charaters phone number!");
                return false;
            } else if (isNumber == false) {
                txtInstruction.setText("Accept number only!");
                return false;
            }
            return false;
        }
    }
    public void initSpinner (){
        slotSpinner = findViewById(R.id.slotSpinner);
        final ArrayList <String> slots = new ArrayList<>();
        slots.add("SlotA");
        slots.add("SlotB");
        slots.add("SlotC");
        ArrayAdapter<String> slotsAdapter = new ArrayAdapter<>(
                restaurantPage.this,
                android.R.layout.simple_spinner_dropdown_item,
                slots
        );
        slotsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slotSpinner.setAdapter(slotsAdapter);
//        slotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedSlot = slots.get(position);
//                s
//                store.edit().putString("selectedSlot", selectedSlot);
//                store.edit().commit();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
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