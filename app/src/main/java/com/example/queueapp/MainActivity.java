package com.example.queueapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText edtTxtrestaurantId;
    private Button btn_Enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        btn_Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEmpty()){
                    String restaurantId = edtTxtrestaurantId.getText().toString();
                    restaurantPage.actionStart(MainActivity.this, restaurantId);
                }
            }
        });
    }

    private void initViews() {
        edtTxtrestaurantId = findViewById(R.id.edtResID);
        btn_Enter = findViewById(R.id.btnEnter);
    }

    private boolean checkEmpty(){
        if (edtTxtrestaurantId.getText().toString().equals("")){
            Toast.makeText(MainActivity.this, "Can not leave RestaurantId empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }


}