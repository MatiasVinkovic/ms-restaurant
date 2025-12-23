package com.saf.messages;

import com.saf.core.Message;

public class TicketResponse implements Message {
    private String ticketId;
    private String status;
    private String message;

    public TicketResponse() {}

    public TicketResponse(String ticketId, String status, String message) {
        this.ticketId = ticketId;
        this.status = status;
        this.message = message;
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
