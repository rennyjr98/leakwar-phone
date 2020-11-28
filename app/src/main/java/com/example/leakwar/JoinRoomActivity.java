package com.example.leakwar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class JoinRoomActivity  extends AppCompatActivity {
    protected GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joinroom_layout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Button signInButton = findViewById(R.id.logout);
        Button connectButton = findViewById(R.id.join_btn);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.logout:
                        signOut();
                        break;
                }
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.join_btn:
                        connect();
                        break;
                }
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String [] personData = {
                    acct.getDisplayName(),
                    acct.getGivenName(),
                    acct.getFamilyName(),
                    acct.getEmail(),
                    acct.getId()
            };
            Uri personPhoto = acct.getPhotoUrl();
            setUserData(personData, personPhoto);
        } else {
            Intent intent = new Intent(JoinRoomActivity.this, MainActivity.class);
            startActivity(intent);
        }

        EditText id = findViewById(R.id.room_id_edit);
        EditText password = findViewById(R.id.password_room_edit);
        id.setText("LosRajones");
        password.setText("elpepepe");
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(
                        JoinRoomActivity.this,
                        "Sesión cerrada con exito", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void setUserData(String []data, Uri photo) {
        ImageView displayPhoto = findViewById(R.id.profilePhoto);
        TextView displayName = findViewById(R.id.displayName);

        displayName.setText(data[0]);
        Glide.with(this).load(String.valueOf(photo)).circleCrop().into(displayPhoto);
    }

    private void connect() {
        EditText id = findViewById(R.id.room_id_edit);
        EditText password = findViewById(R.id.password_room_edit);

        boolean correctId = id.getText().length() >= 4;
        boolean correctPassword = password.getText().length() >= 4;

        if(correctId && correctPassword) {
            Toast.makeText(
                    JoinRoomActivity.this,
                    "Conectando a la sala",
                    Toast.LENGTH_LONG).show();
            join();
        } else {
            Toast.makeText(
                    JoinRoomActivity.this,
                    "Nombre de sala y contraseña deben tener al menos 4 digitos",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void join() {
        EditText id = findViewById(R.id.room_id_edit);
        Intent intent = new Intent(JoinRoomActivity.this, GameRoomActivity.class);
        intent.putExtra("roomId", id.getText().toString());
        startActivity(intent);
    }
}
