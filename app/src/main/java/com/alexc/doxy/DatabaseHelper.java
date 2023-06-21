package com.alexc.doxy;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //General
    private static final String DATABASE_NAME = "doxy_db";
    private static final int DATABASE_VERSION = 6;

    // User
    public static final String TABLE_USER = "user";
    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_SURNAME = "surname";
    public static final String USER_USERNAME = "username";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";

    //Payment
    public static final String TABLE_PAYMENT = "payment";
    public static final String PAYMENT_ID = "id";
    public static final String PAYMENT_TITLE = "title";
    public static final String PAYMENT_DESCRIPTION = "description";
    public static final String PAYMENT_AMOUNT = "amount";
    public static final String PAYMENT_USER_OWNER_ID = "user_owner_id";

    //Payment group
    public static final String TABLE_PAYMENT_GROUP = "payment_group";
    public static final String PAYMENT_GROUP_ID = "id";
    public static final String PAYMENT_GROUP_TITLE = "title";
    public static final String PAYMENT_GROUP_DESCRIPTION = "description";

    //Relation PaymentGroup - User
    public static final String TABLE_REL_PG_USER = "payment_group_user_relation";
    public static final String REL_PG_USER_PAYMENT_GROUP_ID = "payment_group_id";
    public static final String REL_PG_USER_USER_ID = "user_id";

    //Relaion User - Payment
    public static final String TABLE_REL_USER_PAYMENT = "user_payment_relation";
    public static final String REL_USER_PAYMENT_USER_ID = "user_id";
    public static final String REL_USER_PAYMENT_PAYMENT_ID = "payment_id";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // User
        String createUserTableQuery  = "CREATE TABLE " + TABLE_USER + "(" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                USER_NAME + " TEXT," +
                USER_SURNAME + " TEXT," +
                USER_USERNAME + " TEXT," +
                USER_EMAIL + " TEXT," +
                USER_PASSWORD + " TEXT" +
                ")";
        db.execSQL(createUserTableQuery );
        // Payment
        String createPaymentTableQuery = "CREATE TABLE " + TABLE_PAYMENT + "(" +
                PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PAYMENT_TITLE + " TEXT," +
                PAYMENT_DESCRIPTION + " TEXT," +
                PAYMENT_AMOUNT + " REAL," +
                PAYMENT_USER_OWNER_ID + " INTEGER," +
                "FOREIGN KEY (" + PAYMENT_USER_OWNER_ID + ") REFERENCES " +
                TABLE_USER + "(" + USER_ID + ")" +
                ")";
        db.execSQL(createPaymentTableQuery);
        // PaymentGroup
        String createPaymentGroupTableQuery = "CREATE TABLE " + TABLE_PAYMENT_GROUP + "(" +
                PAYMENT_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PAYMENT_GROUP_TITLE + " TEXT," +
                PAYMENT_GROUP_DESCRIPTION + " TEXT" +
                ")";
        db.execSQL(createPaymentGroupTableQuery);
        // Relation Payment group - User
//        query = "CREATE TABLE " + TABLE_REL_PG_USER + "(" +
//                REL_PG_USER_PAYMENT_GROUP_ID + " INTEGER," +
//                REL_PG_USER_USER_ID + " INTEGER," +
//                "FOREIGN KEY (" + REL_PG_USER_PAYMENT_GROUP_ID + ") REFERENCES " +
//                TABLE_PAYMENT_GROUP + "(" + PAYMENT_GROUP_ID + ")," +
//                "FOREIGN KEY (" + REL_PG_USER_USER_ID + ") REFERENCES " +
//                TABLE_USER + "(" + USER_ID + ")" +
//                ")";
//        db.execSQL(query);
//        // Relation User - Payment
//        query = "CREATE TABLE " + TABLE_REL_USER_PAYMENT + "(" +
//                REL_USER_PAYMENT_USER_ID + " INTEGER," +
//                REL_USER_PAYMENT_PAYMENT_ID + " INTEGER," +
//                "FOREIGN KEY (" + REL_USER_PAYMENT_USER_ID + ") REFERENCES " +
//                TABLE_USER + "(" + USER_ID + ")," +
//                "FOREIGN KEY (" + REL_USER_PAYMENT_PAYMENT_ID + ") REFERENCES " +
//                TABLE_PAYMENT + "(" + PAYMENT_ID + ")" +
//                ")";
//        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REL_PG_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REL_USER_PAYMENT);
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // USER ----------------------------------------------------------------------------------------
    public long addUser(String name, String surname, String username, String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, name);
        values.put(USER_SURNAME, surname);
        values.put(USER_USERNAME, username);
        values.put(USER_EMAIL, email);
        values.put(USER_PASSWORD, password);

        return db.insert(TABLE_USER, null, values);
    }

    @SuppressLint("Range")
    public int isUserValid(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USER_ID};
        String selection = USER_EMAIL + " = ? AND " + USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
