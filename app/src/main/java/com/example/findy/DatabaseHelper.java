package com.example.findy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "findy.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_BRANDS = "brands";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_VEGETARIAN = "vegetarian_option";
    public static final String COLUMN_VEGAN = "vegan_option";
    public static final String COLUMN_GLUTEN_FREE = "gluten_free_option";
    public static final String COLUMN_MEAT = "meat_option";


    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_BRANDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    COLUMN_VEGETARIAN + " INTEGER DEFAULT 0, " +
                    COLUMN_VEGAN + " INTEGER DEFAULT 0, " +
                    COLUMN_GLUTEN_FREE + " INTEGER DEFAULT 0, " +
                    COLUMN_MEAT + " INTEGER DEFAULT 0" + ");";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        String[][] sampleBrands = {
                {"McDonalds", "Jedzenie", "1", "0", "0", "1"},
                {"Starbucks", "Kawiarnie", "1", "1", "1","1"},
                {"Burger King", "Jedzenie", "1", "0", "0","1"},
                {"Subway", "Jedzenie", "1", "0", "1","1"},
                {"KFC", "Jedzenie", "0", "0", "0","1"},
                {"Pizza Hut", "Jedzenie", "1", "0", "1","1"},
                {"7-Eleven", "Sklepy", "0", "0", "0","0"},
                {"Target", "Sklepy", "0", "0", "0","0"},
                {"Dolar Tree", "Sklepy","0", "0", "0","0"},
                {"Costa Coffee", "Kawiarnie", "1", "0", "1","1"},
                {"Dunkin Donuts" ,"Kawiarnie", "0", "0", "1","1"}
        };

        for (String[] brand : sampleBrands) {
            db.execSQL("INSERT INTO " + TABLE_BRANDS + " (" +
                    COLUMN_NAME + ", " +
                    COLUMN_CATEGORY + ", " +
                    COLUMN_VEGETARIAN + ", " +
                    COLUMN_VEGAN + ", " +
                    COLUMN_GLUTEN_FREE + ", " +
                    COLUMN_MEAT + ") VALUES ('" +
                    brand[0] + "', '" +
                    brand[1] + "', " +
                    brand[2] + ", " +
                    brand[3] + ", " +
                    brand[4] + ", " +
                    brand[5] + ");");

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANDS);
        onCreate(db);
    }
}