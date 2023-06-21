package com.alexc.doxy;

public class Payment {
    private int id;
    private String title;
    private String description;
    private double amount;
    private int ownerUserId;

    public Payment(int id, String title, String description, double amount, int ownerUserId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.ownerUserId = ownerUserId;
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

    public double getAmount() {
        return amount;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }
}

