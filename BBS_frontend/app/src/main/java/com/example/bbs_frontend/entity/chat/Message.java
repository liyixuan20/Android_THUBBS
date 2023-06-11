package com.example.bbs_frontend.entity.chat;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Calendar;
import java.util.Date;


/**
 * 会话or系统通知
 */
public class Message implements IMessage,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType /*and this one is for custom content type (in this case - voice message)*/ {

    private String id;
    private String text;
    private Calendar cal;
    private User user; // 用户，"0"是自己，"1"是对方，但用户信息都是对方的
    private Image image; // 图片url
    private Voice voice;
    private Boolean read = false; // 是否已读

    private String dateString;

    public Message(String id, User user, String text) {
        this.id = id;
        this.text = text;
        this.user = user;

    }

    public Message(String id, User user, String text, Calendar cal) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.cal = cal;
    }


    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public Boolean isRead() {
        return this.read;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Date getCreatedAt() {
        return cal.getTime();
    }

    public void setCreatedAt(Calendar cal) {
        this.cal = cal;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public String getStatus() {
        return "Sent";
    }

    public void setRead() {
        read = true;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    public static class Voice {

        private String url;
        private int duration;

        public Voice(String url, int duration) {
            this.url = url;
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public int getDuration() {
            return duration;
        }
    }
}
