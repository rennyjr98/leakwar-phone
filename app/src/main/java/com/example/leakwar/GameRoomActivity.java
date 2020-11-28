package com.example.leakwar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.leakwar.models.User;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

public class GameRoomActivity extends AppCompatActivity {
    private int time = 15;
    private String roomId;

    private User player;
    private Socket mSocket;
    private TextView status;
    private TextView visual_timer;
    private Context appContext = this;
    private Gson gson = new Gson();

    private LinkedList<User> friends = new LinkedList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_room);
        this.roomId = getIntent().getStringExtra("roomId");

        TextView room = findViewById(R.id.room);
        this.status = findViewById(R.id.status);
        this.visual_timer = findViewById(R.id.timer);

        room.setText(roomId);
        this.status.setText("Conectando...");
        this.visual_timer.setText(this.time + " seg");

        try {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            this.player = new User(acct.getDisplayName(), acct.getEmail());
            String json = this.gson.toJson(this.player);
            URI url = new URI(
                    "http",
                    "192.168.0.3:8080",
                    "",
                    "user=" + json + "&room=" + roomId,
                    "");

            this.mSocket = IO.socket(url);
            setSocketListeners();
            this.mSocket.connect();
            this.status.setText("Esperando a más jugadores");
        } catch (URISyntaxException e) {
            this.status.setText(e.getMessage());
        }
    }

    private void setSocketListeners() {
        this.mSocket.on("add_friend", onNewFriend);
    }

    private Emitter.Listener onNewFriend = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final Object tmpFriend = args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    friends.add(gson.fromJson((String)tmpFriend, User.class));
                    LinearLayout linear = findViewById(R.id.list);
                    for(User user : friends) {
                        TextView name = new TextView(appContext);
                        name.setText(user.getName());
                        if(user.isAdmin())
                            linear.addView(name);
                    }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.emit("disconnecting", this.roomId);
        mSocket.disconnect();
        mSocket.off("add_friend", onNewFriend);
    }
}
