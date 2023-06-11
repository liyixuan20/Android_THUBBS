package com.example.bbs_frontend.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ChatHistoryRepository {
    private ChatHistoryDao mChatHistoryDao;
    private LiveData<List<ChatHistory>> mAllHistory;


    public ChatHistoryRepository(Application application) {
        ChatHistoryDb db = ChatHistoryDb.getDatabase(application);
        mChatHistoryDao = db.chatHistoryDao();
        mAllHistory = mChatHistoryDao.getAllHistory();
    }

    public LiveData<List<ChatHistory>> getAllHistory() {
        return mAllHistory;
    }

    public void insert(ChatHistory chatHistory) {
        new insertAsyncTask(mChatHistoryDao).execute(chatHistory);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(mChatHistoryDao).execute();
    }

    public void delete(ChatHistory chatHistory) {
        new deleteAsyncTask(mChatHistoryDao).execute(chatHistory);
    }

    private static class insertAsyncTask extends AsyncTask<ChatHistory, Void, Void> {

        private ChatHistoryDao mAsyncTaskDao;

        insertAsyncTask(ChatHistoryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ChatHistory... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private ChatHistoryDao mAsyncTaskDao;

        deleteAllAsyncTask(ChatHistoryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<ChatHistory, Void, Void> {
        private ChatHistoryDao mAsyncTaskDao;

        deleteAsyncTask(ChatHistoryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ChatHistory... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

}
