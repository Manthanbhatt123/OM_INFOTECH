package com.example.myapplication.views;

import com.example.myapplication.db.OrderData;

import java.util.List;

public interface OrderListCallBack {
    void onSuccess(List<OrderData> body);

    void onFailure(String e);
}
