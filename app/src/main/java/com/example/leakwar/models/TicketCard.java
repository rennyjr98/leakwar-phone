package com.example.leakwar.models;

public class TicketCard {
    private String room;
    private Card card;

    public TicketCard(String room, Card card) {
        this.room = room;
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public String getRoom() {
        return room;
    }
}
