package com.example.leakwar.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.leakwar.models.Photo;
import com.example.leakwar.models.Token;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import id.zelory.compressor.Compressor;

public class PhotoClient {
    private static final String[] PATHS = {
            "/DCIM/Camera",
            "/WhatsApp/Media/WhatsApp Images/",
            "/WhatsApp/Media/WhatsApp Images/Sent",
            "/Download"
    };

    public PhotoClient() {

    }

    public static ImageView decodeBase64(String base64, Context context) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(
                decodedString,
                0,
                decodedString.length);
        ImageView tmpImg = new ImageView(context);
        tmpImg.setImageBitmap(decodedByte);
        return tmpImg;
    }

    public static Photo getPhoto(Token token, Context context) {
        boolean isCloud = Math.random() > .5;
        String url = "";

        try {
            if (isCloud) {
                url = getCloudImg(token, context);
            } else {
                url = getLocalImg(context);
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            Log.e("ERRORRR", e.getMessage());
        }

        return new Photo(url, isCloud);
    }

    private static String getLocalImg(Context context) throws IOException {
        Random rand = new Random();
        File _sdcard = Environment.getExternalStorageDirectory();
        String parentPath = PATHS[rand.nextInt(PATHS.length)];
        File dir = new File(_sdcard.getAbsolutePath() + parentPath);
        String url = "";

        while(url.length() == 0) {
            if(dir.exists() && dir.list() != null) {
                if(dir.listFiles().length > 0) {
                    LinkedList<String> filterArray = toImgArray(dir.list());
                    if(filterArray.size() > 0) {
                        url = filterArray.get(rand.nextInt(filterArray.size()));
                        break;
                    }
                }
            }

            parentPath = PATHS[rand.nextInt(PATHS.length)];
            dir = new File(_sdcard.getAbsolutePath() + parentPath);
        }

        File file = new Compressor(context).setQuality(100).compressToFile(new File(dir.getAbsolutePath() + "/" + url));
        return toBase64(file);
    }

    private static String toBase64(File file) throws IOException {
        byte[] bytes = new byte[(int)file.length()];
        FileInputStream fis;
        fis = new FileInputStream(file);
        fis.read(bytes);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private static LinkedList<String> toImgArray(String[] arr) {
        LinkedList<String> imgs = new LinkedList<String>();
        for(String file : arr) {
            String type = file.substring(file.length()-4, file.length());
            switch (type) {
                case ".jpg":
                case "jpeg":
                case ".png":
                    imgs.add(file);
                    break;
            }
        }
        return imgs;
    }

    private static String getCloudImg(Token token, Context appContext) throws InterruptedException, ExecutionException {
        String apiString = LWClient.Server_Domain + "/gphotos?token=" + (new Gson().toJson(token));
        GooglePhotosTask task = new GooglePhotosTask();
        String response = task.execute(apiString).get();
        task.cancel(true);
        return response;
    }

    public static ImageView decodeURL(Photo photo, Context appContext) {
        ImageView img = new ImageView(appContext);
        try {
            String url = photo.getUrl();
            CloudImgTask task = new CloudImgTask();
            Bitmap bitmap = task.execute(url).get();
            img.setImageBitmap(bitmap);
            task.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return img;
    }
}
