package com.alexc.doxy;

public class UserDebtor {
    private int id;
    private int user_id;
    private int payment_id;
    private String username;
    private double amount;

    // Constructor
    public UserDebtor(int id, int user_id, int payment_id, String username, Double amount) {
        this.id = id;
        this.user_id = user_id;
        this.payment_id = payment_id;
        this.username = username;
        this.amount = amount;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getPayment_id() {
        return payment_id;
    }

    public double getAmount() {
        return amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setPayment_id(int payment_id) {
        this.payment_id = payment_id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
