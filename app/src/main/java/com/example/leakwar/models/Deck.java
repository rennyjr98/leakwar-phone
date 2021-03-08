package com.example.leakwar.models;

import java.util.LinkedList;

public class Deck {
    private String author;
    private LinkedList<Card> cards;

    public Deck() {

    }

    public String getAuthor() {
        return this.author;
    }
    public LinkedList<Card> getCards() {
        return this.cards;
    }
}
