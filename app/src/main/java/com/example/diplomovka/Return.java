package com.example.diplomovka;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class Return extends AppCompatActivity {
    String token;
    String fullname;
    String carId;

    private TextView textViewOdometer, textViewIBAN;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        Intent intent = getIntent();
        this.token = intent.getStringExtra("token");
        this.fullname = intent.getStringExtra("fullname");
        this.carId = intent.getStringExtra("carId");

        fillCar();
        fillIBAN();
        fillRentInfo();

        ((TextView) findViewById(R.id.returnFullname)).setText(this.fullname);

        this.textViewIBAN = (TextView) findViewById(R.id.returnIBAN);
        this.textViewOdometer = (TextView) findViewById(R.id.returnOdometer);

        ((Button) findViewById(R.id.returnCarButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String odometer, orderIBAN;

                odometer = String.valueOf(Return.this.textViewOdometer.getText());
                orderIBAN = String.valueOf(Return.this.textViewIBAN.getText());

                if (odometer.equals("")) {
                    Utils.showWarning("Zadajte stav odometera!", Return.this);
                    return;
                }
                if (orderIBAN.equals("")) {
                    Utils.showWarning("Zadajte IBAN!", Return.this);
                    return;
                }

                returnCar(odometer, orderIBAN);
            }
        });
    }

    public void fillCar() {

        new AsyncTask<Void, Void, JSONObject>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    URL url = new URL(Utils.url + Utils.rentedCar + "?token=" + Return.this.token + "&car_id=" + Return.this.carId);

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
                                Intent intent = new Intent(Return.this, Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), Return.this);
                            return;
                        }

                        JSONObject data = json.getJSONObject("data");

                        ((TextView) findViewById(R.id.returnCarHeader)).setText(data.getString("fullname"));
                        ((TextView) findViewById(R.id.returnCarCarBodyStyle)).setText(data.getString("type"));
                        ((TextView) findViewById(R.id.returnCarTransmission)).setText(data.getString("transmission"));
                        ((TextView) findViewById(R.id.returnCarFuel)).setText(data.getString("fuel"));
                        ((TextView) findViewById(R.id.returnCarSeats)).setText(data.getString("seats"));
                        ((TextView) findViewById(R.id.returnCarPower)).setText(data.getString("power"));
                        ((TextView) findViewById(R.id.returnCarPrice)).setText(data.getString("price_for_day"));
                        new Utils.DownloadImageTask((ImageView) findViewById(R.id.returnCarImage)).execute(Utils.url + data.getString("image_location"));
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Return.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Return.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void fillRentInfo() {

        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    URL url = new URL(Utils.url + Utils.rentInfo + "?token=" + Return.this.token + "&car_id=" + Return.this.carId);

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
                                Intent intent = new Intent(Return.this, Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), Return.this);
                            return;
                        }

                        JSONObject data = json.getJSONObject("data");

                        ((TextView) findViewById(R.id.returnRentedFrom)).setText(data.getString("rent_from"));
                        ((TextView) findViewById(R.id.returnRentedToExpected)).setText(data.getString("rent_to_expected"));
                        ((TextView) findViewById(R.id.returnDaysAfter)).setText(data.getString("days_after"));
                        ((TextView) findViewById(R.id.returnAdditionalCharge)).setText(data.getString("additional_charge"));
                        ((TextView) findViewById(R.id.returnDiscount)).setText(data.getString("discount"));
                        ((Button) findViewById(R.id.returnCarButton)).setText("Uhradiť platbu: " + data.getString("final_price"));
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Return.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Return.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void fillIBAN() {

        new AsyncTask<Void, Void, JSONObject>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    URL url = new URL(Utils.url + Utils.iban + "?token=" + Return.this.token);

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
                                Intent intent = new Intent(Return.this, Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), Return.this);
                            return;
                        }

                        JSONObject data = json.getJSONObject("data");

                        ((TextView) findViewById(R.id.returnIBAN)).setText(data.getString("IBAN"));
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Return.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Return.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void returnCar(final String odometer, final String iban) {

        new AsyncTask<Void, Void, JSONObject>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    URL url = new URL(Utils.url + Utils.returnCar + "?token=" + Return.this.token + "&car_id=" + Return.this.carId + "&odometer=" + odometer + "&IBAN=" + iban);

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
                                Intent intent = new Intent(getApplicationContext(), Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), Return.this);
                            return;
                        }
                        Intent intent = new Intent(Return.this, Menu.class);

                        intent.putExtra("token", Return.this.token);
                        intent.putExtra("fullname", Return.this.fullname);
                        intent.putExtra("message", "Auto ste vrátili.");

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Return.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Return.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
