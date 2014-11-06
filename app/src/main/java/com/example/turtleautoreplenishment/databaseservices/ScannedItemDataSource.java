package com.example.turtleautoreplenishment.databaseservices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.turtleautoreplenishment.ScannedItem;

import java.util.ArrayList;

/**
 * Class maintains sqlite database connection and supports adding and fetching scanneditems
 */
public class ScannedItemDataSource
{
    private SQLiteDatabase database;
    private TurtleSQLiteHelper dbHelper;
    private String[] columns = {"id", "custProd", "turtleProd", "repType",
                                "descOne", "descTwo", "quantity", "max", "min", "bin"};
    private final String TABLE;

    public ScannedItemDataSource(Context context)
    {
        dbHelper = new TurtleSQLiteHelper(context);
        TABLE = "item";
    }

    public void openDB()
    {
        database = dbHelper.getWritableDatabase();
    }

    public void closeDB()
    {
        dbHelper.close();
    }

    // method for inserting a scanned item into the db
    // returns the newly created item after querying the db to make sure it was inserted
    public ScannedItem createScannedItem(String turtleProdID, String custProdID, String replenishment, String descOne,
                                         String descTwo, int quantity, String max, String min, String binNumber)
    {
        ContentValues values = new ContentValues();

        values.put(columns[1], custProdID);
        values.put(columns[2], turtleProdID);
        values.put(columns[3], replenishment);
        values.put(columns[4], descOne);
        values.put(columns[5], descTwo);
        values.put(columns[6], quantity);
        values.put(columns[7], max);
        values.put(columns[8], min);
        values.put(columns[9], binNumber);

        long insertID = database.insert(TABLE, null, values);

        Cursor cursor = database.query("item", columns, "id = " + insertID, null, null, null, null);
        cursor.moveToFirst();

        ScannedItem newScannedItem = cursorToScannedItem(cursor);
        cursor.close();

        return newScannedItem;
    }

    public void deleteScannedItem(ScannedItem toDelete)
    {
        int idToDelete = toDelete.getSqLiteID();
        database.delete("item", columns[0] + " = " + idToDelete, null);

        Log.i("Database Event: ", "Deleted item: " + idToDelete);
    }

    public ArrayList<ScannedItem> getAllItems()
    {
        ArrayList<ScannedItem> scannedItems = new ArrayList<ScannedItem>();

        Cursor cursor = database.query("item", columns, null, null, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast())
        {
            ScannedItem current = cursorToScannedItem(cursor);
            scannedItems.add(current);
            cursor.moveToNext();
        }

        cursor.close();

        return scannedItems;
    }

    public ScannedItem getScannedItemByID(int id)
    {
        Cursor cursor = database.query("item", columns, "id = " + id, null, null, null, null);
        cursor.moveToFirst();
        ScannedItem toReturn = cursorToScannedItem(cursor);
        cursor.close();

        return toReturn;
    }

    public void updateItem(ScannedItem updatedItem)
    {
        ContentValues valuesToUpdate = new ContentValues();
        valuesToUpdate.put(columns[3], updatedItem.getQuantity());
        valuesToUpdate.put(columns[6], updatedItem.getQuantity());
        valuesToUpdate.put(columns[7], updatedItem.getMax());
        valuesToUpdate.put(columns[8], updatedItem.getMin());

        int updatedID = database.update("item", valuesToUpdate, "id = " + updatedItem.getSqLiteID(),null);

        Log.i("Database Event: ", "Updated item: " + updatedID);
    }

    public void clearTable()
    {
        int numDeleted = database.delete("item", "1", null);

        Log.i("Database Event: ", numDeleted + " rows DELETED");
    }

    private ScannedItem cursorToScannedItem(Cursor cursor)
    {
        int id = cursor.getInt(0);
        String turtleProd = cursor.getString(1);
        String custProd = cursor.getString(2);
        String repType = cursor.getString(3);
        String descOne = cursor.getString(4);
        String descTwo = cursor.getString(5);
        int quantity  = cursor.getInt(6);
        String min = cursor.getString(7);
        String max = cursor.getString(8);
        String bin = cursor.getString(9);

        return new ScannedItem(id, turtleProd, custProd, repType, descOne,
                                descTwo, quantity, min, max, bin);
    }

}
