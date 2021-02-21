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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public class settingPage extends AppCompatActivity {
    private EditText edtTxtretaurantName;
    private EditText edtTxtslotAMin;
    private EditText edtTxtslotAMax;
    private EditText edtTxtslotALimit;
    private EditText edtTxtslotBMin;
    private EditText edtTxtslotBMax;
    private EditText edtTxtslotBLimit;
    private EditText edtTxtslotCMin;
    private EditText edtTxtslotCMax;
    private EditText edtTxtslotCLimit;
    private Button btnSettingCancel;
    private Button btnSettingSave;
    private static final String TAG = "settingPage";
    private boolean firstLogin;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);
        receiveProps();
        initView();
        initListener();
    }

    public static void actionStart(Context context, boolean firstLogin) {
        Intent intent = new Intent(context, settingPage.class);
        intent.putExtra("firstLogin", firstLogin);
        context.startActivity(intent);
    }

    private void receiveProps() {
        Intent intent = getIntent();
        firstLogin = intent.getBooleanExtra("firstLogin", true);
    }

    private void initView() {
        edtTxtretaurantName = findViewById(R.id.edtTxtRestaurantNameSetting);
        edtTxtslotAMin = findViewById(R.id.edtTxtSlotAMinSetting);
        edtTxtslotAMax = findViewById(R.id.edtTxtSlotAMaxSetting);
        edtTxtslotALimit = findViewById(R.id.edtTxtSlotALimitSetting);
        edtTxtslotBMin = findViewById(R.id.edtTxtSlotBMinSetting);
        edtTxtslotBMax = findViewById(R.id.edtTxtSlotBMaxSetting);
        edtTxtslotBLimit = findViewById(R.id.edtTxtSlotBLimitSetting);
        edtTxtslotCMin = findViewById(R.id.edtTxtSlotCMinSetting);
        edtTxtslotCMax = findViewById(R.id.edtTxtSlotCMaxSetting);
        edtTxtslotCLimit = findViewById(R.id.edtTxtSlotCLimitSetting);
        btnSettingCancel = findViewById(R.id.btnSettingCancel);
        btnSettingSave = findViewById(R.id.btnSettingSave);
        if (firstLogin) {
            btnSettingCancel.setVisibility(View.INVISIBLE);
        }
        getId();
        loadSettingData();
    }

    private void initListener() {
        btnSettingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminPage.actionStart(settingPage.this);
            }
        });
        btnSettingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = edtTxtretaurantName.getText().toString();
                int newAMin = Integer.parseInt(edtTxtslotAMin.getText().toString());
                int newAMax = Integer.parseInt(edtTxtslotAMax.getText().toString());
                int newALimit = Integer.parseInt(edtTxtslotALimit.getText().toString());
                int newBMin = Integer.parseInt(edtTxtslotBMin.getText().toString());
                int newBMax = Integer.parseInt(edtTxtslotBMax.getText().toString());
                int newBLimit = Integer.parseInt(edtTxtslotBLimit.getText().toString());
                int newCMin = Integer.parseInt(edtTxtslotCMin.getText().toString());
                int newCMax = Integer.parseInt(edtTxtslotCMax.getText().toString());
                int newCLimit = Integer.parseInt(edtTxtslotCLimit.getText().toString());
                String id = loadSettingData("restaurantId");
                DocumentReference aSetting = db.collection("restaurant").document(id).collection("queueSetting").document("slotA");
                DocumentReference bSetting = db.collection("restaurant").document(id).collection("queueSetting").document("slotB");
                DocumentReference cSetting = db.collection("restaurant").document(id).collection("queueSetting").document("slotC");
                DocumentReference name = db.collection("restaurant").document(id);
                WriteBatch batch = db.batch();
                batch.set(aSetting, new setting("slotA", newAMin, newAMax, newALimit));
                batch.set(bSetting, new setting("slotB", newBMin, newBMax, newBLimit));
                batch.set(cSetting, new setting("slotC", newCMin, newCMax, newCLimit));
                batch.update(name, "restaurantName", newName);
                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(settingPage.this, "Data Saved", Toast.LENGTH_SHORT).show();
                        adminPage.actionStart(settingPage.this);
                    }
                });
            }

        });
    }

    //    private boolean isEmpty(){
//
//    }
    private void getId() {
        Log.d(TAG, "isSet: ran");
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
                            Log.d(TAG, "onComplete: search completed");
                            QuerySnapshot doc = task.getResult();
                            if (doc.isEmpty()) {
                                Log.d(TAG, "onComplete: doc found!");
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String resId = document.getString("restaurantId");
                                    String name = document.getString("restaurantName");
                                    saveSettingData("restaurantId", resId);
                                    saveSettingData("restaurantName", name);
                                    edtTxtretaurantName.setText(name);
                                    Log.d(TAG, "onComplete: doc found!");
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void loadSettingData() {
        String restaurantId = loadSettingData("restaurantId");
        CollectionReference queueSetting = db.collection("restaurant").document(restaurantId).collection("queueSetting");
        queueSetting.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String min = document.getLong("minPax").toString();
                                String max = document.getLong("maxPax").toString();
                                String limit = document.getLong("queueLimit").toString();
                                switch (document.getString("identifier")) {
                                    case "slotA":
                                        edtTxtslotAMin.setText(min);
                                        edtTxtslotAMax.setText(max);
                                        edtTxtslotALimit.setText(limit);
                                        saveSettingData("slotAMin", min);
                                        saveSettingData("slotAMax", max);
                                        saveSettingData("slotALimit", limit);
                                    case "slotB":
                                        edtTxtslotBMin.setText(min);
                                        edtTxtslotBMax.setText(max);
                                        edtTxtslotBLimit.setText(limit);
                                        saveSettingData("slotBMin", min);
                                        saveSettingData("slotBMax", max);
                                        saveSettingData("slotBLimit", limit);
                                    case "slotC":
                                        edtTxtslotCMin.setText(min);
                                        edtTxtslotCMax.setText(max);
                                        edtTxtslotCLimit.setText(limit);
                                        saveSettingData("slotCMin", min);
                                        saveSettingData("slotCMax", max);
                                        saveSettingData("slotCLimit", limit);
                                }
                                Log.d(TAG, "queue setting loaded");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void saveSettingData(String key, String value) {
        SharedPreferences adminData = getSharedPreferences("settingData", MODE_PRIVATE);
        SharedPreferences.Editor editor = adminData.edit();
        editor.putString(key, value);
        editor.commit();
        Log.i(TAG, "data saved" + key + value);
    }

    private String loadSettingData(String key) {
        SharedPreferences adminData = getSharedPreferences("settingData", MODE_PRIVATE);
        String data = adminData.getString(key, "0");
        Log.d(TAG, "data loaded: " + key + data);
        return data;
    }
}