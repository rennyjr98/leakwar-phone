package com.example.leakwar.utils;

import com.example.leakwar.models.Photo;

import java.util.LinkedList;

public class Penance {
    private String author;
    private String group;
    private LinkedList<Photo> album;

    public Penance(String author, String group) {
        this.author = author;
        this.group = group;
        this.album = new LinkedList<Photo>();
    }

    public String getAuthor() {
        return author;
    }

    public LinkedList<Photo> getAlbum() {
        return album;
    }

    public void addAlbum(Photo photo) {
        this.album.add(photo);
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
