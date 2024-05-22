package com.example.chatgptchatbot;

import android.graphics.Bitmap;

public class Message {
    private String message;
    private String sentBy;
    private Bitmap image;

    public static final String SENT_BY_ME = "me";
    public static final String SENT_BY_BOT = "bot";

    // Constructor for text-only messages
    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
        this.image = null; // No image for text-only messages
    }

    // Constructor for messages with an image
    public Message(String message, String sentBy, Bitmap image) {
        this.message = message;
        this.sentBy = sentBy;
        this.image = image;
    }

    // Getters and setters for message, sentBy, and image
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
