package com.example.facesach.model;

public class Message {
    private int id;
    private int sender_id;
    private String room;
    private String message;
    private String created_at;

    private String name;
    private String avatar;

    public Message() {}

    public Message(int id, int sender_id, String room, String message, String created_at, String name, String avatar) {
        this.id = id;
        this.sender_id = sender_id;
        this.room = room;
        this.message = message;
        this.created_at = created_at;
        this.name = name;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
