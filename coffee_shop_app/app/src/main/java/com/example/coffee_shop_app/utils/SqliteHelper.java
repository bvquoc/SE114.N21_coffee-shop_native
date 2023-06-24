package com.example.coffee_shop_app.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.coffee_shop_app.Data;
import com.example.coffee_shop_app.models.CartFood;
import com.example.coffee_shop_app.models.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class SqliteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Database.db";
    public static final String CART_TABLE="cart";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_USER_ID="userId";
    public static final String COLUMN_FOOD_ID="foodId";
    public static final String COLUMN_QUANTITY="quantity";
    public static final String COLUMN_SIZE="size";
    public static final String COLUMN_TOPPING="topping";
    public static final String COLUMN_NOTE="note";


    private static final String SQL_CREATE_TABLE= "CREATE TABLE " + CART_TABLE + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            COLUMN_USER_ID + " TEXT NOT NULL," +
            COLUMN_FOOD_ID + " TEXT NOT NULL," +
            COLUMN_QUANTITY + " INTEGER," +
            COLUMN_SIZE + " TEXT NOT NULL," +
            COLUMN_TOPPING + " TEXT," +
            COLUMN_NOTE + " TEXT)";

    public SqliteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }
    public long createCartFood(CartFood cartFood) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_ID, cartFood.getProduct().getId());
        values.put(COLUMN_QUANTITY, cartFood.getQuantity());
        values.put(COLUMN_USER_ID, "1");
        values.put(COLUMN_SIZE, cartFood.getSize());
        values.put(COLUMN_TOPPING, cartFood.getTopping());
        values.put(COLUMN_NOTE, cartFood.getNote());

        final long id= db.insert(CART_TABLE, null, values);
        db.close();
        return id;
    }
    public ArrayList<HashMap<String, Object>> getCartFood(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {
                COLUMN_ID,
                COLUMN_USER_ID,
                COLUMN_FOOD_ID,
                COLUMN_QUANTITY,
                COLUMN_SIZE,
                COLUMN_TOPPING,
                COLUMN_NOTE
        };
        String[] selectionArgs = {userId};

        Cursor cursor = db.query(CART_TABLE, projection, "userId = ?",
                selectionArgs, null, null, null);
        ArrayList<HashMap<String, Object>> items=new ArrayList<HashMap<String, Object>>();

        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(COLUMN_ID, cursor.getString(0));
            map.put(COLUMN_USER_ID, cursor.getString(1));
            map.put(COLUMN_FOOD_ID, cursor.getString(2));
            map.put(COLUMN_QUANTITY,cursor.getInt(3));
            map.put(COLUMN_SIZE,cursor.getString(4));
            map.put(COLUMN_TOPPING, cursor.getString(5));
            map.put(COLUMN_NOTE, cursor.getString(6));

            items.add(map);
        }
        cursor.close();
        db.close();
        return items;
    }

    public long updateCartFood(CartFood cartFood) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_ID, cartFood.getProduct().getId());
        values.put(COLUMN_QUANTITY, cartFood.getQuantity());
        values.put(COLUMN_USER_ID, "1");
        values.put(COLUMN_SIZE, cartFood.getSize());
        values.put(COLUMN_TOPPING, cartFood.getTopping());
        values.put(COLUMN_NOTE, cartFood.getNote());
        String[] selectionArgs = {Integer.toString(cartFood.getId())};

        final long id= db.update(CART_TABLE,values, "id = ?", selectionArgs);
        db.close();
        return id;
    }
    public void deleteCartFood(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {String.valueOf(id)};

        db.delete(CART_TABLE, "id = ?",selectionArgs );
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
