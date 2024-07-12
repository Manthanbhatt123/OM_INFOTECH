package com.example.myapplication.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.myapplication.db.AppDataBase;
import com.example.myapplication.model.OrderList;
import com.example.myapplication.network.NetworkService;
import com.example.myapplication.utils.RetrofitClient;
import com.example.myapplication.views.OrderListCallBack;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepo {
    private final NetworkService networkService = RetrofitClient.getRetrofitInstance().create(NetworkService.class);
    AppDataBase db;
    public OrderRepo(Context context){
       db = AppDataBase.getInstance(context);
    }

    public void getOrderList(OrderListCallBack callBack ) {

        if (db.orderDao().getAllOrders().isEmpty()) {
            networkService.getOrderData().enqueue(new Callback<OrderList>() {
                @Override
                public void onResponse(@NonNull Call<OrderList> call, @NonNull Response<OrderList> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        db.orderDao().insertOrders(response.body().getOrderList());
                        callBack.onSuccess(response.body().getOrderList());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<OrderList> call, @NonNull Throwable t) {
                    callBack.onFailure(t.getMessage());
                }
            });
        } else {
            callBack.onSuccess(db.orderDao().getAllOrders());
        }
    }

    public void updateOrderList(String damageType,String collectedCost,String img_url,String consignment_status,String order_status,String order_id){
        db.orderDao().updateOrder(damageType,collectedCost,img_url, consignment_status,order_status,order_id);
    }
}
