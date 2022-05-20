package com.example.fvfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class AccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // TOOLBAR
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        SharedPreferences savedUsername = getSharedPreferences("username", Context.MODE_PRIVATE);
        EditText editText = findViewById(R.id.accountUsernameEdit);
        editText.setText(savedUsername.getString("username", ""));

        Button button = findViewById(R.id.accountButton);
        button.setOnClickListener( (click) -> {
            SharedPreferences.Editor saveUsername = savedUsername.edit();
            String name = editText.getText().toString();
            saveUsername.putString("username", name);
            saveUsername.commit();
            // Closing the keyboard to see the Snackbar
            InputMethodManager imm = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
            View view = this.getCurrentFocus();
            if (view == null) {
                view = new View(this);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Snackbar.make(button, getString(R.string.usernameSaved), Snackbar.LENGTH_SHORT).show();
        });

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
                        .setMessage(R.string.howToAccount)
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