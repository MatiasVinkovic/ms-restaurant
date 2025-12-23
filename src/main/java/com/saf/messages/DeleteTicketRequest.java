package com.saf.messages;

import com.saf.core.Message;

public class DeleteTicketRequest implements Message {
    private String ticketId;

    public DeleteTicketRequest() {}

    public DeleteTicketRequest(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
}
