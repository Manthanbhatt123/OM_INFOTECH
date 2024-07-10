package com.example.myapplication.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.myapplication.repository.OrderRepo;

public class DeliveryViewModel extends ViewModel {
   public static OrderRepo orderRepo;

   public DeliveryViewModel(OrderRepo orderRepo) {
      this.orderRepo = orderRepo;
   }

   public void updateOrder(String damageType,String collectedCost ,String img_url,String consignment_status,String order_status,String order_id) {
      orderRepo.updateOrderList(damageType,collectedCost,img_url,consignment_status,order_status,order_id);
   }

}
