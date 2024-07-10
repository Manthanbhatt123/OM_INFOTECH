package com.example.myapplication.network;

import com.example.myapplication.model.OrderList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NetworkService {

    @GET("orderlist.php")
    Call<OrderList> getOrderData();
}
