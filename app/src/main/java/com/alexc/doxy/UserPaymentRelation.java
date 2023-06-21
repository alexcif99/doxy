package com.alexc.doxy;

public class UserPaymentRelation {
    private int userId;
    private int paymentId;

    public UserPaymentRelation(int userId, int paymentId) {
        this.userId = userId;
        this.paymentId = paymentId;
    }

    public int getUserId() {
        return userId;
    }

    public int getPaymentId() {
        return paymentId;
    }
}
