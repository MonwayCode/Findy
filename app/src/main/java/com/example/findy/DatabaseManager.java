package com.example.findy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<Brand> getBrandsByCategory(String category) {
        List<Brand> brands = new ArrayList<>();
        String selection = DatabaseHelper.COLUMN_CATEGORY + "=?";
        String[] selectionArgs = { category };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_BRANDS,
                null,
                selection,
                selectionArgs,
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Brand brand = new Brand();
                brand.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                brand.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                brand.setCategory(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY)));
                brand.setVegetarianOption(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_VEGETARIAN)) == 1);
                brand.setVeganOption(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_VEGAN)) == 1);
                brand.setGlutenFreeOption(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_GLUTEN_FREE)) == 1);
                brand.setMeatOption(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MEAT)) == 1);

                brands.add(brand);
            }
            cursor.close();
        }
        return brands;
    }

    public long addBrand(Brand brand) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, brand.getName());
        values.put(DatabaseHelper.COLUMN_CATEGORY, brand.getCategory());
        values.put(DatabaseHelper.COLUMN_VEGETARIAN, brand.isVegetarianOption() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_VEGAN, brand.isVeganOption() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_GLUTEN_FREE, brand.isGlutenFreeOption() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_MEAT, brand.isMeatOption() ? 1 : 0);

        return database.insert(DatabaseHelper.TABLE_BRANDS, null, values);
    }
    public void deleteBrand(int id) {
        database.delete(DatabaseHelper.TABLE_BRANDS, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

}