package com.example.leakwar.templates;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.leakwar.R;
import com.example.leakwar.models.Card;
import com.example.leakwar.models.Deck;
import com.example.leakwar.models.Job;
import com.example.leakwar.models.Photo;
import com.example.leakwar.models.Slave;
import com.example.leakwar.models.StockExchange;
import com.example.leakwar.models.TicketCard;
import com.example.leakwar.models.Token;
import com.example.leakwar.models.User;
import com.example.leakwar.utils.CustomArrayAdapter;
import com.example.leakwar.utils.Penance;
import com.example.leakwar.utils.PhotoClient;
import com.example.leakwar.utils.ReqPenance;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TBaseGameRoom extends AppCompatActivity {
    protected boolean showDeck = false;
    protected String roomId;

    protected TextView status;
    protected TextView job;
    protected TextView visual_timer;
    protected LinearLayout task;
    protected Button optionA;
    protected Button optionB;
    protected Button deckBtn;
    protected ListView deckBox;

    protected Socket mSocket;
    protected User player;
    protected Gson gson = new Gson();
    private CustomArrayAdapter adapter;
    private Context appContext = this;
    private Activity app = this;

    protected LinkedList<User> friends = new LinkedList<User>();
    protected LinkedList<String> effects = new LinkedList<String>();
    protected LinkedList<LinearLayout> paymentHistory = new LinkedList<LinearLayout>();

    protected void setSocketListeners() {
        this.mSocket.on("applied_card", onAppliedCard);
        this.mSocket.on("add_friend", onNewFriend);
        this.mSocket.on("get_roulette", onGetRoulette);
        this.mSocket.on("get_action", onGetRouletteAction);
        this.mSocket.on("req_payment", onPenance);
        this.mSocket.on("show_payment", onShowPayment);
        this.mSocket.on("get_deck", onGetDeck);
        this.mSocket.on("clock", onClock);
        // this.mSocket.on("end",)
        // TODO : Show cloud imgs
    }

    protected Emitter.Listener onAppliedCard = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String name = (String) args[0];
            runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    String payments = "";
                    for (String name : effects) {
                        payments += (effects.size() == 1) ? name : ", " + name;
                    }
                    status.setText(status.getText() +
                            ((effects.size() == 1) ? "\n" : "") +
                            payments);
                }
            });
        }
    };

    protected Emitter.Listener onNewFriend = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final Object tmpFriend = args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    User responseUser = gson.fromJson((String) tmpFriend, User.class);

                    if (responseUser.getEmail().equals(player.getEmail())) {
                        if (responseUser.isAdmin()) {
                            player.setAdmin(true);
                        }
                    } else {
                        friends.add(responseUser);
                    }
                }
            });
        }
    };

    protected Emitter.Listener onPenance = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final Object reqPayment = args[0];
            final ReqPenance req = gson.fromJson((String) reqPayment, ReqPenance.class);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gatherPenance(req);
                }
            });
        }
    };

    protected Emitter.Listener onGetRoulette = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final Object userRoulette = args[0];
            final User roulette = gson.fromJson((String) userRoulette, User.class);
            runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    status.setText("La ruleta dice : " + roulette.getName());
                    if (roulette.getEmail().equals(player.getEmail())) {
                        String jsonUser = gson.toJson(player);
                        mSocket.emit("reqRouletteAction", jsonUser);
                    }
                }
            });
        }
    };

    protected Emitter.Listener onGetRouletteAction = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final Object userRoulette = args[0];
            final StockExchange stock = gson.fromJson((String) userRoulette, StockExchange.class);
            runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    task.setVisibility(View.VISIBLE);
                    applyAction(stock);
                }
            });
        }
    };

    @SuppressLint("SetTextI18n")
    private void applyAction(final StockExchange stock) {
        TextView taskName = findViewById(R.id.task_name);
        TextView respA = findViewById(R.id.resp_a);
        TextView respB = findViewById(R.id.resp_b);
        taskName.setText(stock.name);
        optionA.setText(stock.a);
        optionB.setText(stock.b);

        switch(stock.type) {
            case 1:
                respA.setText("Ingresos " + stock.ra.get(0) + ", Impuestos: " + stock.ra.get(1));
                respB.setText("Ingresos " + stock.rb.get(0) + ", Impuestos: " + stock.rb.get(1));
                break;
            case 2:
                respA.setText("Impuestos: " + stock.ra.get(0));
                respB.setText("Impuestos: " + stock.rb.get(0));
                break;
            case 3:
                respA.setText("");
                respB.setText("");
                break;
        }

        optionA.setOnClickListener(getCfgOptionTask(stock.type, (stock.type == 2) ? "altf4" : stock.a, stock.ra));
        optionB.setOnClickListener(getCfgOptionTask(stock.type, stock.b, stock.rb));
    }

    private View.OnClickListener getCfgOptionTask(final int type, final String name, final List<Integer> economy) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(type) {
                    case 1:
                        hanldeJob(name, economy);
                        break;
                    case 2:
                        handleTaxes(name, economy);
                        break;
                    case 3:
                        handleAction(name);
                        break;
                }
                task.setVisibility(View.INVISIBLE);
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private void hanldeJob(String name, List<Integer> economy) {
        player.setJob(new Job(name, economy));
        job.setText(name + "\tIngresos: " + economy.get(0) + ", Impuestos: " + economy.get(1));
        mSocket.emit("applyJob", gson.toJson(player));
    }

    private void handleTaxes(String name, List<Integer> economy) {
        player.setTreat(new Job(name, economy));
        mSocket.emit("reqPenance", gson.toJson(player));
        player.setTreat(null);
    }

    private void handleAction(String name) {
        mSocket.emit("vote", name + "," + this.roomId);
    }

    protected Emitter.Listener onClock = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final Object second = args[0];
            final int clock = Integer.parseInt((String) second);
            runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    if(clock == 1 && task.getVisibility() == View.VISIBLE) {
                        // TODO: Check if this works
                        optionB.performClick();
                    }
                    visual_timer.setText(clock + " seg");
                }
            });
        }
    };

    protected void gatherPenance(ReqPenance req) {
        Penance penance = new Penance(this.player.getName(), this.roomId);
        if (req.getPublicList().containsKey(this.player.getEmail())) {
            for (int i = 0; i < req.getPublicList().get(this.player.getEmail()); i++) {
                Token token = this.player.getToken();
                Photo ph = PhotoClient.getPhoto(token, this.appContext);
                ph.addMaster("public");
                penance.addAlbum(ph);
            }
        }

            for (Slave slave: req.getSlaves()) {
                if(slave.name.equals(player.getEmail())) {
                    for(int i = 0; i < slave.quote; i++) {
                        Token token = this.player.getToken();
                        Photo ph = PhotoClient.getPhoto(token, this.appContext);
                        ph.addMaster(slave.owner);
                        penance.addAlbum(ph);
                    }
                }
            }

        this.mSocket.emit("send_penance", gson.toJson(penance));
    }

    protected Emitter.Listener onShowPayment = new Emitter.Listener() {
        @Override
        public void call(Object... objs) {
            final Object objPayment = objs[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Penance payment = gson.fromJson((String) objPayment, Penance.class);
                    int index = 1;
                    for (Photo photo : payment.getAlbum()) {
                        if (photo.canSee(player.getEmail()) || photo.canSee("public")) {
                            LinearLayout bundle = new LinearLayout(appContext);
                            bundle.setOrientation(LinearLayout.VERTICAL);
                            TextView author = new TextView(appContext);
                            author.setText(payment.getAuthor());
                            ImageView img = null;
                            if (photo.isInCloud()) {
                                img = PhotoClient.decodeURL(photo, appContext);
                            } else {
                                img = PhotoClient.decodeBase64(
                                        photo.getUrl(),
                                        appContext
                                );
                            }
                            setImgAttr(img);
                            setAuthorTextViewAttr(author);
                            if (index == payment.getAlbum().size()) {
                                bundle.setPadding(16, 16, 16, 200);
                            } else {
                                bundle.setPadding(16, 16, 16, 25);
                            }
                            bundle.addView(img);
                            bundle.addView(author);
                            paymentHistory.add(bundle);
                        }
                        index++;
                    }
                    setImgScreen();
                }
            });
        }
    };

    protected void setImgAttr(ImageView img) {
        ViewGroup.LayoutParams params = img.getLayoutParams();
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        img.setLayoutParams(params);
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        img.setAdjustViewBounds(true);
        img.setBackgroundResource(R.drawable.authorbox);
    }

    protected void setAuthorTextViewAttr(TextView author) {
        author.setBackgroundResource(R.drawable.authorbox);
        author.setTextColor(Color.WHITE);
        author.setGravity(Gravity.CENTER);
        author.setPadding(10, 10, 10, 10);
    }

    protected void setImgScreen() {
        for (LinearLayout img : this.paymentHistory) {
            LinearLayout layout = findViewById(R.id.wall);
            if (img.getParent() != null) {
                ((LinearLayout) img.getParent()).removeView(img);
                img.setPadding(16, 16, 16, 25);
            }
            layout.addView(img);
        }
    }

    protected Emitter.Listener onGetDeck = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String jsonUsers = (String) args[0];
            final Map<String, Object> users = gson.fromJson(jsonUsers, HashMap.class);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (users.containsKey(player.getEmail())) {
                        JsonObject jsonMe = gson.toJsonTree(users.get(player.getEmail())).getAsJsonObject();
                        User me = gson.fromJson(jsonMe, User.class);
                        player.setCards(me.getCards());

                        ArrayList<String> titles = new ArrayList<String>();
                        ArrayList<String> descs = new ArrayList<String>();

                        for (Card card : player.getCards()) {
                            titles.add(card.getName());
                            descs.add(card.getDescription());
                        }

                        adapter = new CustomArrayAdapter(app, titles, descs);
                        deckBox.setAdapter(adapter);
                        deckBox.setOnItemClickListener(applyCard);
                    }
                }
            });
        }
    };

    private AdapterView.OnItemClickListener applyCard = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
            Card card = player.getCards().remove(position);

            if (card.isChangePlayer()) {
                applyTargetCard(card, position);
            } else {
                sendCard(card);
                removeCard(position);
            }
        }
    };

    private void applyTargetCard(final Card card, final int index) {
        final String[] friends = getFriendsArray();
        new AlertDialog.Builder(this).setTitle("Elige un objetivo")
                .setItems(friends, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (which >= 0 && which < friends.length) {
                                    removeCard(index);
                                    sendCard(card);
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void removeCard(int position) {
        adapter.remove(adapter.getItem(position));
        adapter.notifyDataSetChanged();
    }

    private void sendCard(Card card) {
        TicketCard ticket = new TicketCard(roomId, card);
        String json = gson.toJson(ticket);
        mSocket.emit("set_card", json);
    }

    private String[] getFriendsArray() {
        String[] arr_friends = new String[this.friends.size()];
        for (int i = 0; i < this.friends.size(); i++) {
            arr_friends[i] = this.friends.get(i).getName();
        }

        return arr_friends;
    }

    private Emitter.Listener onEndGame = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Photo photo = new Photo("https://media.kasperskydaily.com/wp-content/uploads/sites/88/2014/06/05224446/game-over.jpg", true);
        }
    };
}
