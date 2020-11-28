package com.example.leakwar.models;

public class Card {
    public Card() {

    }

    public String getName() {
        return this.name;
    }

    private String name;
    private int turnEffect;
    private boolean changePlayer;
    private boolean changeCard;
    private boolean isSlave;
    private boolean toBlock;
    private String target;
}
