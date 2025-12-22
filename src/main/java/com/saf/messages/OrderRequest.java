package com.saf.messages;

import com.saf.core.Message;

public class OrderRequest implements Message {
    // Utilise EXACTEMENT ces noms là
    private String dishName;
    private String customerName;

    public OrderRequest() {}

    public OrderRequest(String dishName, String customerName) {
        this.dishName = dishName;
        this.customerName = customerName;
    }

    // Vérifie bien que les getters correspondent : getDishName() et getCustomerName()
    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}