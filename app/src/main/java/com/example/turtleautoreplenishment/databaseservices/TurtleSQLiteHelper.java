package com.example.turtleautoreplenishment.databaseservices;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class to facilitate connection to sqlite database
 */
public class TurtleSQLiteHelper extends SQLiteOpenHelper
{

    private final String CREATE_DB;

    // constructor sets create_db query
    public TurtleSQLiteHelper(Context context)
    {
        super(context, "autoRepl.db", null, 2);

        CREATE_DB = "create table item (_id integer primary key autoincrement, " +
                                        "custProd text not null, " +
                                        "turtleProd text not null, " +
                                        "repType text not null, " +
                                        "descOne text, " +
                                        "descTwo text, " +
                                        "quantity integer not null, " +
                                        "max text not null, " +
                                        "min text not null, " +
                                        "bin text not null)";
    }

    // create database with item table
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        Log.i("DATABASE: ", "Calling db onCreate");
        database.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("DATABASE: ", "Calling db onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS item");
        onCreate(db);
    }
}
