package com.saf.messages;

import com.saf.core.Message;

public class TicketResponse implements Message {

    private TicketDTO ticket;
    private String message;

    public TicketResponse() {}

    public TicketResponse(TicketDTO t, String m) {
        this.ticket = t;
        this.message = m;
    }

    public TicketDTO getTicket() {
        return ticket;
    }

    public String getMessage() {
        return message;
    }
}
