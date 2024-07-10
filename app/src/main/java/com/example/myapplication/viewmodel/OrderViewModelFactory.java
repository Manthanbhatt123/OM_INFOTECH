package com.example.myapplication.viewmodel;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.repository.OrderRepo;

public class OrderViewModelFactory implements ViewModelProvider.Factory {
    private final OrderRepo repository;

    public OrderViewModelFactory(OrderRepo repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrderListViewModel.class)) {
            return (T) new OrderListViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
