package com.saf.messages;

public class TicketCreateDTO {

    private String title;
    private String description;
    private TicketPriority priority;

    public TicketCreateDTO() {}

    public TicketCreateDTO(String t, String d, TicketPriority p) {
        this.title = t;
        this.description = d;
        this.priority = p;
    }

    public void setTitle(String t) {
        this.title = t;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public void setPriority(TicketPriority p) {
        this.priority = p;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "Ticket ->" +
                "\nTitle : " + title +
                "\nDescription : " + description +
                "\nPriority : " + priority;
    }
}
