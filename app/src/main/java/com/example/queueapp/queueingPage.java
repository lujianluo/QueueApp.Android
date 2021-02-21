package com.example.queueapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

    protected void onStart() {
        super.onStart();
        fbLoadData();
        initViews();
        initListener();
        createNotificationChannel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queueing_page);
        receiveProps();
    }

    public static void actionStart(Context context, String restaurantId, String contact) {
        Intent intent = new Intent(context, queueingPage.class);
        intent.putExtra("restaurantId", restaurantId);
        intent.putExtra("contact", contact);
        context.startActivity(intent);
    }

    private void receiveProps() {
        Intent intent = getIntent();
        restaurantId = intent.getStringExtra("restaurantId");
        contact = intent.getStringExtra("contact");
    }

    private void initViews() {
        txtSlot = findViewById(R.id.txtSlot);
        txtNumber = findViewById(R.id.txtNumber);
        txtPhone = findViewById(R.id.txtPhone);
        txtPhone.setText("Contact:" + contact);
        txtCurrent = findViewById(R.id.txtCurrent);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void saveStringData(String key, String value) {
        SharedPreferences stringData = getSharedPreferences("stringData", MODE_PRIVATE);
        SharedPreferences.Editor editor = stringData.edit();
        editor.putString(key, value);
        editor.commit();
        Log.i(TAG, "data saved");
    }

    private String loadStringData(String key) {
        SharedPreferences stringData = getSharedPreferences("stringData", MODE_PRIVATE);
        String data = stringData.getString(key, "0");
        return data;
    }

    private void initListener() {
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

    private void fbLoadData() {
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
                        saveStringData("number", number);
                        txtSlot.setText("Your Slot: "  + identifier);
                        txtNumber.setText("Your Number: " + number);
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
                            txtCurrent.setText("Now Calling: "  + current);
                            saveStringData("current", current);
                            compare(current);
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }
    private void compare(String current){
        int num = Integer.parseInt(loadStringData("number"));
        int now = Integer.parseInt(loadStringData("current"));
        if (num-now<10){
            notification("Less than 10 tables ahead, we are calling your number soon");
        } else if (num-now<5){
            notification("Less than 5 tables ahead, we are calling your number soon");
        } else if (num==now){
            notification("you are called, present your App screen for validating!");
        }
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "testingChannel";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("0", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void notification(String text){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(getApplicationContext(), queueingPage.class);
        intent.putExtra("restaurantId", restaurantId);
        intent.putExtra("contact", contact);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "0")
                .setSmallIcon(R.drawable.message_icon)
                .setContentTitle("Queue Updates")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(0, builder.build());
    }
}
