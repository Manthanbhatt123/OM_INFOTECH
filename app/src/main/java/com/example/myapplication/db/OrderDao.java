package com.example.myapplication.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrders(List<OrderData> order);

    @Query("SELECT * FROM order_table")
    List<OrderData> getAllOrders();

}
