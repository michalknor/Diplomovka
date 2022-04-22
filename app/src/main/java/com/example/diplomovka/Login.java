package com.example.diplomovka;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {
    int backButtonCount = 0;

    EditText textUsername;
    EditText textPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        if (message != null) {
            Utils.showWarning(message, Login.this);
        }

        textUsername = findViewById(R.id.loginUsername);
        textPassword = findViewById(R.id.loginPassword);
        Button buttonLogin = findViewById(R.id.loginLogin);
        //login("knorm", "1234");

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username, password;
                username = String.valueOf(textUsername.getText());
                password = String.valueOf(textPassword.getText());
                if (username.equals("")) {
                    Utils.showWarning("Zadajte prihlasovacie meno!", Login.this);
                    return;
                }
                if (password.equals("")) {
                    Utils.showWarning("Zadajte heslo!", Login.this);
                    return;
                }

                login(username, password);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backButtonCount >= 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                Log.w("requestcode", requestCode + "");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.INTERNET},10);
                }
                break;
            default:
                break;
        }
    }*/

    public void login(final String username, final String password) {

        new AsyncTask<Void, Void, JSONObject>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    String passwordHashed = getSha256Hash(password);

                    URL url = new URL(Utils.url + Utils.login + "?username=" + username + "&password=" + passwordHashed);

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
                            Utils.showWarning(json.getString("message"), Login.this);
                            return;
                        }
                        JSONObject data = json.getJSONObject("data");

                        Intent intent = new Intent(Login.this, Menu.class);

                        intent.putExtra("token", data.getString("token"));
                        intent.putExtra("fullname", data.getString("fullname"));

                        startActivity(intent);
                        finish();
                    }
                    else {
                        Utils.showWarning("Vyskytla sa neočakávaná chyba", Login.this);
                    }
                }
                catch (JSONException e) {
                    Utils.showWarning("Vyskytla sa neočakávaná chyba", Login.this);
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public static String getSha256Hash(String password) {
        try {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            digest.reset();
            return bin2hex(digest.digest(password.getBytes()));
        } catch (Exception ignored) {
            return null;
        }
    }

    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
    }
}
