package com.example.leakwar.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.leakwar.models.Token;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GooglePhotosTask extends AsyncTask<String, Void, String> {
    public GooglePhotosTask() {
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            okhttp3.OkHttpClient client = new OkHttpClient.Builder().build();
            okhttp3.Request request = new Request.Builder()
                    .url(urls[0])
                    .build();
            Response resp = client.newCall(request).execute();
            return resp.body().string();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
    }
}
