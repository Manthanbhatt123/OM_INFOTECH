package com.example.myapplication.model;

import com.example.myapplication.db.OrderData;

import java.util.List;

public class OrderList {
    private List<OrderData> orderlist;

    // Getters and Setters
    public List<OrderData> getOrderList() {
        return orderlist;
    }

    public void setOrderList(List<OrderData> orderlist) {
        this.orderlist = orderlist;
    }


}
