package com.example.noteapp.model;

import java.util.Date;

public class Note {
    private int idNote;
    private int idFolder;
    private String name;
    private String context;
    private boolean isPin;
    private boolean isLock;
    private boolean isCheckedNewNote;
    private String password;
    private String date;

    public  Note(){

    }


    public Note(int idNote, int idFolder, String name, String context, boolean isPin, boolean isLock, boolean isCheckedNewNote, String password, String date) {
        this.idNote = idNote;
        this.idFolder = idFolder;
        this.name = name;
        this.context = context;
        this.isPin = isPin;
        this.isLock = isLock;
        this.password = password;
        this.date = date;
        this.isCheckedNewNote = isCheckedNewNote;
    }

    public Note(int idNote, int idFolder, String context, boolean isPin, boolean isLock, String date) {
        this.idNote = idNote;
        this.idFolder = idFolder;
        this.context = context;
        this.isPin = isPin;
        this.isLock = isLock;
        this.date = date;
    }


    public int getIdNote() {
        return idNote;
    }

    public void setIdNote(int idNote) {
        this.idNote = idNote;
    }

    public int getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(int idFolder) {
        this.idFolder = idFolder;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isPin() {
        return isPin;
    }

    public void setPin(boolean pin) {
        isPin = pin;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheckedNewNote() {
        return isCheckedNewNote;
    }

    public void setCheckedNewNote(boolean checkedNewNote) {
        isCheckedNewNote = checkedNewNote;
    }
}
