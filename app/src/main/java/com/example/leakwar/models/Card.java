package com.example.leakwar.models;

public class Card {
    private String name;
    private String author;
    private String description;
    private int size;
    private int turnEffect;
    private boolean changePlayer;
    private boolean changeCard;
    private boolean isSlave;
    private boolean toBlock;
    private String target;

    public Card() {

    }

    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public boolean isSlave() {
        return this.isSlave;
    }
    public boolean isChangePlayer() {
        return this.changePlayer;
    }
    public boolean isChangeCard() {
        return this.changeCard;
    }
    public boolean isToBlock() {
        return this.toBlock;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public void setTarget(String target) {
        this.target = target;
    }
}
