package com.example.chatgptchatbot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    RecyclerView messageRecyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    ImageButton imageButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    private static final int SELECT_IMAGE_REQUEST = 1;
    private Bitmap selectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageList = new ArrayList<>();

        messageRecyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        imageButton = findViewById(R.id.image_btn);

        // Setup message recycler view
        messageAdapter = new MessageAdapter(messageList);
        messageRecyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(llm);

        imageButton.setOnClickListener((v) -> openImageGallery());

        sendButton.setOnClickListener((v) -> {
            String question = messageEditText.getText().toString().trim();
            if (!question.isEmpty() || selectedImage != null) {
                if (selectedImage != null) {
                    // Add the image to the chat immediately
                    addToChat(question, Message.SENT_BY_ME, selectedImage);
                    callAPIWithImage(question, selectedImage);
                    selectedImage = null;
                    welcomeTextView.setVisibility(View.GONE);
                } else {
                    addToChat(question, Message.SENT_BY_ME, null);
                    callAPIWithText(question);
                    welcomeTextView.setVisibility(View.GONE);
                }
                messageEditText.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Please enter a question or select an image.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void addToChat(String message, String sentBy, Bitmap image) {
        messageList.add(new Message(message, sentBy, image));
        messageAdapter.notifyDataSetChanged();
        messageRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
    }

    void callAPIWithText(String question) {
        // Add "Typing..." message to the chat
        Message typingMessage = new Message("Typing...", Message.SENT_BY_BOT, null);
        messageList.add(typingMessage);
        int typingMessageIndex = messageList.size() - 1;
        messageAdapter.notifyItemInserted(typingMessageIndex);
        messageRecyclerView.smoothScrollToPosition(typingMessageIndex);

        // Initialize the generative model for text
        GenerativeModel gmText = new GenerativeModel("gemini-pro", BuildConfig.API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gmText);

        // Create content with the question
        Content content = new Content.Builder().addText(question).build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    String resultText = result.getText();
                    // Update the "Typing..." message with the actual reply
                    typingMessage.setMessage(resultText.trim());
                    typingMessage.setImage(null);
                    messageAdapter.notifyItemChanged(typingMessageIndex);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    // Update the "Typing..." message with the error message
                    typingMessage.setMessage("Failed to load response due to " + t.getMessage());
                    typingMessage.setImage(null);
                    messageAdapter.notifyItemChanged(typingMessageIndex);
                });
            }
        }, executor);
    }

    void callAPIWithImage(String question, Bitmap image) {
        // Add "Typing..." message to the chat
        Message typingMessage = new Message("Typing...", Message.SENT_BY_BOT, null);
        messageList.add(typingMessage);
        int typingMessageIndex = messageList.size() - 1;
        messageAdapter.notifyItemInserted(typingMessageIndex);
        messageRecyclerView.smoothScrollToPosition(typingMessageIndex);

        // Initialize the generative model for images
        GenerativeModel gmImage = new GenerativeModel("gemini-pro-vision", BuildConfig.API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gmImage);

        // Convert the Bitmap to a base64 string
        String base64Image = convertBitmapToBase64(image);

        // Convert Base64 string back to Bitmap
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        // Now add the Bitmap to the Content
        Content content = new Content.Builder()
                .addText(question)
                .addImage(decodedByte)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    String resultText = result.getText();
                    // Replace the "Typing..." message with the actual reply
                    typingMessage.setMessage(resultText);
                    typingMessage.setImage(null);  // No need to add the image again
                    messageAdapter.notifyItemChanged(typingMessageIndex);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    // Replace the "Typing..." message with an error message
                    typingMessage.setMessage("Failed to load response due to " + t.getMessage());
                    typingMessage.setImage(null);  // No need to add the image again
                    messageAdapter.notifyItemChanged(typingMessageIndex);
                });
            }
        }, executor);
    }


    private void openImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                // You can remove this line to prevent adding the image to the chat
                // addToChat("", Message.SENT_BY_ME, selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri selectedImageUri = data.getData();
//            try {
//                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//                // Add image to chat immediately with no text
//                addToChat("", Message.SENT_BY_ME, selectedImage);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
