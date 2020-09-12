package com.waslabank.wasslabank.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatModel implements Serializable {

    @SerializedName("chat_id")
    String chatId;
    @SerializedName("lastMessage")
    String lastMessage;
    String seen;
    String name;
    String toId;
    String email;
    String messageSenderId;
    String image;
    String requestId;
    @SerializedName("status")
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ChatModel(String chatId, String lastMessage, String seen, String name, String toId, String email, String messageSenderId, String image, String requestId) {
        this.chatId = chatId;
        this.lastMessage = lastMessage;
        this.seen = seen;
        this.name = name;
        this.toId = toId;
        this.email = email;
        this.messageSenderId = messageSenderId;
        this.image = image;
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getChatId() {
        return chatId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getSeen() {
        return seen;
    }

    public String getName() {
        return name;
    }

    public String getToId() {
        return toId;
    }

    public String getEmail() {
        return email;
    }

    public String getMessageSenderId() {
        return messageSenderId;
    }

    public String getImage() {
        return image;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }
}
