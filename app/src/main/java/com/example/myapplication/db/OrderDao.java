package com.example.myapplication.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrders(List<OrderData> order);

    @Query("SELECT * FROM order_table")
    List<OrderData> getAllOrders();

    @Query("update order_table set collected_cost=:collectedCost, image_url = :imageUrl, " +
            "consignment_status = :consignmentStatus,order_status = :orderStatus,damage_type=:damageType" +
            " where order_id = :orderId")
    void updateOrder(String damageType,
                     String collectedCost,
                     String imageUrl,
                     String consignmentStatus,
                     String orderStatus,
                     String orderId);
}
