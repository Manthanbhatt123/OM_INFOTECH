package com.example.myapplication.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.myapplication.repository.OrderRepo;
import com.example.myapplication.views.OrderListCallBack;

public class OrderListViewModel extends ViewModel {
    private final OrderRepo repository;

    // Constructor
    public OrderListViewModel(OrderRepo repo) {
        this.repository = repo;
    }

    public void fetchOrderList(OrderListCallBack callback) {
        repository.getOrderList(callback);
    }

}