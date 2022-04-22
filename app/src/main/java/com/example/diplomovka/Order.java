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

public class Order extends AppCompatActivity {
    String token;
    String fullname;
    String carId;

    private TextView textViewDateToExpected, textViewDateOfBirth, textViewIBAN;
    private DatePickerDialog.OnDateSetListener onDateSetListenerDateToExpected, onDateSetListenerDateOfBirth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Intent intent = getIntent();
        this.token = intent.getStringExtra("token");
        this.fullname = intent.getStringExtra("fullname");
        this.carId = intent.getStringExtra("carId");

        fillCar();
        fillIBAN();

        ((TextView) findViewById(R.id.orderFullname)).setText(this.fullname);

        this.textViewDateToExpected = (TextView) findViewById(R.id.orderDateToExpected);
        textViewDateToExpected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Order.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        Order.this.onDateSetListenerDateToExpected,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();
            }
        });

        this.onDateSetListenerDateToExpected = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date = year + "-" + ((month < 10) ? "0" : "") + month + "-" + ((day < 10) ? "0" : "") + day;
                Order.this.textViewDateToExpected.setText(date);
                Order.this.fillExpectedPrice(date);
            }
        };

        this.textViewDateOfBirth = (TextView) findViewById(R.id.orderDateOfBirth);
        this.textViewDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Order.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        Order.this.onDateSetListenerDateOfBirth,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });

        this.onDateSetListenerDateOfBirth = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date = year + "-" + ((month < 10) ? "0" : "") + month + "-" + ((day < 10) ? "0" : "") + day;
                Order.this.textViewDateOfBirth.setText(date);
            }
        };

        this.textViewIBAN = (TextView) findViewById(R.id.orderIBAN);

        ((Button) findViewById(R.id.orderCarButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String dateToExpected, dateOfBirth, orderIBAN;
                dateToExpected = String.valueOf(Order.this.textViewDateToExpected.getText());
                dateOfBirth = String.valueOf(Order.this.textViewDateOfBirth.getText());
                orderIBAN = String.valueOf(Order.this.textViewIBAN.getText());
                if (dateToExpected.equals("")) {
                    Utils.showWarning("Zadajte predpokladaný dátum vrátenia!", Order.this);
                    return;
                }
                if (dateOfBirth.equals("")) {
                    Utils.showWarning("Zadajte dátum narodenia!", Order.this);
                    return;
                }
                if (orderIBAN.equals("")) {
                    Utils.showWarning("Zadajte IBAN!", Order.this);
                    return;
                }

                makeOrder(dateToExpected, dateOfBirth, orderIBAN);
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
                    URL url = new URL(Utils.url + Utils.car + "?token=" + Order.this.token + "&car_id=" + Order.this.carId);

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
                                Intent intent = new Intent(Order.this, Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), Order.this);
                            return;
                        }

                        JSONObject data = json.getJSONObject("data");

                        ((TextView) findViewById(R.id.orderCarHeader)).setText(data.getString("fullname"));
                        ((TextView) findViewById(R.id.orderCarCarBodyStyle)).setText(data.getString("type"));
                        ((TextView) findViewById(R.id.orderCarTransmission)).setText(data.getString("transmission"));
                        ((TextView) findViewById(R.id.orderCarFuel)).setText(data.getString("fuel"));
                        ((TextView) findViewById(R.id.orderCarSeats)).setText(data.getString("seats"));
                        ((TextView) findViewById(R.id.orderCarPower)).setText(data.getString("power"));
                        ((TextView) findViewById(R.id.orderCarPrice)).setText(data.getString("price_for_day"));
                        new Utils.DownloadImageTask((ImageView) findViewById(R.id.orderCarImage)).execute(Utils.url + data.getString("image_location"));
                        ((Button) findViewById(R.id.orderCarButton)).setText("Uhradiť zálohu: " + data.getString("deposit"));
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Order.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Order.this);
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
                    URL url = new URL(Utils.url + Utils.iban + "?token=" + Order.this.token);

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
                                Intent intent = new Intent(Order.this, Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), Order.this);
                            return;
                        }

                        JSONObject data = json.getJSONObject("data");

                        ((TextView) findViewById(R.id.orderIBAN)).setText(data.getString("IBAN"));
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Order.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Order.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void fillExpectedPrice(String rentToExpected) {

        new AsyncTask<Void, Void, JSONObject>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    Log.w("URL", Utils.url + Utils.expectedPrice + "?token=" + Order.this.token + "&car_id=" + Order.this.carId + "&rent_to_expected=" + rentToExpected);
                    URL url = new URL(Utils.url + Utils.expectedPrice + "?token=" + Order.this.token + "&car_id=" + Order.this.carId + "&rent_to_expected=" + rentToExpected);

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
                                Intent intent = new Intent(Order.this, Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), Order.this);
                            return;
                        }

                        JSONObject data = json.getJSONObject("data");

                        ((TextView) findViewById(R.id.orderPriceExpected)).setText(data.getString("price"));
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Order.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Order.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void makeOrder(final String dateToExpected, final String dateOfBirth, final String orderIBAN) {

        new AsyncTask<Void, Void, JSONObject>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    URL url = new URL(Utils.url + Utils.rentCar + "?token=" + Order.this.token + "&car_id=" + Order.this.carId + "&rent_to_expected=" + dateToExpected + "&IBAN=" + orderIBAN + "&birthdate=" + dateOfBirth);

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

                            Utils.showWarning(json.getString("message"), Order.this);
                            return;
                        }
                        Intent intent = new Intent(Order.this, Menu.class);

                        intent.putExtra("token", Order.this.token);
                        intent.putExtra("fullname", Order.this.fullname);
                        intent.putExtra("message", "Auto vám bolo požičané.");

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Order.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Order.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
