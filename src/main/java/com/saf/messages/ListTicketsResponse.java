package com.saf.messages;

import com.saf.core.Message;
import java.util.List;

public class ListTicketsResponse implements Message {
    private List<String> tickets;
    private int count;

    public ListTicketsResponse() {}

    public ListTicketsResponse(List<String> tickets, int count) {
        this.tickets = tickets;
        this.count = count;
    }

    public List<String> getTickets() { return tickets; }
    public void setTickets(List<String> tickets) { this.tickets = tickets; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
