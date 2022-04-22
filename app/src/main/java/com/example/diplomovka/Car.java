package com.example.diplomovka;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class Car extends AppCompatActivity {
    String token;
    String fullname;
    String carId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        Intent intent = getIntent();
        this.token = intent.getStringExtra("token");
        this.fullname = intent.getStringExtra("fullname");
        this.carId = intent.getStringExtra("carId");

        fillJSONCar();

        findViewById(R.id.carOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Car.this, Order.class);

                intent.putExtra("token", Car.this.token);
                intent.putExtra("fullname", Car.this.fullname);
                intent.putExtra("carId", Car.this.carId);

                startActivityForResult(intent, 1);
            }
        });
    }

    public void fillJSONCar() {

        new AsyncTask<Void, Void, JSONObject>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    URL url = new URL(Utils.url + Utils.car + "?token=" + Car.this.token + "&car_id=" + Car.this.carId);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";

                    while ((tmp = reader.readLine()) != null) {
                        json.append(tmp).append("\n");
                    }
                    reader.close();

                    return new JSONObject(json.toString());
                }
                catch (Exception e) {
                    System.out.println("Exception " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject json) {
                try {
                    if (json != null) {
                        if (json.getInt("status") != 1) {
                            if (json.getInt("status") == 200) {
                                Intent intent = new Intent(Car.this, Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), Car.this);
                            return;
                        }

                        JSONObject data = json.getJSONObject("data");

                        ((TextView) findViewById(R.id.carHeader)).setText(data.getString("fullname"));
                        ((TextView) findViewById(R.id.carManufacturer)).setText(data.getString("manufacturer"));
                        ((TextView) findViewById(R.id.carModel)).setText(data.getString("model"));
                        ((TextView) findViewById(R.id.carColour)).setText(data.getString("colour"));
                        ((TextView) findViewById(R.id.carCarBodyStyle)).setText(data.getString("type"));
                        ((TextView) findViewById(R.id.carTransmission)).setText(data.getString("transmission"));
                        ((TextView) findViewById(R.id.carFuel)).setText(data.getString("fuel"));
                        ((TextView) findViewById(R.id.carSeats)).setText(data.getString("seats"));
                        ((TextView) findViewById(R.id.carPower)).setText(data.getString("power"));
                        ((TextView) findViewById(R.id.carPrice)).setText(data.getString("price_for_day"));

                        if (data.has("features") && !data.isNull("features")) {
                            JSONObject dataFeatures = data.getJSONObject("features");
                            JSONArray dataFeaturesArray = dataFeatures.getJSONArray("comfort");
                            String text = "";
                            for (int i = 0; i < dataFeaturesArray.length(); i++) {
                                if (i != 0) {
                                    text += ", ";
                                }
                                text += dataFeaturesArray.getJSONObject(i).getString("name");
                            }
                            ((TextView) findViewById(R.id.carComfort)).setText(text);

                            dataFeaturesArray = dataFeatures.getJSONArray("secure");
                            text = "";
                            for (int i = 0; i < dataFeaturesArray.length(); i++) {
                                if (i != 0) {
                                    text += ", ";
                                }
                                text += dataFeaturesArray.getJSONObject(i).getString("name");
                            }
                            ((TextView) findViewById(R.id.carSecure)).setText(text);
                        }
                        new Utils.DownloadImageTask((ImageView) findViewById(R.id.carImage)).execute(Utils.url + data.getString("image_location"));
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Car.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Car.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
