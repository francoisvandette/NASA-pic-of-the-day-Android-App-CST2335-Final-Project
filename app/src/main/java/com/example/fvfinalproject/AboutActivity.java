package com.example.fvfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class AboutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // TOOLBAR
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);


    }

    // Beginning of Toolbar functions
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.navMenuHome:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.navMenuFavourites:
                startActivity(new Intent(this, FavouritesActivity.class));
                break;
            case R.id.navMenuAccount:
                startActivity(new Intent(this, AccountActivity.class));
                break;
            case R.id.navMenuAbout:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.help_button, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.helpbtn:

                AlertDialog.Builder alertDeleteMsg = new AlertDialog.Builder(this);
                alertDeleteMsg.setTitle(R.string.howToUse)
                        .setMessage(R.string.howToAbout)
                        .setNegativeButton(R.string.close, (click, arg) -> {
                            // nothing
                        })
                        .create().show();
                break;
            case R.id.helpbtnHome:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.helpbtnFav:
                startActivity(new Intent(this, FavouritesActivity.class));
                break;
            case R.id.helpbtnAccount:
                startActivity(new Intent(this, AccountActivity.class));
                break;
            case R.id.helpbtnAbout:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return true;
    }
    // End of Toolbar functions
}