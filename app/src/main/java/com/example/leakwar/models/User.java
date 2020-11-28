package com.example.leakwar.models;

public class User {
    private boolean admin;
    private int payTurn;

    private String name;
    private String email;

    private Card[] cards;
    private User[] slaves;

    public User(String name, String email) {
        this.admin = false;
        this.payTurn = 0;
        this.name = name;
        this.email = email;
        this.cards = new Card[1];
        this.slaves = new User[1];
    }

    public String getName() {
        return this.name;
    }

    public boolean isAdmin() {
        return this.admin;
    }
}
