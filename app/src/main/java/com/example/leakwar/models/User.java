package com.example.leakwar.models;

import java.util.LinkedList;

public class User {
    private boolean admin;
    private int payTurn;
    private int paySize;

    private String name;
    private String email;
    private String room;

    private Job job;
    private Job treat;
    private Token token;
    private LinkedList<Card> cards;
    private LinkedList<String> slaves;

    public User(String name, String email, String room, Token token) {
        this.admin = false;
        this.payTurn = 0;
        this.paySize = 1;
        this.name = name;
        this.email = email;
        this.room = room;
        this.cards = new LinkedList<Card>();
        this.slaves = new LinkedList<String>();
        this.token = token;
    }

    public String getName() {
        return this.name;
    }
    public String getEmail() { return this.email; }
    public boolean isAdmin() {
        return this.admin;
    }
    public Job getJob() {
        return job;
    }
    public Job getTreat() {
        return treat;
    }
    public Token getToken() {
        return this.token;
    }
    public LinkedList<Card> getCards() {
        return this.cards;
    }
    public LinkedList<String> getSlaves() {
        return this.slaves;
    }

    public void setCards(LinkedList<Card> cards) {
        this.cards = cards;
    }
    public void setAdmin(boolean isAdmin) {
        this.admin = isAdmin;
    }
    public void setJob(Job job) {
        this.job = job;
    }
    public void setTreat(Job job) {
        this.treat = job;
    }
}
