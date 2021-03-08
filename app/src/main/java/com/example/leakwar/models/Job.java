package com.example.leakwar.models;

import java.util.List;

public class Job {
    private String name;
    private List<Integer> economy;

    public Job(String name, List<Integer> economy) {
        this.name = name;
        this.economy = economy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getEconomy() {
        return economy;
    }

    public void setEconomy(List<Integer> economy) {
        this.economy = economy;
    }
}
