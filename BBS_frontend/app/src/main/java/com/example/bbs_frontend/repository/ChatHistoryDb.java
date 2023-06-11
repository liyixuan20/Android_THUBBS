package com.example.bbs_frontend.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ChatHistory.class}, version = 2, exportSchema = false)
public abstract class ChatHistoryDb extends RoomDatabase {

    private static ChatHistoryDb INSTANCE;
    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    static ChatHistoryDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ChatHistoryDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ChatHistoryDb.class, "chat_history")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ChatHistoryDao chatHistoryDao();

    /**
     * Populate the database in the background.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final ChatHistoryDao mDao;

        PopulateDbAsync(ChatHistoryDb db) {
            mDao = db.chatHistoryDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            return null;
        }
    }
}
