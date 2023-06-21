package com.mca.mindmelter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;

import com.mca.mindmelter.R;
import com.mca.mindmelter.adapters.ChatAdapter;
import com.mca.mindmelter.viewmodels.ChatViewModel;

public class ChatActivity extends AppCompatActivity {
    private ChatViewModel viewModel;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Initialize RecyclerView and its adapter
        RecyclerView recyclerView = findViewById(R.id.chat_recycler_view);
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe LiveData from ViewModel
        viewModel.getChatMessagesLiveData().observe(this, chatMessages -> chatAdapter.submitList(chatMessages));

        // Handle the intent extras
        String triviaId = getIntent().getStringExtra("triviaId");
        String chatId = getIntent().getStringExtra("chatId");

        // Observe User object and load chats when User is ready
        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                if (triviaId != null) {
                    viewModel.loadChatHistoryByTriviaId(triviaId);
                } else if (chatId != null) {
                    viewModel.loadChatHistory(chatId);
                }
            }
        });

        // Handle message sending
        EditText inputText = findViewById(R.id.message_input);
        ImageButton sendButton = findViewById(R.id.send_message_button);

        sendButton.setOnClickListener(view -> {
            String userMessage = inputText.getText().toString();
            if (!userMessage.isEmpty()) {
                viewModel.sendMessage(userMessage);
                inputText.setText("");
            }
        });

        inputText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String userMessage = inputText.getText().toString();
                if (!userMessage.isEmpty()) {
                    viewModel.sendMessage(userMessage);
                    inputText.setText("");
                }
                return true;
            }
            return false;
        });
    }
}
