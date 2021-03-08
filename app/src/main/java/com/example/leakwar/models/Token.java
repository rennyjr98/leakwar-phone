package com.example.leakwar.models;

import java.io.Serializable;
import java.util.Date;

public class Token implements Serializable {
    private static final long serialVersionUID = 42L;
    public String access_token;
    public String token_type;
    public String refresh_token;
    public String expiry;

    public Token() {

    }
}
