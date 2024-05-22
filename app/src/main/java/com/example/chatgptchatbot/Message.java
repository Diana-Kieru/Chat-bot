package com.example.chatgptchatbot;


import android.graphics.Bitmap;
import android.text.SpannableString;

public class Message {
    private SpannableString message;
    private String sentBy;
    private Bitmap image;

    public static final String SENT_BY_ME = "me";
    public static final String SENT_BY_BOT = "bot";

    public Message(SpannableString message, String sentBy, Bitmap image) {
        this.message = message;
        this.sentBy = sentBy;
        this.image = image;
    }

    public SpannableString getMessage() {
        return message;
    }

    public void setMessage(SpannableString message) {
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
