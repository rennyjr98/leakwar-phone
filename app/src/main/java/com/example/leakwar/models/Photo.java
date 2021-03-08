package com.example.leakwar.models;

import java.util.LinkedList;

public class Photo {
    private String url;
    private boolean inCloud;

    private LinkedList<String> to = new LinkedList<String>();

    public Photo(String _url, boolean _inCloud) {
        this.url = _url;
        this.inCloud = _inCloud;
    }

    public Photo() {

    }

    public String getUrl() {
        return this.url;
    }

    public boolean isInCloud() {
        return this.inCloud;
    }

    public void addMaster(String email) {
        this.to.add(email);
    }

    public boolean canSee(String email) {
        for(String refer : this.to) {
            if (refer.equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "URL: " + this.url + "\nInCloud: " + this.inCloud;
    }
}
