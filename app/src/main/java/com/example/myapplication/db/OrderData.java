package com.example.myapplication.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_table")
public class OrderData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String order_id;
    private String order_no;
    private String customer_name;
    private String latitude;
    private String longitude;
    private String address;
    private String delivery_cost;
    private String collected_cost;
    private String image_url;
    private String consignment_status;
    private String damage_type;
    private String order_status;

    public OrderData() {
        this.order_id = "";
        this.order_no = "";
        this.customer_name = "";
        this.latitude = "";
        this.longitude = "";
        this.address = "";
        this.delivery_cost = "";
        this.collected_cost = "0.00";
        this.image_url = "-";
        this.consignment_status = "-";
        this.damage_type = "None";
        this.order_status = "Pending";
    }


    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDelivery_cost() {
        return delivery_cost;
    }

    public void setDelivery_cost(String delivery_cost) {
        this.delivery_cost = delivery_cost;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getConsignment_status() {
        return consignment_status;
    }

    public void setConsignment_status(String consignment_status) {
        this.consignment_status = consignment_status;
    }

    public String getDamage_type() {
        return damage_type;
    }

    public void setDamage_type(String damage_type) {
        this.damage_type = damage_type;
    }

    public String getCollected_cost() {
        return collected_cost;
    }

    public void setCollected_cost(String collected_cost) {
        this.collected_cost = collected_cost;
    }
}

