package com.saf.messages;

import com.saf.core.Message;

public class OrderResponse implements Message {
    private String status;
    public OrderResponse() {}
    public OrderResponse(String status) { this.status = status; }
    public String getStatus() { return status; }
}