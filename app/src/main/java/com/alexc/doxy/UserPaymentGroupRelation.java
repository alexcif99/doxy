package com.alexc.doxy;

public class UserPaymentGroupRelation {
    private int userId;
    private int groupPaymentId;

    public UserPaymentGroupRelation(int userId, int groupPaymentId) {
        this.userId = userId;
        this.groupPaymentId = groupPaymentId;
    }

    public int getUserId() {
        return userId;
    }

    public int getGroupPaymentId() {
        return groupPaymentId;
    }
}

