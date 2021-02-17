package com.example.queueapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class restaurantPage extends AppCompatActivity {

    private TextView tv_restaurantId;
    String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_page);
        receiveData();
        tv_restaurantId = findViewById(R.id.restaurantId);
        tv_restaurantId.setText(restaurantId);
    }

    public static void actionStart(Context context, String data){
        Intent intent = new Intent(context, restaurantPage.class);
        intent.putExtra("RestaurantId", data);
        context.startActivity(intent);
    }
    public void receiveData(){
        Intent intent = getIntent();
        this.restaurantId = intent.getStringExtra("RestaurantId");
    }
    public void fbLoadData(){

    }
}