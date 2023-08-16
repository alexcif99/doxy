package com.alexc.doxy;

public class Transaction {
    private int id;
    private PaymentGroup payment_group;
    private User user;
    private User user_to_pay;
    private double amount;

    public Transaction(int id, PaymentGroup payment_group, User user, User user_to_pay, double amount) {
        this.id = id;
        this.payment_group = payment_group;
        this.user = user;
        this.user_to_pay = user_to_pay;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PaymentGroup getPayment_group() {
        return payment_group;
    }

    public void setPayment_group(PaymentGroup payment_group_id) {
        this.payment_group = payment_group_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser_to_pay() {
        return user_to_pay;
    }

    public void setUser_to_pay(User user_to_pay) {
        this.user_to_pay = user_to_pay;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
