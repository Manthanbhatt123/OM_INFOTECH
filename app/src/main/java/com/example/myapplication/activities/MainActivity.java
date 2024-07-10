package com.example.myapplication.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.example.myapplication.adapter.OrderListAdapter;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.db.OrderData;
import com.example.myapplication.repository.OrderRepo;
import com.example.myapplication.viewmodel.OrderListViewModel;
import com.example.myapplication.viewmodel.OrderViewModelFactory;
import com.example.myapplication.views.OrderItemClickListner;
import com.example.myapplication.views.OrderListCallBack;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OrderListCallBack, OrderItemClickListner {

    ActivityMainBinding binding;
    private OrderListViewModel orderListViewModel;
    private OrderListAdapter orderListAdapter;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;


    private List<OrderData> orderDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initUI();
    }

    private void initUI() {
        getCurrentLocation();
        setupViewModel();
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLocation = location;
                            Log.e("CurrentLocation", "getCurrentLocation: "+currentLocation );
                        }
                    });
        }
    }

    private void setupAdapter() {
        orderListAdapter = new OrderListAdapter(this, orderDataList,this);
        binding.rvOrderList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderList.setAdapter(orderListAdapter);
    }

    private void setupViewModel() {
        OrderRepo repository = new OrderRepo();
        OrderViewModelFactory factory = new OrderViewModelFactory(repository);
        orderListViewModel = new ViewModelProvider(this, factory).get(OrderListViewModel.class);
        orderListViewModel.fetchOrderList(this,this);

    }

    @Override
    public void onSuccess(List<OrderData> body) {
        if (!body.isEmpty()) {
            orderDataList = body;
            setupAdapter();
        }
        Log.e("OrderData", "onSuccess: "+ body );
    }

    @Override
    public void onFailure(String e) {
        Log.e("OrderData", "onFailure: "+ e);
    }

    @Override
    public void onOrderItemClick(OrderData orderData) {

        if (currentLocation != null) {
            Location orderLocation = new Location("");
            double lat = Double.parseDouble(orderData.getLatitude());
            double lng = Double.parseDouble(orderData.getLongitude());
            orderLocation.setLatitude(lat);
            orderLocation.setLongitude(lng);

            float distance = currentLocation.distanceTo(orderLocation);

            if (distance < 500000) {
                Intent intent = new Intent(this, DeliveryActivity.class);
                intent.putExtra("orderId",orderData.getOrder_id());
                intent.putExtra("orderName",orderData.getOrder_no());
                intent.putExtra("orderAddress",orderData.getAddress());
                intent.putExtra("orderLat",orderData.getLongitude());
                intent.putExtra("orderLng",orderData.getLatitude());
                intent.putExtra("orderCustomerName",orderData.getCustomer_name());
                startActivity(intent);
               Toast.makeText(this, "Location is within 50 meters", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location is too far (" + distance + " meters)", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Current location is not available", Toast.LENGTH_SHORT).show();
        }

        Log.e("OrderSpecific", "onOrderItemClick: "+
                "\n" +
                "Latitude:"+orderData.getLatitude() +
                "\n" +
                "Longitude:"+orderData.getLongitude());
    }
}