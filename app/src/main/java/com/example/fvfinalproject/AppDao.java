package com.example.fvfinalproject;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public class AppDao extends AppCompatActivity {

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
