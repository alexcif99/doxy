package com.alexc.doxy;

import static java.sql.DriverManager.println;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    //General
    private static final String DATABASE_NAME = "doxy_db";
    private static final int DATABASE_VERSION = 14;

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
    public static final String PAYMENT_IS_PAID = "is_paid";
    public static final String PAYMENT_USER_OWNER_ID = "user_owner_id";
    public static final String PAYMENT_PAYMENT_GROUP_ID = "payment_group_id";

    //Payment group
    public static final String TABLE_PAYMENT_GROUP = "payment_group";
    public static final String PAYMENT_GROUP_ID = "id";
    public static final String PAYMENT_GROUP_TITLE = "title";
    public static final String PAYMENT_GROUP_DESCRIPTION = "description";

    //Relation User - PaymentGroup
    public static final String TABLE_REL_USER_PG = "rel_user_pg";
    public static final String REL_USER_PG_ID = "id";
    public static final String REL_USER_PG_PAYMENT_GROUP_ID = "payment_group_id";
    public static final String REL_USER_PG_USER_ID = "user_id";
    public static final String REL_USER_PG_AMOUNT = "amount";

    //Relaion User - Payment
    public static final String TABLE_REL_USER_P = "rel_user_payment";
    public static final String REL_USER_P_ID = "id";
    public static final String REL_USER_P_USER_ID = "user_id";
    public static final String REL_USER_P_PAYMENT_ID = "payment_id";
    public static final String REL_USER_P_AMOUNT = "amount";
    public static final String REL_USER_P_IS_PAID = "is_paid";

    //Friend
    public static final String TABLE_FRIENDS = "friends";
    public static final String FRIEND_USER_ID = "user_id";
    public static final String FRIEND_ID = "friend_id";

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
                PAYMENT_IS_PAID + " BOOLEAN," +
                PAYMENT_USER_OWNER_ID + " INTEGER," +
                PAYMENT_PAYMENT_GROUP_ID + " INTEGER," +
                "FOREIGN KEY (" + PAYMENT_USER_OWNER_ID + ") REFERENCES " +
                TABLE_USER + "(" + USER_ID + ")," +
                "FOREIGN KEY (" + PAYMENT_PAYMENT_GROUP_ID + ") REFERENCES " +
                TABLE_PAYMENT_GROUP + "(" + PAYMENT_GROUP_ID + ")" +
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
        String createRelUserPgTable = "CREATE TABLE " + TABLE_REL_USER_PG + "(" +
                REL_USER_PG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                REL_USER_PG_PAYMENT_GROUP_ID + " INTEGER," +
                REL_USER_PG_USER_ID + " INTEGER," +
                REL_USER_PG_AMOUNT + " DOUBLE," +
                "FOREIGN KEY (" + REL_USER_PG_PAYMENT_GROUP_ID + ") REFERENCES " +
                TABLE_PAYMENT_GROUP + "(" + PAYMENT_GROUP_ID + ")," +
                "FOREIGN KEY (" + REL_USER_PG_USER_ID + ") REFERENCES " +
                TABLE_USER + "(" + USER_ID + ")" +
                ")";
        db.execSQL(createRelUserPgTable);
        // Relation User - Payment
        String createRelUserPaymentTable = "CREATE TABLE " + TABLE_REL_USER_P + "(" +
                REL_USER_P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                REL_USER_P_USER_ID + " INTEGER," +
                REL_USER_P_PAYMENT_ID + " INTEGER," +
                REL_USER_P_AMOUNT + " DOUBLE," +
                REL_USER_P_IS_PAID + " BOOLEAN," +
                "FOREIGN KEY (" + REL_USER_P_USER_ID + ") REFERENCES " +
                TABLE_USER + "(" + USER_ID + ")," +
                "FOREIGN KEY (" + REL_USER_P_PAYMENT_ID + ") REFERENCES " +
                TABLE_PAYMENT + "(" + PAYMENT_ID + ")" +
                ")";
        db.execSQL(createRelUserPaymentTable);
        // Friend
        String createTableFriends = "CREATE TABLE " + TABLE_FRIENDS + "(" +
                FRIEND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FRIEND_USER_ID + " INTEGER," +
                "FOREIGN KEY (" + FRIEND_USER_ID + ") REFERENCES " +
                TABLE_USER + "(" + USER_ID + ")" +
                ")";
        db.execSQL(createTableFriends);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REL_USER_PG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REL_USER_P);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + "payment_group_user_relation");

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

    @SuppressLint("Range")
    public User getUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USER_ID, USER_NAME, USER_SURNAME, USER_USERNAME, USER_EMAIL};
        String selection = USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        User user = null;

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(USER_NAME));
            String surname = cursor.getString(cursor.getColumnIndex(USER_SURNAME));
            String username = cursor.getString(cursor.getColumnIndex(USER_USERNAME));
            String email = cursor.getString(cursor.getColumnIndex(USER_EMAIL));

            user = new User(userId, name, surname, username, email, Boolean.FALSE);
        }

        // Cerrar el cursor y la base de datos
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return user;
    }

    public Cursor getUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USER_ID, USER_NAME, USER_SURNAME, USER_USERNAME, USER_EMAIL};
        return db.query(TABLE_USER, columns, null, null, null, null, null);
    }

    // PAYMENT -------------------------------------------------------------------------------------
    public long addPayment(String title, String description, double amount, boolean is_paid, long userOwnerId, long paymentGroupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(PAYMENT_TITLE, title);
        values.put(PAYMENT_DESCRIPTION, description);
        values.put(PAYMENT_AMOUNT, amount);
        values.put(PAYMENT_IS_PAID, is_paid);
        values.put(PAYMENT_USER_OWNER_ID, userOwnerId);
        values.put(PAYMENT_PAYMENT_GROUP_ID, paymentGroupId);

        return db.insert(TABLE_PAYMENT, null, values);
    }

    public Cursor getPayments(Integer paymentGroupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = PAYMENT_PAYMENT_GROUP_ID + " = ?";
        String[] selectionArgs = {String.valueOf(paymentGroupId)};
        String[] columns = {PAYMENT_ID, PAYMENT_TITLE, PAYMENT_DESCRIPTION, PAYMENT_AMOUNT, PAYMENT_USER_OWNER_ID};
        return db.query(TABLE_PAYMENT, columns, selection, selectionArgs, null, null, null);
    }

    public Cursor getPaymentsFromUserOwner(Integer userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = PAYMENT_USER_OWNER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String[] columns = {PAYMENT_ID, PAYMENT_TITLE, PAYMENT_DESCRIPTION, PAYMENT_AMOUNT, PAYMENT_USER_OWNER_ID};
        return db.query(TABLE_PAYMENT, columns, selection, selectionArgs, null, null, null);
    }

    // todo: interessaria obtenir la suma de getPaymentsFromUserOwner


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
    public long addRelUserPg(long paymentGroupId, long userId, double amount) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        println(String.valueOf(values));
        values.put(REL_USER_PG_PAYMENT_GROUP_ID, paymentGroupId);
        values.put(REL_USER_PG_USER_ID, userId);
        values.put(REL_USER_PG_AMOUNT, amount);  // Creem a 0 pero animrem incrementant conforme es vagin afegint pagament per aquets suari i grup de pagament
        return db.insert(TABLE_REL_USER_PG, null, values);
    }

    // todo: no se que interesa retornar, si id o id + nom, depen del que es necessiti per fer la Card o si s'ha de crear un User
    public Cursor getUsersFromPG(Integer paymentGroupId, int currentUserId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {REL_USER_PG_USER_ID};
        String selection = REL_USER_PG_PAYMENT_GROUP_ID + " = ? AND " + REL_USER_PG_USER_ID + " != ?";
        String[] selectionArgs = {String.valueOf(paymentGroupId), String.valueOf(currentUserId)};
        return db.query(TABLE_REL_USER_PG, columns, selection, selectionArgs, null, null, null);
    }

    public Cursor getPaymentGroups(Integer userId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT pg." + PAYMENT_GROUP_ID + ", pg." + PAYMENT_GROUP_TITLE + ", pg." + PAYMENT_GROUP_DESCRIPTION +
                " FROM " + TABLE_REL_USER_PG + " rel_pg " +
                " JOIN " + TABLE_PAYMENT_GROUP + " pg " +
                " ON rel_pg." + REL_USER_PG_PAYMENT_GROUP_ID + " = pg." + PAYMENT_GROUP_ID +
                " WHERE rel_pg." + REL_USER_PG_USER_ID + " = ?";

        String[] selectionArgs = {String.valueOf(userId)};
        return db.rawQuery(query, selectionArgs);
    }

    //USER - PAYMENT RELATION ----------------------------------------------------------------------
    public long addRelUserP(long userId, long paymentId, double amount, boolean is_paid) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(REL_USER_P_USER_ID, userId);
        values.put(REL_USER_P_PAYMENT_ID, paymentId);
        values.put(REL_USER_P_AMOUNT, amount);  // Quantitat que deu respecte el total del pagament
        values.put(REL_USER_P_IS_PAID, is_paid);

        return db.insert(TABLE_REL_USER_P, null, values);
    }

    public Cursor getUsersFromP(Integer paymentId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {REL_USER_P_ID, REL_USER_P_USER_ID, REL_USER_P_PAYMENT_ID, REL_USER_P_AMOUNT};
        String selection = REL_USER_P_PAYMENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(paymentId)};
        return db.query(TABLE_REL_USER_P, columns, selection, selectionArgs, null, null, null);
    }

    //  Suma els paments d'un usuari i un payment group
    //  S'haura de fer un join amb la taula payment per mirar el grup
    public Cursor getAmountTotalSpendedOwnUser(Integer userId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUM(payment." + PAYMENT_AMOUNT +
                ") FROM " + TABLE_PAYMENT + " payment " +
                " JOIN " + TABLE_REL_USER_P + " rel_user_p " +
                " ON payment." + PAYMENT_ID + " = rel_user_p." + REL_USER_P_PAYMENT_ID +
                " WHERE rel_user_p." + REL_USER_P_USER_ID + " =?";
        String[] selectionArgs = {String.valueOf(userId)};
        return db.rawQuery(query, selectionArgs);
    }

//        // Obtenim els pagament d'un usuari
//        String[] columns = {REL_USER_P_ID, REL_USER_P_USER_ID, REL_USER_P_PAYMENT_ID, REL_USER_P_AMOUNT};
//        String selection = REL_USER_P_USER_ID + " = ?";
//        String[] selectionArgs = {String.valueOf(paymentId)};
//        return db.query(TABLE_REL_USER_P, columns, selection, selectionArgs, null, null, null);


    public Cursor getUsersTeDeben(Integer userId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT rel_user_p." + REL_USER_P_ID + ", rel_user_p." + REL_USER_P_PAYMENT_ID + ", rel_user_p." + REL_USER_P_AMOUNT +
                " FROM " + TABLE_PAYMENT + " payment " +
                " JOIN " + TABLE_REL_USER_P + " rel_user_p " +
                " ON payment." + PAYMENT_ID + " = rel_user_p." + REL_USER_P_PAYMENT_ID +
                " WHERE payment." + PAYMENT_USER_OWNER_ID + " =?" +
                " AND rel_user_p." + REL_USER_P_IS_PAID + " = 0";
        String[] selectionArgs = {String.valueOf(userId)};
        return db.rawQuery(query, selectionArgs);
    }

    public Cursor getUsersDebes(Integer userId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT rel_user_p." + REL_USER_P_ID + ", rel_user_p." + REL_USER_P_PAYMENT_ID + ", rel_user_p." + REL_USER_P_AMOUNT +
                " FROM " + TABLE_PAYMENT + " payment " +
                " JOIN " + TABLE_REL_USER_P + " rel_user_p " +
                " ON payment." + PAYMENT_ID + " = rel_user_p." + REL_USER_P_PAYMENT_ID +
                " WHERE payment." + PAYMENT_USER_OWNER_ID + " =?" +
                " AND rel_user_p." + REL_USER_P_IS_PAID + " = 0";
        String[] selectionArgs = {String.valueOf(userId)};
        return db.rawQuery(query, selectionArgs);
    }

    // FRIEND --------------------------------------------------------------------------------------
    public void addFriend(int userId, int friendId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valuesA = new ContentValues();
        valuesA.put(FRIEND_USER_ID, userId);
        valuesA.put(FRIEND_ID, friendId);
        db.insert(TABLE_FRIENDS, null, valuesA);
        ContentValues valuesB = new ContentValues();
        valuesB.put(FRIEND_USER_ID, friendId);
        valuesB.put(FRIEND_ID, userId);
        db.insert(TABLE_FRIENDS, null, valuesB);
        db.close();
    }

    public void deleteFriend(int userId, int friendId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FRIENDS, FRIEND_USER_ID + " = ? AND " + FRIEND_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(friendId)});
        db.close();
    }

    public Cursor getFriends(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {FRIEND_ID};
        String selection = FRIEND_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        return db.query(TABLE_FRIENDS, columns, selection, selectionArgs, null, null, null);
    }

    public List<User> getUsersByUsername(String username) {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {USER_ID, USER_USERNAME}; // Agrega otros campos que desees mostrar en la búsqueda
        String selection = USER_USERNAME + " LIKE ?";
        String[] selectionArgs = {"%" + username + "%"};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(USER_ID));
                @SuppressLint("Range") String userUsername = cursor.getString(cursor.getColumnIndex(USER_USERNAME));
                // Aquí puedes agregar otros campos que desees mostrar en la búsqueda
                User user = getUser(userId);
//                User user = new User(userId, '', '', userUsername, '',Boolean.FALSE );
                userList.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return userList;
    }

    public List<User> searchUsers(String query) {
        List<User> usersList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {USER_ID, USER_USERNAME}; // Agrega aquí los nombres de las columnas que necesitas obtener
        String selection = USER_USERNAME + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%"}; // Utiliza '%query%' para buscar usuarios que contengan el texto de 'query'
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(USER_ID));
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(USER_USERNAME));
                // Puedes obtener aquí los otros datos de usuario si los necesitas y crear el objeto User
                User user = getUser(userId);
                usersList.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return usersList;
    }

    public void removeFriend(int userId, int friendId) {
        SQLiteDatabase db = getWritableDatabase();

        // Eliminar la relación de amistad de la tabla de amigos
        String whereClause = "(" + FRIEND_USER_ID + " = ? AND " + FRIEND_ID + " = ?) OR " +
                "(" + FRIEND_USER_ID + " = ? AND " + FRIEND_ID + " = ?)";
        String[] whereArgs = {String.valueOf(userId), String.valueOf(friendId),
                String.valueOf(friendId), String.valueOf(userId)};

        db.delete(TABLE_FRIENDS, whereClause, whereArgs);
        db.close();
    }




}
