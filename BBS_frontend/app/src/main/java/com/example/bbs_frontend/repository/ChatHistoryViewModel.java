package com.example.bbs_frontend.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ChatHistoryViewModel extends AndroidViewModel {

    private ChatHistoryRepository mRepository;

    private LiveData<List<ChatHistory>> mAllHistory;


    public ChatHistoryViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ChatHistoryRepository(application);
        mAllHistory = mRepository.getAllHistory();
    }


    public LiveData<List<ChatHistory>> getAllHistory() {
        return mAllHistory;
    }

    public void insert(ChatHistory chatHistory) {
        mRepository.insert(chatHistory);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void deleteWord(ChatHistory chatHistory) {
        mRepository.delete(chatHistory);
    }

}

