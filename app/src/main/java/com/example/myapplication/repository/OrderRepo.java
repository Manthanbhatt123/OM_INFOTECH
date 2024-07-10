package com.example.myapplication.repository;

import android.content.Context;

//import com.example.myapplication.db.AppDataBase;
import com.example.myapplication.db.AppDataBase;
import com.example.myapplication.model.OrderList;
import com.example.myapplication.network.NetworkService;
import com.example.myapplication.utils.RetrofitClient;
import com.example.myapplication.views.OrderListCallBack;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepo {
    private final NetworkService networkService = RetrofitClient.getRetrofitInstance().create(NetworkService.class);

    public void getOrderList(OrderListCallBack callBack , Context context) {
        AppDataBase db = AppDataBase.getInstance(context);

        if (db.orderDao().getAllOrders().isEmpty()) {
            networkService.getOrderData().enqueue(new Callback<OrderList>() {
                @Override
                public void onResponse(Call<OrderList> call, Response<OrderList> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        db.orderDao().insertOrders(response.body().getOrderList());
                        callBack.onSuccess(response.body().getOrderList());
                    }
                }

                @Override
                public void onFailure(Call<OrderList> call, Throwable t) {
                    callBack.onFailure(t.getMessage());
                }
            });
        } else {
            callBack.onSuccess(db.orderDao().getAllOrders());
        }
    }
}
