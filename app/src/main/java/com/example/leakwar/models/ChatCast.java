package com.example.leakwar.models;

public class ChatCast {
    private String msg;
    private String to;
    private User author;

    public ChatCast(String msg, String to) {
        this.msg = msg;
        this.to = to;
    }
}
