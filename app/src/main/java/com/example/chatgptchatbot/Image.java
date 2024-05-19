package com.example.chatgptchatbot;

import android.graphics.Bitmap;

public class Image {
    private Bitmap bitmap;
    private String description;

    public Image(Bitmap bitmap, String description) {
        this.bitmap = bitmap;
        this.description = description;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}