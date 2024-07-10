package com.example.myapplication.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityDeliveryBinding;
import com.example.myapplication.db.OrderData;

public class DeliveryActivity extends AppCompatActivity {
    ActivityDeliveryBinding binding;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityDeliveryBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        data = getIntent().getStringExtra("orderId");
        Log.e("OrderData_del", "onCreate: " + data);
    }
}