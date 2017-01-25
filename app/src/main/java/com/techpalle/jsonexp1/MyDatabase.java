package com.techpalle.jsonexp1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by manasranjan on 1/25/2017.
 */

public class MyDatabase {

    MyHelper myHelper ;
    SQLiteDatabase sqLiteDatabase ;//for doing DML operation
    //step 6: create object for MyHelper variable by taking an constructor
    public MyDatabase(Context c)
    {
        myHelper = new MyHelper(c,"techpalle.db",null,1);

    }
    //step7 :create sqlitedatabase object by using open method
    public void open()
    {
        sqLiteDatabase = myHelper.getWritableDatabase();
    }
    //step 8: perform DML operartion
    public void insertStudent(String name, String email,String mobile)
    {
        ContentValues cv= new ContentValues();
        cv.put("name",name);
        cv.put("email",email);
        cv.put("mobile",mobile);
        sqLiteDatabase.insert("student",null,cv);//here inserting rows

    }

    public Cursor queryStudent()
    {
        Cursor c= null;
        //Q1: read all student details
        c = sqLiteDatabase.query("student",null,null,null,null,null,null);
        return  c;
    }
    //step 9 : close data base
    public void close()
    {
        sqLiteDatabase.close();
    }
    //Step3: create Inner Healper class for DDL operation
    private class MyHelper extends SQLiteOpenHelper {
        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Step 4: create all required tables in this method
            //during creating sql command you have to also give semicolon inside doublecort
            db.execSQL("create table student(_id integer primary key, name text, email text, mobile text);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
