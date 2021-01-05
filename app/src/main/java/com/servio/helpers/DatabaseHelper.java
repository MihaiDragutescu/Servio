package com.servio.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clasa care creeaza baza de date locala SQLite cu care lucreaza aplicatia.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RESTAURANT_ADMIN.db";
    private static final String TABLE_NAME = "CELLS_TABLE";
    private static final String COL_1 = "CELL_ID";
    private static final String COL_2 = "UP_NEIGHBOUR";
    private static final String COL_3 = "LEFT_NEIGHBOUR";
    private static final String COL_4 = "CONTENT";
    private static final String COL_5 = "TABLE_NUMBER";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (CELL_ID TEXT,UP_NEIGHBOUR TEXT,LEFT_NEIGHBOUR TEXT,CONTENT TEXT,TABLE_NUMBER INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String cellId, String upNeighbour, String leftNeighbour, String content, int tableNumber) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, cellId);
        contentValues.put(COL_2, upNeighbour);
        contentValues.put(COL_3, leftNeighbour);
        contentValues.put(COL_4, content);
        contentValues.put(COL_5, tableNumber);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
    }

    public void deleteData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public boolean updateCellContent(String cellId, String content) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4, content);
        long result = sqLiteDatabase.update(TABLE_NAME, contentValues, " CELL_ID = ? AND CONTENT IS NULL", new String[]{cellId});

        return result != -1;
    }

    public boolean updateTableNumber(String cellId, int tableNumber) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5, tableNumber);
        long result = sqLiteDatabase.update(TABLE_NAME, contentValues, " CELL_ID = ?", new String[]{cellId});

        return result != -1;
    }

    public boolean undoImage(String parentId) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4, (String) null);
        long result = sqLiteDatabase.update(TABLE_NAME, contentValues, " CELL_ID = ?", new String[]{parentId});

        return result != -1;
    }
}
