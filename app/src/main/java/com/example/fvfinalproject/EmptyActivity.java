package com.example.fvfinalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity_layout);

        Bundle dataToPass = getIntent().getExtras();


        FavouriteDetailsFragment df = new FavouriteDetailsFragment();
        df.setArguments( dataToPass ); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.favFragFrame, df)
                .commit();


    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
