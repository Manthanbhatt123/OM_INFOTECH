package com.example.myapplication.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FilterOrderListBinding;
import com.example.myapplication.databinding.SortListDialogBinding;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OrderListCallBack, OrderItemClickListner {

    ActivityMainBinding binding;
    SortListDialogBinding sortListDialogBinding;
    FilterOrderListBinding filterOrderListBinding;
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
        setContentView(binding.getRoot());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initUI();
    }

    private void initUI() {
        getCurrentLocation();
        setupViewModel();
        onButtonCLick();

    }
    private void onButtonCLick() {
        binding.tvSortList.setOnClickListener(v -> {
            if (orderDataList != null) {
                sortListDialog();
            }
        });
        
        binding.tvFilterList.setOnClickListener(v -> {
         if (orderDataList != null){
             filterListDialog();
         }
        });
        
    }

    private void filterListDialog() {
        filterOrderListBinding = FilterOrderListBinding.inflate(getLayoutInflater());
        AlertDialog filterDialog = new AlertDialog.Builder(this).create();
        filterDialog.setTitle("Filter List By:");
        filterDialog.setView(filterOrderListBinding.getRoot());

        filterOrderListBinding.rgFilterList.setOnCheckedChangeListener((radioGroup, i) -> {
         if (i == R.id.rbDelivered){
             filterListItem(getString(R.string.delivered));
         } else if (i == R.id.rbPending){
            filterListItem(getString(R.string.pending));
         } else if (i == R.id.rbAll){
             orderListAdapter.setOrders(orderDataList);
         }
         filterDialog.dismiss();
        });
        filterDialog.show();
    }

    private void filterListItem(String status) {
        List<OrderData> orders = new ArrayList<>();
//        orders.clear();
        for (OrderData orderData:orderDataList) {
            if (orderData.getOrder_status() != null && status.equals(getString(R.string.delivered))) {
                if(orderData.getOrder_status().equals(status)){
                    orders.add(orderData);
                    orderListAdapter.setOrders(orders);
                }
            } else {
                if (orderData.getOrder_status().isEmpty()){
                    orders.add(orderData);
                    orderListAdapter.setOrders(orders);
                }
            }
        }
    }

    private void sortListDialog() {
        sortListDialogBinding = SortListDialogBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Sort List By:")
                .setView(sortListDialogBinding.getRoot())
                .create();
        sortListDialogBinding.rgSortList.setOnCheckedChangeListener((radioGroup, i) -> {
          if (i == R.id.rbName){
              orderDataList.sort(Comparator.comparing(OrderData::getCustomer_name));
              orderListAdapter.notifyDataSetChanged();
          } else if (i == R.id.rbPrice){
              orderDataList.sort(Comparator.comparing(OrderData::getDelivery_cost));
              orderListAdapter.notifyDataSetChanged();
          } else if (i == R.id.rbOrderId){
              orderDataList.sort(Comparator.comparing(OrderData::getOrder_id));
              orderListAdapter.notifyDataSetChanged();
          }
          dialog.dismiss();
        });
        dialog.show();
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
        OrderRepo repository = new OrderRepo(this);
        OrderViewModelFactory factory = new OrderViewModelFactory(repository);
        orderListViewModel = new ViewModelProvider(this, factory).get(OrderListViewModel.class);
    }

    @Override
    public void onSuccess(List<OrderData> body) {
        if (!body.isEmpty()) {
            orderDataList = body;
            setupAdapter();
        }
        int deliveryCount = 0;
        double totalCash = 0;
        if (orderDataList != null) {
            for (int i = 0; i < orderDataList.size(); i++) {
                if (orderDataList.get(i).getOrder_status() != null) {
                    if (orderDataList.get(i).getOrder_status().equals("Delivered")) {
                        deliveryCount++;
                    }
                }
                if (orderDataList.get(i).getCollected_cost() != null) {
                    totalCash += Double.parseDouble(orderDataList.get(i).getCollected_cost());
                }
            }
        }
        binding.tvOrderDeliveryCount.setText(String.format("%s%d/%d", getString(R.string.delivery_count), deliveryCount, orderDataList.size()));
        binding.tvOrderCashCollected.setText(String.format("%s%s", getString(R.string.total_cash_collected), totalCash));

        Log.e("OrderData---!@#", "onSuccess: "+ body +"\n"+"deliveryCount:" + deliveryCount);
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

            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.orderId),orderData.getOrder_id());
            bundle.putString(getString(R.string.orderNo),orderData.getOrder_no());
            bundle.putString(getString(R.string.orderAddress),orderData.getAddress());
            bundle.putString(getString(R.string.orderLat),orderData.getLongitude());
            bundle.putString(getString(R.string.orderLng),orderData.getLatitude());
            bundle.putString(getString(R.string.orderCustomerName),orderData.getCustomer_name());
            bundle.putString(getString(R.string.orderDeliveryCost),orderData.getDelivery_cost());
            if (distance < 500000) {
                Intent intent = new Intent(this, DeliveryActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
               Toast.makeText(this, "Location is within 50 meters", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Location Error");
                dialog.setMessage("Location is too far (" + distance + " meters)");
                dialog.setPositiveButton("OK", (dialog1, which) -> {dialog1.dismiss();});
                dialog.show();

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

    @Override
    protected void onResume() {
        super.onResume();
        orderListViewModel.fetchOrderList(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
}