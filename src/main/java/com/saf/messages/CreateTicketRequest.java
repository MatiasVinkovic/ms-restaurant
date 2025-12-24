package com.saf.messages;

import com.saf.core.Message;

public class CreateTicketRequest implements Message {

    private TicketCreateDTO ticket;

    public CreateTicketRequest() {}

    public CreateTicketRequest(TicketCreateDTO ticket) {
        this.ticket = ticket;
    }

    public TicketCreateDTO getTicket() {
        return ticket;
    }

    @Override
    public String toString() {
        return "Ticket created ->\n" + ticket;
    }
}
