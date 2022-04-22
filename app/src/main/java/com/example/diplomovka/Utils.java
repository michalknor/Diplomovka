package com.example.diplomovka;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class Utils {
    static String url = "http://10.0.2.2/car_sharing/";
    static String login = "";
    static String offerFilters = "car_offer_filters";
    static String offer = "car_offer";
    static String car = "car";
    static String rentCar = "rent_car";
    static String offerRented = "car_offer_rented";
    static String rentedCar = "car_rented";
    static String iban = "iban";
    static String expectedPrice = "car_expected_price";
    static String rentInfo = "rent_info";
    static String returnCar = "return_car";

    public static void showWarning(String message, Context context) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
