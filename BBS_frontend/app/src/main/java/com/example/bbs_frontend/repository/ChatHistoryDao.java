package com.example.bbs_frontend.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatHistoryDao {

    @Insert
    Long insert(ChatHistory chatHistory);

    @Query("SELECT * FROM chat_history ORDER BY id asc")
    LiveData<List<ChatHistory>> getAllHistory();

    @Delete()
    void delete(ChatHistory chatHistory);

    @Query("DELETE FROM chat_history")
    void deleteAll();


}
