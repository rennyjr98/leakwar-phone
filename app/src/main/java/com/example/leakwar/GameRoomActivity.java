package com.example.leakwar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.leakwar.models.Token;
import com.example.leakwar.models.User;
import com.example.leakwar.templates.TBaseGameRoom;
import com.example.leakwar.utils.LWClient;
import com.github.nkzawa.socketio.client.IO;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.net.URI;
import java.net.URISyntaxException;

public class GameRoomActivity extends TBaseGameRoom {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.game_room);
        this.roomId = getIntent().getStringExtra("roomId");
        String jsonToken = getIntent().getStringExtra("token");

        TextView room = findViewById(R.id.room);
        this.status = findViewById(R.id.status);
        this.status.setTranslationZ(16);
        this.job = findViewById(R.id.currentdata);
        this.deckBtn = findViewById(R.id.deckBtn);
        this.visual_timer = findViewById(R.id.timer);
        this.deckBox = findViewById(R.id.deckBox);
        this.task = findViewById(R.id.task);
        this.task.setVisibility(View.INVISIBLE);
        this.optionA = findViewById(R.id.option_a);
        this.optionB = findViewById(R.id.option_b);

        room.setText(roomId);
        this.status.setText("Conectando...");
        this.visual_timer.setText("");
        this.deckBox.setVisibility(View.INVISIBLE);

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.status:
                        if(player.isAdmin()) {
                            mSocket.emit("reqStartGame", roomId);
                        }
                        break;
                }
            }
        });

        this.deckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.deckBtn:
                        showDeck = !showDeck;
                        deckBox.setVisibility((showDeck ? View.VISIBLE : View.INVISIBLE));
                        TranslateAnimation anim = null;
                        if(showDeck) {
                            anim = new TranslateAnimation(
                                    0,
                                    0,
                                    deckBox.getHeight(),
                                    0);
                        } else {
                            anim = new TranslateAnimation(
                                    0,
                                    0,
                                    0,
                                    deckBox.getHeight());
                        }
                        anim.setDuration(200);
                        deckBox.startAnimation(anim);
                        break;
                }
            }
        });

        try {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            Token token = gson.fromJson((String)jsonToken, Token.class);
            this.player = new User(acct.getDisplayName(), acct.getEmail(), roomId, token);
            String json = this.gson.toJson(this.player);
            URI url = new URI(
                    "http",
                    LWClient.Server_IP,
                    "",
                    "user=" + json + "&room=" + roomId,
                    "");

            this.mSocket = IO.socket(url);
            setSocketListeners();
            this.mSocket.connect();
            this.status.setText("Esperando a más jugadores");
        } catch(URISyntaxException e) {
            this.status.setText(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
        .setTitle("¿Ya viste suficiente?")
        .setMessage("¿Realmente quieres salir de la sala?")
        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        })
        .setNegativeButton("No", null)
        .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.emit("disconnecting", this.roomId);
        mSocket.disconnect();
        mSocket.off("add_friend", onNewFriend);
    }
}
