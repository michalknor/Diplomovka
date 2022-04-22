package com.example.diplomovka;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class Menu extends AppCompatActivity {
    String token;
    String fullname;

    int backButtonCount = 0;

    TextView textUsername;
    TextView textPassword;

    JSONObject jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        this.token = intent.getStringExtra("token");
        this.fullname = intent.getStringExtra("fullname");
        String message = intent.getStringExtra("message");
        if (message != null) {
            Utils.showWarning(message, Menu.this);
        }

        TextView menuHeader = findViewById(R.id.headerText);
        menuHeader.setText("Vitaj, " + fullname);

        ((Button) findViewById(R.id.menuButtonOffer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Offer.class);

                intent.putExtra("token", token);
                intent.putExtra("fullname", fullname);

                startActivityForResult(intent, 1);
            }
        });

        ((Button) findViewById(R.id.menuButtonOfferRented)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, OfferRented.class);

                intent.putExtra("token", token);
                intent.putExtra("fullname", fullname);

                startActivityForResult(intent, 1);
            }
        });
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setTitle(intent.getStringExtra("token"));
        }
    }*/

    @Override
    public void onBackPressed() {
        if (backButtonCount >= 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            backButtonCount = 0;
        }
        else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}
