package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemrowOrderDataListBinding;
import com.example.myapplication.db.OrderData;
import com.example.myapplication.views.OrderItemClickListner;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private List<OrderData> orders;
    private final LayoutInflater inflater;
    private final OrderItemClickListner orderItemClickListner;
    private final Context contextAdapter;

    public OrderListAdapter(Context context, List<OrderData> orders, OrderItemClickListner orderItemClickListner) {
        contextAdapter = context;
        this.orders = orders;
        this.orderItemClickListner = orderItemClickListner;
        inflater = LayoutInflater.from(context);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOrders(List<OrderData> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemrowOrderDataListBinding binding;

        public ViewHolder(ItemrowOrderDataListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemrowOrderDataListBinding binding = ItemrowOrderDataListBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (orders != null) {
            final OrderData orderData = orders.get(position);
            holder.binding.tvOrderCustomerName.setText(orderData.getCustomer_name());
            holder.binding.tvOrderId.setText(orderData.getOrder_id());
            holder.binding.tvOrderNo.setText(orderData.getOrder_no());
            holder.binding.tvOrderCustomerAddress.setText(orderData.getAddress());
            holder.binding.tvOrderDeliveryCost.setText(orderData.getDelivery_cost());
            holder.binding.btnDeliverItem.setOnClickListener(
                    v ->orderItemClickListner.onOrderItemClick(orderData)
            );
            try {
                if(orderData.getOrder_status() == null || !orderData.getOrder_status().isEmpty()) {
                    if (orderData.getOrder_status().equals("Delivered")) {
                        holder.binding.cvOrderDetails.setCardBackgroundColor(ContextCompat.getColor(contextAdapter, R.color.green));
                    }else {
                        holder.binding.cvOrderDetails.setCardBackgroundColor(ContextCompat.getColor(contextAdapter, R.color.white));
                    }
                } else {
                    holder.binding.cvOrderDetails.setCardBackgroundColor(ContextCompat.getColor(contextAdapter, R.color.white));
                }
            } catch (Exception e) {
                Log.e("OrderAdapter ", "onBindViewHolder: \n"+e.getMessage()+"\n",e );
            }
        }
    }

    @Override
    public int getItemCount() {
        if (orders != null) {
            return orders.size();
        } else {
            return 0;
        }
    }
}
