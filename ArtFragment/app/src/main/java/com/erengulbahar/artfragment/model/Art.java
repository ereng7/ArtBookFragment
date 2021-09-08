package com.erengulbahar.artfragment.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Art implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "artistname")
    public String artistName;

    @ColumnInfo(name = "artname")
    public String artname;

    @ColumnInfo(name = "year")
    public String year;

    @ColumnInfo(name = "image")
    public byte[] image;

    public Art(String artistName, String artname, String year, byte[] image)
    {
        this.artistName = artistName;
        this.artname = artname;
        this.year = year;
        this.image = image;
    }
}