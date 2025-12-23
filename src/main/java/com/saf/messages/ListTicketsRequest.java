package com.saf.messages;

import com.saf.core.Message;

public class ListTicketsRequest implements Message {
    private String filter;

    public ListTicketsRequest() {}

    public ListTicketsRequest(String filter) {
        this.filter = filter;
    }

    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }
}
