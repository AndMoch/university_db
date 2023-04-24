package com.example.mainproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBMatches {
    private static final String DATABASE_NAME = "university.db";
    private static final int DATABASE_VERSION = 1;
    private static final String FIRST_TABLE_NAME = "Students";
    private static final String SECOND_TABLE_NAME = "Groups";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SURNAME = "surname";
    private static final String COLUMN_SECOND_NAME = "second_name";
    private static final String COLUMN_BIRTHDATE = "birthdate";
    private static final String COLUMN_GROUP_ID = "group_id";
    private static final String COLUMN_NUMBER = "number";
    private static final String COLUMN_FACULTY = "faculty";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_NAME_NUMBER = 1;
    private static final int NUM_COLUMN_SURNAME_FACULTY = 2;
    private static final int NUM_COLUMN_SECOND_NAME = 3;
    private static final int NUM_COLUMN_BIRTHDATE = 4;
    private static final int NUM_COLUMN_GROUP_ID = 5;

    private SQLiteDatabase mDataBase;

    public DBMatches(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insertStud(String name, String surname, String second_name, String birthdate,
                           int group_id) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_SURNAME, surname);
        cv.put(COLUMN_SECOND_NAME, second_name);
        cv.put(COLUMN_BIRTHDATE, birthdate);
        cv.put(COLUMN_GROUP_ID, group_id);
        return mDataBase.insert(FIRST_TABLE_NAME, null, cv);
    }

    public long insertGroup(int number, String faculty) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_NUMBER, number);
        cv.put(COLUMN_FACULTY, faculty);
        return mDataBase.insert(SECOND_TABLE_NAME, null, cv);
    }

    public int updateStud(MatchesStud md) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, md.getName());
        cv.put(COLUMN_SURNAME, md.getSurname());
        cv.put(COLUMN_SECOND_NAME, md.getSecond_name());
        cv.put(COLUMN_BIRTHDATE,md.getBirthdate());
        cv.put(COLUMN_GROUP_ID,md.getGroup_id());
        return mDataBase.update(FIRST_TABLE_NAME, cv, COLUMN_ID + " = ?",new String[] { String.valueOf(md.getId())});
    }
    public int updateGroup(MatchesGroup md) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_NUMBER, md.getNumber());
        cv.put(COLUMN_FACULTY, md.getFaculty());
        return mDataBase.update(SECOND_TABLE_NAME, cv, COLUMN_ID + " = ?",new String[] { String.valueOf(md.getId())});
    }

    public void deleteAllStuds() {
        mDataBase.delete(FIRST_TABLE_NAME, null, null);
    }
    public void deleteAllStudsFromGroup(long groupId) {
        mDataBase.delete(FIRST_TABLE_NAME, COLUMN_GROUP_ID + " = ?", new String[] { String.valueOf(groupId) });
    }
    public void deleteAllGroups() {
        mDataBase.delete(SECOND_TABLE_NAME, null, null);
    }

    public void deleteStud(long id) {
        mDataBase.delete(FIRST_TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }
    public void deleteGroup(long id) {
        mDataBase.delete(SECOND_TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public MatchesStud selectStud(long id) {
        Cursor mCursor = mDataBase.query(FIRST_TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        mCursor.moveToFirst();
        String Name = mCursor.getString(NUM_COLUMN_NAME_NUMBER);
        String Surname = mCursor.getString(NUM_COLUMN_SURNAME_FACULTY);
        String SecondName = mCursor.getString(NUM_COLUMN_SECOND_NAME);
        String Birthdate = mCursor.getString(NUM_COLUMN_BIRTHDATE);
        int GroupId = mCursor.getInt(NUM_COLUMN_GROUP_ID);
        return new MatchesStud(id, Name, Surname, SecondName, Birthdate, GroupId);
    }

    public ArrayList<MatchesStud> selectAllStuds() {
        Cursor mCursor = mDataBase.query(FIRST_TABLE_NAME, null, null, null, null, null, null);

        ArrayList<MatchesStud> arr = new ArrayList<>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String Name = mCursor.getString(NUM_COLUMN_NAME_NUMBER);
                String Surname = mCursor.getString(NUM_COLUMN_SURNAME_FACULTY);
                String SecondName = mCursor.getString(NUM_COLUMN_SECOND_NAME);
                String Birthdate = mCursor.getString(NUM_COLUMN_BIRTHDATE);
                int GroupId = mCursor.getInt(NUM_COLUMN_GROUP_ID);
                arr.add(new MatchesStud(id, Name, Surname, SecondName, Birthdate, GroupId));
            } while (mCursor.moveToNext());
        }
        return arr;
    }
    public ArrayList<MatchesStud> selectStudsByGroup(long groupId) {
        String[] args = {String.valueOf(groupId)};
        Cursor mCursor = mDataBase.query(FIRST_TABLE_NAME, null, COLUMN_GROUP_ID +  " = ? ", args, null, null, null);


        ArrayList<MatchesStud> arr = new ArrayList<>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String Name = mCursor.getString(NUM_COLUMN_NAME_NUMBER);
                String Surname = mCursor.getString(NUM_COLUMN_SURNAME_FACULTY);
                String SecondName = mCursor.getString(NUM_COLUMN_SECOND_NAME);
                String Birthdate = mCursor.getString(NUM_COLUMN_BIRTHDATE);
                int GroupId = mCursor.getInt(NUM_COLUMN_GROUP_ID);
                arr.add(new MatchesStud(id, Name, Surname, SecondName, Birthdate, GroupId));
            } while (mCursor.moveToNext());
        }
        return arr;
    }
    public MatchesGroup selectGroup(long id) {
        Cursor mCursor = mDataBase.query(SECOND_TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        int Num = mCursor.getInt(NUM_COLUMN_NAME_NUMBER);
        String Faculty = mCursor.getString(NUM_COLUMN_SURNAME_FACULTY);
        return new MatchesGroup(id, Num, Faculty);
    }

    public ArrayList<MatchesGroup> selectAllGroups() {
        Cursor mCursor = mDataBase.query(SECOND_TABLE_NAME, null, null, null, null, null, null);

        ArrayList<MatchesGroup> arr = new ArrayList<>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                int Num = mCursor.getInt(NUM_COLUMN_NAME_NUMBER);
                String Faculty = mCursor.getString(NUM_COLUMN_SURNAME_FACULTY);
                arr.add(new MatchesGroup(id, Num, Faculty));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    public MatchesGroup selectGroupByParams(int number, String faculty) {
        Cursor mCursor = mDataBase.query(SECOND_TABLE_NAME, null, COLUMN_NUMBER + " = ? AND " + COLUMN_FACULTY + " = ?", new String[]{String.valueOf(number), faculty}, null, null, null);
        mCursor.moveToFirst();
        long id = mCursor.getLong(NUM_COLUMN_ID);
        int Num = mCursor.getInt(NUM_COLUMN_NAME_NUMBER);
        String Faculty = mCursor.getString(NUM_COLUMN_SURNAME_FACULTY);
        return new MatchesGroup(id, Num, Faculty);
    }

    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION); }


        @Override
        public void onCreate(SQLiteDatabase db) {
            String query1 = "CREATE TABLE " + FIRST_TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_SURNAME + " TEXT, " +
                    COLUMN_SECOND_NAME + " TEXT, " +
                    COLUMN_BIRTHDATE + " TEXT, " +
                    COLUMN_GROUP_ID + " INTEGER);";
            String query2 =   "CREATE TABLE " + SECOND_TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NUMBER + " INTEGER, " +
                    COLUMN_FACULTY + " TEXT); ";
            db.execSQL(query1);
            db.execSQL(query2);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FIRST_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SECOND_TABLE_NAME);
            onCreate(db);
        }
    }

}
