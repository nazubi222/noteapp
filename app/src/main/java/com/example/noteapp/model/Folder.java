package com.example.noteapp.model;

public class Folder {
    private int idFolder;
    private String nameFolder;

    public Folder(String nameFolder) {
        this.nameFolder = nameFolder;
    }
    public Folder(int idFolder, String nameFolder) {
        this.idFolder = idFolder;
        this.nameFolder = nameFolder;
    }

    public Folder() {
    }

    public int getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(int idFolder) {
        this.idFolder = idFolder;
    }

    public String getNameFolder() {
        return nameFolder;
    }

    public void setNameFolder(String nameFolder) {
        this.nameFolder = nameFolder;
    }
}
