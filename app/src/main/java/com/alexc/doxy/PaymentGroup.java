package com.alexc.doxy;

public class PaymentGroup {
    private int id;
    private String title;
    private String description;

    public PaymentGroup(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

