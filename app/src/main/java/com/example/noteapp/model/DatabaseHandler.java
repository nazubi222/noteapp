package com.example.noteapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {


    private static final String TABLE_NOTES = "notes";
    private static final String KEY_IDNOTE = "idnote";
    private static final String COLUMN_FOLDER_ID = "idfolder";
    private static final String COLUMN_NAME_NOTE = "name";
    private static final String COLUMN_CONTEXT = "context";
    private static final String COLUMN_ISPIN = "ispin";
    private static final String COLUMN_ISLOCK = "islock";
    private static final String COLUMN_ISCHECKED_NEWNOTE = "ischeckednewnote";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_DATE = "date";

    private static final String TABLE_NAMEFOLDER = "folders";
    private static final String KEY_IDFOLDER = "idfolder";
    private static final String COLUMN_NAMEFOLDER = "namefolder";


    public DatabaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("database", "Create table folder");
        String createFolderTable = String.format("CREATE TABLE " + TABLE_NAMEFOLDER + " ("
                + KEY_IDFOLDER + " INTEGER PRIMARY KEY, "
                + COLUMN_NAMEFOLDER+ " TEXT);");

        String insertSampleRow = "INSERT INTO " + TABLE_NAMEFOLDER
                + " (" + KEY_IDFOLDER + ", " + COLUMN_NAMEFOLDER + ") VALUES (1, 'Ghi chú')";


        //create table note
        Log.i("database", "Create table note");
        String createNoteTable = String.format("CREATE TABLE " + TABLE_NOTES + " ("
                + KEY_IDNOTE + " INTEGER PRIMARY KEY,"
                + COLUMN_FOLDER_ID + " INTEGER,"
                + COLUMN_NAME_NOTE + " TEXT NULL,"
                + COLUMN_CONTEXT + " TEXT,"
                + COLUMN_ISPIN + " NUMERIC,"
                + COLUMN_ISLOCK + " NUMERIC,"
                + COLUMN_ISCHECKED_NEWNOTE + " NUMERIC,"
                + COLUMN_PASSWORD + " TEXT NULL,"
                + COLUMN_DATE + " TEXT NULL,"
                + "FOREIGN KEY(" + KEY_IDFOLDER + ") REFERENCES " + TABLE_NAMEFOLDER + "(" + COLUMN_FOLDER_ID + "))");


        db.execSQL(createFolderTable);
        db.execSQL(createNoteTable);
        db.execSQL(insertSampleRow);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Xoá bảng cũ
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMEFOLDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        //Tiến hành tạo bảng mới
        onCreate(db);
    }

    public void addFolder(Folder folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMEFOLDER, folder.getNameFolder());
        db.insert(TABLE_NAMEFOLDER, null, values);
        db.close();

        Log.i("database", "Tạo folder thành công");
    }

    public List<Folder> getAllFolder(){
        List<Folder> folders= new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAMEFOLDER;

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while(cursor.isAfterLast() == false) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);

            Folder folder = new Folder(id, name);
            folders.add(folder);
            cursor.moveToNext();
        }
        cursor.close();
        return folders;
    }

    public void deleteFolder(int id){
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            // Thực hiện xóa dữ liệu dựa trên điều kiện
            String whereClause = KEY_IDFOLDER + " = ?";
            String[] whereArgs = {String.valueOf(id)};
            db.delete(TABLE_NAMEFOLDER, whereClause, whereArgs);

            String whereClause1 =COLUMN_FOLDER_ID + " = ?";
            String[] whereArgs1 = {String.valueOf(id)};
            db.delete(TABLE_NOTES, whereClause1, whereArgs1);

            db.close();
        }catch (SQLiteException e) {
            Log.d("database", e.toString());
        }
    }

    public void updateChangeNameFolder(int id, String name){
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_NAMEFOLDER, name);

            String whereClause = KEY_IDFOLDER + " = ?";
            String[] whereArgs = {String.valueOf(id)};

            db.update(TABLE_NAMEFOLDER, values, whereClause, whereArgs);
            Log.i("database", "update  tên note thành công" );
            db.close();
        }catch (SQLiteException e) {
            Log.d("database", e.toString());
        }
    }

    public long addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String name = note.getName();
        if (name == null) {
            values.putNull(COLUMN_NAME_NOTE);
        } else {
            values.put(COLUMN_NAME_NOTE, name);
        }
        values.put(COLUMN_FOLDER_ID, note.getIdFolder());
        values.put(COLUMN_CONTEXT, note.getContext());
        values.put(COLUMN_ISLOCK, note.isLock());
        values.put(COLUMN_ISPIN, note.isPin());
        values.put(COLUMN_ISCHECKED_NEWNOTE, note.isCheckedNewNote());
        String password = note.getPassword();
        if (password == null) {
            values.putNull(COLUMN_PASSWORD);
        } else {
            values.put(COLUMN_PASSWORD, password);
        }
        values.put(COLUMN_DATE, String.valueOf(note.getDate()));
        long idnote = db.insert(TABLE_NOTES, null, values);
        db.close();

        Log.i("database", "Tạo note thành công");
        return idnote;
    }

    public void updateNote(int id, String context, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTEXT, context);
        values.put(COLUMN_DATE, date);

        String whereClause = KEY_IDNOTE + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.update(TABLE_NOTES, values, whereClause, whereArgs);

        db.close();
        Log.i("database", "Update context thành công");
    }

    public void updateIsPinNote(int id, boolean isPin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ISPIN, isPin);

        String whereClause = KEY_IDNOTE + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.update(TABLE_NOTES, values, whereClause, whereArgs);
        Log.i("database", "update  ispin note thành công");
        db.close();
    }

    public void updateIsLockNote(int id, boolean isLock, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ISLOCK, isLock);
        values.put(COLUMN_PASSWORD, password);

        String whereClause = KEY_IDNOTE + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.update(TABLE_NOTES, values, whereClause, whereArgs);
        Log.i("database", "update  khóa note thành công" );
        db.close();
    }

    public void updateUnIsLockNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ISLOCK, false);

        String whereClause = KEY_IDNOTE + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.update(TABLE_NOTES, values, whereClause, whereArgs);
        Log.i("database", "update  bỏ khóa note thành công" );
        db.close();
    }

    public void updateChangeName(int id, String name){
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_NOTE, name);

            String whereClause = KEY_IDNOTE + " = ?";
            String[] whereArgs = {String.valueOf(id)};

            db.update(TABLE_NOTES, values, whereClause, whereArgs);
            Log.i("database", "update  tên note thành công" );
            db.close();
        }catch (SQLiteException e) {
            Log.d("database", e.toString());
        }
    }


    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Thực hiện xóa dữ liệu dựa trên điều kiện
        String whereClause = KEY_IDNOTE + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        db.delete(TABLE_NOTES, whereClause, whereArgs);

        db.close();
    }
    public Note getNote(int idnote){
        Note note = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_NOTES + " WHERE " + KEY_IDNOTE + " = " + idnote;

            Cursor cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                int idfolder = cursor.getInt(1);
                String name = cursor.getString(2);
                String context = cursor.getString(3);
                int isPinValues  = cursor.getInt(4);
                int isLockValues  = cursor.getInt(5);
                int isCheckedNewNote  = cursor.getInt(6);
                boolean isPin, isLock, isChecked;
                if(isLockValues != 0){
                    isLock = true;
                }else {
                    isLock = false;
                }
                if(isPinValues != 0){
                    isPin = true;
                }else {
                    isPin = false;
                }
                if(isCheckedNewNote != 0){
                    isChecked = true;
                }else {
                    isChecked = false;
                }

                String password = cursor.getString(7);
                String date = cursor.getString(8);


                note = new Note(id, idfolder, name, context, isPin, isLock, isChecked, password, date);
                cursor.moveToNext();
            }
        }catch (SQLiteException e) {
            Log.d("database", e.toString());
        }

        return note;
    }

    public List<Note> getAllNote(){
        List<Note> notes= new ArrayList<>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_NOTES;

            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                int id = cursor.getInt(0);
                int idfolder = cursor.getInt(1);
                String name = cursor.getString(2);
                String context = cursor.getString(3);
                int isPinValues  = cursor.getInt(4);
                int isLockValues  = cursor.getInt(5);
                int isCheckedNewNote  = cursor.getInt(6);
                boolean isPin, isLock, isChecked;
                if(isLockValues != 0){
                    isLock = true;
                }else {
                    isLock = false;
                }
                if(isPinValues != 0){
                    isPin = true;
                }else {
                    isPin = false;
                }
                if(isCheckedNewNote != 0){
                    isChecked = true;
                }else {
                    isChecked = false;
                }

                String password = cursor.getString(7);
                String date = cursor.getString(8);


                Note note = new Note(id, idfolder, name, context, isPin, isLock, isChecked, password, date);
                notes.add(note);
                cursor.moveToNext();
            }
            cursor.close();
        }catch (SQLiteException e) {
            Log.d("database", e.toString());
        }
        return notes;

    }

    public List<Note> getNotesInFolder(int folderId) {
        List<Note> notes = new ArrayList<>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_NOTES + " WHERE "+ COLUMN_FOLDER_ID + " = " + folderId;

            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false) {
                int id = cursor.getInt(0);
                int idfolder = cursor.getInt(1);
                String name = cursor.getString(2);
                String context = cursor.getString(3);
                int isPinValues  = cursor.getInt(4);
                int isLockValues  = cursor.getInt(5);
                int isCheckedNewNote  = cursor.getInt(6);
                boolean isPin, isLock, isChecked;
                if(isLockValues != 0){
                    isLock = true;
                }else {
                    isLock = false;
                }
                if(isPinValues != 0){
                    isPin = true;
                }else {
                    isPin = false;
                }
                if(isCheckedNewNote != 0){
                    isChecked = true;
                }else {
                    isChecked = false;
                }

                String password = cursor.getString(7);
                String date = cursor.getString(8);


                Note note = new Note(id, idfolder, name, context, isPin, isLock, isChecked, password, date);
                notes.add(note);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (SQLiteException e) {
            Log.d("database", e.toString());
        }

        return notes;
    }

}
