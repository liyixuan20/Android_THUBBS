package com.example.bbs_frontend.repository;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.bbs_frontend.util.TimeStampConverter;

import java.util.Date;

@Entity(tableName = "chat_history")
public class ChatHistory {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "time")
    @TypeConverters({TimeStampConverter.class})
    private Date time;

    @ColumnInfo(name = "content")
    private String content;

    // 消息类型 T/P
    @ColumnInfo(name = "type")
    private String type;

    // 是否为当前用户发送 S/R
    @ColumnInfo(name = "send")
    private String send;

    // 当前用户用户名
    @ColumnInfo(name = "user")
    private String user;

    // 联系人用户名
    @ColumnInfo(name = "contact")
    private String contact;

    // 联系人姓名
    @ColumnInfo(name = "realName")
    private String realName;

    // 联系人id
    @ColumnInfo(name = "contactId")
    private String contactId;

    // 联系人类型
    @ColumnInfo(name = "contactType")
    private String contactType;

    public ChatHistory(Date time, String content, String type, String send, String user, String contact, String contactId, String contactType, String realName) {
        this.time = time;
        this.content = content;
        this.type = type;
        this.send = send;
        this.user = user;
        this.contact = contact;
        this.contactId = contactId;
        this.contactType = contactType;
        this.realName = realName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