//        boolean isValid = cursor.moveToFirst();
//        cursor.close();
//        db.close();
//        return isValid;
        int userId = -1; // Valor predeterminado en caso de que no se encuentre el usuario
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(USER_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

    public boolean isUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USER_EMAIL};
        String selection = USER_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public static Cursor getUsers(SQLiteDatabase db) {
        String[] columns = {USER_ID, USER_NAME, USER_SURNAME, USER_USERNAME, USER_EMAIL};
        return db.query(TABLE_USER, columns, null, null, null, null, null);
    }

    // PAYMENT -------------------------------------------------------------------------------------
    public long addPayment(String title, String description, double amount, long userOwnerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(PAYMENT_TITLE, title);
        values.put(PAYMENT_DESCRIPTION, description);
        values.put(PAYMENT_AMOUNT, amount);
        values.put(PAYMENT_USER_OWNER_ID, userOwnerId);

        return db.insert(TABLE_PAYMENT, null, values);
    }

    public static Cursor getPayments(SQLiteDatabase db) {
        String[] columns = {PAYMENT_ID, PAYMENT_TITLE, PAYMENT_DESCRIPTION, PAYMENT_AMOUNT};
        return db.query(TABLE_PAYMENT, columns, null, null, null, null, null);
    }

    // PAYMENT GROUP ----------
    public long addPaymentGroup(String title, String description) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(PAYMENT_GROUP_TITLE, title);
        values.put(PAYMENT_GROUP_DESCRIPTION, description);

        return db.insert(TABLE_PAYMENT_GROUP, null, values);
    }

    public Cursor getPaymentGroups() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {PAYMENT_GROUP_ID, PAYMENT_GROUP_TITLE, PAYMENT_GROUP_DESCRIPTION};
        return db.query(TABLE_PAYMENT_GROUP, columns, null, null, null, null, null);
    }

    public Cursor getPaymentGroup(int paymentGroupId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {PAYMENT_GROUP_ID, PAYMENT_GROUP_TITLE, PAYMENT_GROUP_DESCRIPTION};
        String selection = PAYMENT_GROUP_ID + " = ?";
        String[] selectionArgs = {String.valueOf(paymentGroupId)};
        return db.query(TABLE_PAYMENT_GROUP, columns, selection, selectionArgs, null, null, null);
    }


    //USER - PG RELATION ---------------------------------------------------------------------------
//    public static long addPaymentGroupUserRelation(SQLiteDatabase db, long paymentGroupId, long userId) {
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_PAYMENT_GROUP_ID, paymentGroupId);
//        values.put(COLUMN_USER_ID, userId);
//
//        return db.insert(TABLE_NAME, null, values);
//    }



    //USER - PAYMENT RELATION ----------------------------------------------------------------------
//        public static long addUserPaymentRelation(SQLiteDatabase db, long userId, long paymentId) {
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_USER_ID, userId);
//        values.put(COLUMN_PAYMENT_ID, paymentId);
//
//        return db.insert(TABLE_NAME, null, values);
//    }

}
