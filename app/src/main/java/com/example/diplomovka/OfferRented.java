package com.example.diplomovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OfferRented extends AppCompatActivity {
    String token;
    String fullname;

    int page = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_rented);

        Intent intent = getIntent();
        this.token = intent.getStringExtra("token");
        this.fullname = intent.getStringExtra("fullname");

        getRentedCars();

        for (int i = 1; i <= 2; i++) {
            int id = getResources().getIdentifier("offerRentedPagePrevious" + i, "id", getPackageName());
            ((Button) findViewById(id)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OfferRented.this.page--;
                    getRentedCars();
                }
            });
            id = getResources().getIdentifier("offerRentedPageNext" + i, "id", getPackageName());
            ((Button) findViewById(id)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OfferRented.this.page++;
                    getRentedCars();
                }
            });
        }
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("token", this.token);
        intent.putExtra("fullname", this.fullname);
        setResult(RESULT_OK, intent);
        finish();
    }*/

    public void getRentedCars() {

        new AsyncTask<Void, Void, JSONObject>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    URL url = new URL(Utils.url + Utils.offerRented + "?token=" + OfferRented.this.token + "&page=" + OfferRented.this.page);

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
                                Intent intent = new Intent(OfferRented.this, Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), OfferRented.this);
                            return;
                        }

                        int id;
                        String text;
                        TextView textView;
                        Button button;

                        JSONObject data = json.getJSONObject("data");

                        int total = data.getInt("total");

                        text = "Zobrazených: " + ((OfferRented.this.page - 1) * 10 + 1) + " - " + Math.min(((OfferRented.this.page - 1) * 10 + 10), total) + " / " + total
                                + ", Strana: " + OfferRented.this.page + " / " + ((int) Math.ceil((double) total / 10));
                        for (int i = 1; i <= 2; i++) {
                            id = getResources().getIdentifier("offerRentedPageInfo" + i, "id", getPackageName());
                            textView = (TextView) findViewById(id);
                            textView.setText(text);
                            textView.setVisibility(View.VISIBLE);

                            id = getResources().getIdentifier("offerRentedPagePrevious" + i, "id", getPackageName());
                            button = (Button) findViewById(id);
                            if (OfferRented.this.page > 1) {
                                button.setVisibility(View.VISIBLE);
                            }
                            else {
                                button.setVisibility(View.INVISIBLE);
                            }

                            id = getResources().getIdentifier("offerRentedPageNext" + i, "id", getPackageName());
                            button = (Button) findViewById(id);
                            if (OfferRented.this.page < ((int) Math.ceil((double) total / 10))) {
                                button.setVisibility(View.VISIBLE);
                            }
                            else {
                                button.setVisibility(View.INVISIBLE);
                            }
                        }

                        for (int i = 1; i <= 10; i++) {
                            id = getResources().getIdentifier("offerRentedCar" + i, "id", getPackageName());
                            ((LinearLayout) findViewById(id)).setVisibility(View.GONE);
                        }

                        JSONArray offer = data.getJSONArray("offer");

                        for (int i = 0; i < offer.length(); i++) {
                            JSONObject car = offer.getJSONObject(i);

                            String carId = car.getString("id");

                            id = getResources().getIdentifier("offerRentedCar" + (i + 1), "id", getPackageName());
                            LinearLayout linearLayout = (LinearLayout) findViewById(id);
                            linearLayout.setVisibility(View.VISIBLE);
                            linearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(OfferRented.this, Return.class);

                                    intent.putExtra("token", OfferRented.this.token);
                                    intent.putExtra("fullname", OfferRented.this.fullname);
                                    intent.putExtra("carId", carId);

                                    startActivityForResult(intent, 1);
                                }
                            });

                            id = getResources().getIdentifier("offerRentedCarHeader" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("fullname"));

                            id = getResources().getIdentifier("offerRentedCarCarBodyStyle" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("type"));

                            id = getResources().getIdentifier("offerRentedTransmission" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("transmission"));

                            id = getResources().getIdentifier("offerRentedFuel" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("fuel"));

                            id = getResources().getIdentifier("offerRentedSeats" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("seats"));

                            id = getResources().getIdentifier("offerRentedPower" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("power"));

                            id = getResources().getIdentifier("offerRentedPrice" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("price_for_day"));

                            id = getResources().getIdentifier("offerRentedCarImage" + (i + 1), "id", getPackageName());
                            ImageView imageView = (ImageView) findViewById(id);
                            imageView.setImageResource(android.R.color.transparent);
                            new Utils.DownloadImageTask(imageView).execute(Utils.url + car.getString("image_location"));
                        }

                        ((ScrollView) findViewById(R.id.scrollView)).smoothScrollTo(0, 0);
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", OfferRented.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", OfferRented.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
