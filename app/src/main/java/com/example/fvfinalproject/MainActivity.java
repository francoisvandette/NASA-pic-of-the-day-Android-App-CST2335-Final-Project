package com.example.fvfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String title;
    private String explanation;
    private String dateText;
    private static String datePicked;
    private String url;
    private String hdurl;
    private Bitmap pic;
    private String today;
    public static final String JSON_URL = "https://api.nasa.gov/planetary/apod?api_key=CHqd1uSvGVMw6ftr0I6Mr9p7Vh8d1og75ZUwBFQe";
    private NASAImageQuery req;
    private static TextView dateSelected;
    private ProgressBar progressBar;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Getting the username
        SharedPreferences username = getSharedPreferences("username", Context.MODE_PRIVATE);
        TextView userDisplay = findViewById(R.id.mainUsername);
        String name = username.getString("username", null);
        userDisplay.setText(getString(R.string.welcome)+" "+name+"!");

        // Opening the Database
        MyOpener myOpener = new MyOpener(this);
        db = myOpener.getWritableDatabase();

        // Progress Bar
        progressBar = findViewById(R.id.mainProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Grabs today's date and format it to match the NASA API format
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        today = formatter.format(new Date());

        // Set the Select Date Textview to today's date
        dateSelected = findViewById(R.id.mainDatePicked);
        dateSelected.setText(today);

        // Loads today's Image
        req = new NASAImageQuery();
        req.execute(JSON_URL);

        // TOOLBAR
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        // BUTTONS
        // Pick Date Button
        Button dateButton = findViewById(R.id.mainDatePickerButton);
        dateButton.setOnClickListener( (click) -> {
            showDatePickerDialog(dateButton);
        });

        // Fetch Image Button
        Button fetchButton = findViewById(R.id.mainDateFetchDateContent);
        fetchButton.setOnClickListener( (click) -> {
            String jsonTemp = JSON_URL + datePicked;
            NASAImageQuery req2 = new NASAImageQuery();
            req2.execute(jsonTemp);
        });

        // Favourite THIS image button
        Button favouriteThisImageButton = findViewById(R.id.mainFavouriteThisImage);
        favouriteThisImageButton.setOnClickListener( (click) -> {

            String toastResponse;
            String filename = title + ".png";
            // download the image
            if (!fileExistance(filename)) {
                saveNASAimage(title, url);
                toastResponse = getString(R.string.imageSaved);
            } else {
                toastResponse = getString(R.string.imageExists);
            }
            // set the following into the database: title, date, filepath
                // check to see if entry already exists
            Cursor results = db.rawQuery("select * from "+MyOpener.TABLE_NAME+" where DATE = ?", new String[] {dateText});
            if(results.getCount() == 0){
                ContentValues newrow = new ContentValues();
                newrow.put(MyOpener.COL_TITLE, title);
                newrow.put(MyOpener.COL_DATE, dateText);
                newrow.put(MyOpener.COL_FILENAME, filename);
                newrow.put(MyOpener.COL_HDURL, hdurl);
                newrow.put(MyOpener.COL_EXPLANATION, explanation);
                long id = db.insert(MyOpener.TABLE_NAME, "null", newrow);
            }
            // have toast popup
            Toast.makeText(MainActivity.this, toastResponse, Toast.LENGTH_SHORT).show();
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
                        .setMessage(R.string.howToMain)
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

    // AsyncTask
    public class NASAImageQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {

            try {
                // Reading the JSON
                URL tempurl = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) tempurl.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                // Setting the variables from the JSON file & publishing progressbar values
                JSONObject jsonObject = new JSONObject(result);
                title = jsonObject.getString("title");
                publishProgress(16);
                dateText = jsonObject.getString("date");
                publishProgress(32);
                explanation = jsonObject.getString("explanation");
                publishProgress(48);
                url = jsonObject.getString("url");
                publishProgress(64);
                hdurl = jsonObject.getString("hdurl");
                publishProgress(80);
                pic = downloadNASAimage(url);
                publishProgress(100);

            } catch(IOException | JSONException e) {

            }

            return "Done";
        }

        protected void onProgressUpdate(Integer ... value) { progressBar.setProgress(value[0]);};

        protected void onPostExecute(String fromDoInBackground) {
            // Getting the views
            ImageView layout_image = findViewById(R.id.mainImageView);
            TextView layout_title = findViewById(R.id.mainTitleContent);
            TextView layout_explanation = findViewById(R.id.mainExplanationContent);
            TextView layout_date = findViewById(R.id.mainDateContent);
            TextView layout_hdurl = findViewById(R.id.mainHDURLContent);

            // Setting the views
            layout_image.setImageBitmap(pic);
            layout_title.setText(title);
            layout_explanation.setText(explanation);
            layout_date.setText(dateText);
            layout_hdurl.setText(hdurl);
        }

        protected Bitmap downloadNASAimage(String link) {
            HttpURLConnection connection;
            try {
                URL imageURL = new URL(link);
                connection = (HttpURLConnection) imageURL.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    pic = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pic;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month++;
            String date;
            date = year + "-" + month + "-" + day;
            dateSelected.setText(date);
            datePicked = "&date=" + date;
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public boolean fileExistance(String filename) {
        File file = getBaseContext().getFileStreamPath(filename);
        return file.exists();
    }

    protected void saveNASAimage(String title, String url) {
        HttpURLConnection connection;
        Bitmap pic1;
        try {
            URL imageURL = new URL(url);
            connection = (HttpURLConnection) imageURL.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                pic1 = BitmapFactory.decodeStream(connection.getInputStream());
                FileOutputStream outputStream = openFileOutput( title + ".png", Context.MODE_PRIVATE);
                pic1.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}