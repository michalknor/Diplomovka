package com.example.diplomovka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Collections;

public class Offer extends AppCompatActivity {
    String token;
    String fullname;

    int page = 1;

    String dataUrl;

    TextView textViewCarBodyStyle;
    String[] carBodyStyleArray;
    ArrayList<Integer> carBodyStyleList = new ArrayList<>();
    boolean[] selectedCarBodyStyle;

    TextView textViewTransmission;
    String[] transmissionArray;
    ArrayList<Integer> transmissionList = new ArrayList<>();
    boolean[] selectedTransmission;

    TextView textViewFuel;
    String[] fuelArray;
    ArrayList<Integer> fuelList = new ArrayList<>();
    boolean[] selectedFuel;

    /* access modifiers changed from: protected */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        Intent intent = getIntent();
        this.token = intent.getStringExtra("token");
        this.fullname = intent.getStringExtra("fullname");

        fillFilter();

        findViewById(R.id.offerSearchFilters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Offer.this.page = 1;

                Offer.this.dataUrl = "";
                Offer.this.dataUrl
                        = "&type=" + ((TextView) findViewById(R.id.offerCarBodyStyle)).getText()
                        + "&seats=" + ((TextView) findViewById(R.id.offerSeats)).getText()
                        + "&transmission=" + ((TextView) findViewById(R.id.offerTransmission)).getText()
                        + "&fuel=" + ((TextView) findViewById(R.id.offerFuel)).getText()
                        + "&power=" + ((TextView) findViewById(R.id.offerPowerFrom)).getText() + "-" + ((TextView) findViewById(R.id.offerPowerTo)).getText();
                Offer.this.dataUrl = Offer.this.dataUrl.replace(", ", ",");

                fillOffer();
            }
        });

        for (int i = 1; i <= 2; i++) {
            int id = getResources().getIdentifier("offerPagePrevious" + i, "id", getPackageName());
            ((Button) findViewById(id)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Offer.this.page--;
                    fillOffer();
                }
            });
            id = getResources().getIdentifier("offerPageNext" + i, "id", getPackageName());
            ((Button) findViewById(id)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Offer.this.page++;
                    fillOffer();
                }
            });
        }

        this.textViewCarBodyStyle = (TextView) findViewById(R.id.offerCarBodyStyle);
        this.textViewCarBodyStyle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Offer.this);
                builder.setTitle((CharSequence) "Vyberte z typov karosérií vozidla");
                builder.setCancelable(false);
                ArrayList<Integer> carBodyStyleListOld = new ArrayList<>(Offer.this.carBodyStyleList);
                boolean[] selectedCarBodyStyleOld = Offer.this.selectedCarBodyStyle.clone();

                builder.setMultiChoiceItems((CharSequence[]) Offer.this.carBodyStyleArray, Offer.this.selectedCarBodyStyle, (DialogInterface.OnMultiChoiceClickListener) new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i, boolean checked) {
                        if (checked) {
                            Offer.this.carBodyStyleList.add(i);
                            Collections.sort(Offer.this.carBodyStyleList);
                            return;
                        }
                        Offer.this.carBodyStyleList.remove(Integer.valueOf(i));
                    }
                });

                builder.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < Offer.this.carBodyStyleList.size(); j++) {
                            stringBuilder.append(Offer.this.carBodyStyleArray[Offer.this.carBodyStyleList.get(j).intValue()]);
                            if (j != Offer.this.carBodyStyleList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        Offer.this.textViewCarBodyStyle.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton((CharSequence) "Zrušiť", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Offer.this.carBodyStyleList = new ArrayList<>(carBodyStyleListOld);
                        Offer.this.selectedCarBodyStyle = selectedCarBodyStyleOld.clone();
                    }
                });

                builder.setNeutralButton((CharSequence) "Vymazať", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < Offer.this.selectedCarBodyStyle.length; j++) {
                            Offer.this.selectedCarBodyStyle[j] = false;
                            Offer.this.carBodyStyleList.clear();
                            Offer.this.textViewCarBodyStyle.setText("");
                        }
                    }
                });
                builder.show();
            }
        });

        this.textViewTransmission = (TextView) findViewById(R.id.offerTransmission);
        this.textViewTransmission.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Offer.this);
                builder.setTitle((CharSequence) "Vyberte z typov prevodoviek");
                builder.setCancelable(false);
                ArrayList<Integer> transmissionListOld = new ArrayList<>(Offer.this.transmissionList);
                boolean[] selectedTransmissionOld = Offer.this.selectedTransmission.clone();

                builder.setMultiChoiceItems((CharSequence[]) Offer.this.transmissionArray, Offer.this.selectedTransmission, (DialogInterface.OnMultiChoiceClickListener) new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i, boolean checked) {
                        if (checked) {
                            Offer.this.transmissionList.add(i);
                            Collections.sort(Offer.this.transmissionList);
                            return;
                        }
                        Offer.this.transmissionList.remove(Integer.valueOf(i));
                    }
                });

                builder.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < Offer.this.transmissionList.size(); j++) {
                            stringBuilder.append(Offer.this.transmissionArray[Offer.this.transmissionList.get(j).intValue()]);
                            if (j != Offer.this.transmissionList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        Offer.this.textViewTransmission.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton((CharSequence) "Zrušiť", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Offer.this.transmissionList = new ArrayList<>(transmissionListOld);
                        Offer.this.selectedTransmission = selectedTransmissionOld.clone();
                    }
                });

                builder.setNeutralButton((CharSequence) "Vymazať", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < Offer.this.selectedTransmission.length; j++) {
                            Offer.this.selectedTransmission[j] = false;
                            Offer.this.transmissionList.clear();
                            Offer.this.textViewTransmission.setText("");
                        }
                    }
                });
                builder.show();
            }
        });

        this.textViewFuel = (TextView) findViewById(R.id.offerFuel);
        this.textViewFuel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Offer.this);
                builder.setTitle((CharSequence) "Vyberte z typov paliva");
                builder.setCancelable(false);
                ArrayList<Integer> fuelListOld = new ArrayList<>(Offer.this.fuelList);
                boolean[] selectedFuelOld = Offer.this.selectedFuel.clone();

                builder.setMultiChoiceItems((CharSequence[]) Offer.this.fuelArray, Offer.this.selectedFuel, (DialogInterface.OnMultiChoiceClickListener) new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i, boolean checked) {
                        if (checked) {
                            Offer.this.fuelList.add(i);
                            Collections.sort(Offer.this.fuelList);
                            return;
                        }
                        Offer.this.fuelList.remove(Integer.valueOf(i));
                    }
                });

                builder.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < Offer.this.fuelList.size(); j++) {
                            stringBuilder.append(Offer.this.fuelArray[Offer.this.fuelList.get(j).intValue()]);
                            if (j != Offer.this.fuelList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        Offer.this.textViewFuel.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton((CharSequence) "Zrušiť", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Offer.this.fuelList = new ArrayList<>(fuelListOld);
                        Offer.this.selectedFuel = selectedFuelOld.clone();
                    }
                });

                builder.setNeutralButton((CharSequence) "Vymazať", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < Offer.this.selectedFuel.length; j++) {
                            Offer.this.selectedFuel[j] = false;
                            Offer.this.fuelList.clear();
                            Offer.this.textViewFuel.setText("");
                        }
                    }
                });
                builder.show();
            }
        });
    }

    /*@Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("token", this.token);
        intent.putExtra("fullname", this.fullname);
        setResult(RESULT_OK, intent);
        finish();
    }*/

    public void fillFilter() {

        new AsyncTask<Void, Void, JSONObject>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    URL url = new URL(Utils.url + Utils.offerFilters + "?token=" + Offer.this.token);

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
                                Intent intent = new Intent(Offer.this, Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), Offer.this);
                            return;
                        }
                        JSONObject data = json.getJSONObject("data");

                        JSONArray arr;
                        arr = data.getJSONArray("carBodyType");
                        Offer.this.carBodyStyleArray = new String[arr.length()];
                        for (int i = 0; i < arr.length(); i++) {
                            Offer.this.carBodyStyleArray[i] = arr.getString(i);
                        }
                        Offer.this.selectedCarBodyStyle = new boolean[Offer.this.carBodyStyleArray.length];

                        arr = data.getJSONArray("transmission");
                        Offer.this.transmissionArray = new String[arr.length()];
                        for (int i = 0; i < arr.length(); i++) {
                            Offer.this.transmissionArray[i] = arr.getString(i);
                        }
                        Offer.this.selectedTransmission = new boolean[Offer.this.transmissionArray.length];

                        arr = data.getJSONArray("fuel");
                        Offer.this.fuelArray = new String[arr.length()];
                        for (int i = 0; i < arr.length(); i++) {
                            Offer.this.fuelArray[i] = arr.getString(i);
                        }
                        Offer.this.selectedFuel = new boolean[Offer.this.fuelArray.length];
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Offer.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Offer.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void fillOffer() {

        new AsyncTask<Void, Void, JSONObject>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    URL url = new URL(Utils.url + Utils.offer + "?token=" + Offer.this.token + Offer.this.dataUrl + "&page=" + Offer.this.page);

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
                                Intent intent = new Intent(Offer.this, Login.class);

                                intent.putExtra("message", json.getString("message"));

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                finish();

                                return;
                            }

                            Utils.showWarning(json.getString("message"), Offer.this);
                            return;
                        }

                        int id;
                        String text;
                        TextView textView;
                        Button button;

                        JSONObject data = json.getJSONObject("data");

                        int total = data.getInt("total");

                        text = "Zobrazených: " + ((Offer.this.page - 1) * 10 + 1) + " - " + Math.min(((Offer.this.page - 1) * 10 + 10), total) + " / " + total
                                + ", Strana: " + Offer.this.page + " / " + ((int) Math.ceil((double) total / 10));
                        for (int i = 1; i <= 2; i++) {
                            id = getResources().getIdentifier("offerPageInfo" + i, "id", getPackageName());
                            textView = (TextView) findViewById(id);
                            textView.setText(text);
                            textView.setVisibility(View.VISIBLE);

                            id = getResources().getIdentifier("offerPagePrevious" + i, "id", getPackageName());
                            button = (Button) findViewById(id);
                            if (Offer.this.page > 1) {
                                button.setVisibility(View.VISIBLE);
                            }
                            else {
                                button.setVisibility(View.INVISIBLE);
                            }

                            id = getResources().getIdentifier("offerPageNext" + i, "id", getPackageName());
                            button = (Button) findViewById(id);
                            if (Offer.this.page < ((int) Math.ceil((double) total / 10))) {
                                button.setVisibility(View.VISIBLE);
                            }
                            else {
                                button.setVisibility(View.INVISIBLE);
                            }
                        }

                        for (int i = 1; i <= 10; i++) {
                            id = getResources().getIdentifier("offerCar" + i, "id", getPackageName());
                            ((LinearLayout) findViewById(id)).setVisibility(View.GONE);
                        }

                        JSONArray offer = data.getJSONArray("offer");

                        for (int i = 0; i < offer.length(); i++) {
                            JSONObject car = offer.getJSONObject(i);

                            String carId = car.getString("id");

                            id = getResources().getIdentifier("offerCar" + (i + 1), "id", getPackageName());
                            LinearLayout linearLayout = (LinearLayout) findViewById(id);
                            linearLayout.setVisibility(View.VISIBLE);
                            linearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Offer.this, Car.class);

                                    intent.putExtra("token", Offer.this.token);
                                    intent.putExtra("fullname", Offer.this.fullname);
                                    intent.putExtra("carId", carId);

                                    startActivityForResult(intent, 1);
                                }
                            });

                            id = getResources().getIdentifier("offerCarHeader" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("fullname"));

                            id = getResources().getIdentifier("offerCarCarBodyStyle" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("type"));

                            id = getResources().getIdentifier("offerTransmission" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("transmission"));

                            id = getResources().getIdentifier("offerFuel" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("fuel"));

                            id = getResources().getIdentifier("offerSeats" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("seats"));

                            id = getResources().getIdentifier("offerPower" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("power"));

                            id = getResources().getIdentifier("offerPrice" + (i + 1), "id", getPackageName());
                            ((TextView) findViewById(id)).setText(car.getString("price_for_day"));

                            id = getResources().getIdentifier("offerCarImage" + (i + 1), "id", getPackageName());
                            ImageView imageView = (ImageView) findViewById(id);
                            imageView.setImageResource(android.R.color.transparent);
                            new Utils.DownloadImageTask(imageView).execute(Utils.url + car.getString("image_location"));
                        }

                        ((ScrollView) findViewById(R.id.scrollView)).smoothScrollTo(0, 0);
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Offer.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Offer.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
