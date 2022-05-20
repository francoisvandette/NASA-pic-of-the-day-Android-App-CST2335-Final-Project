package com.example.fvfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ListView listview;
    ArrayList<NASAimage> imagelist;
    SQLiteDatabase db;
    TheAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        // TOOLBAR
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);


        imagelist = new ArrayList<>();
        adapter = new TheAdapter();
        listview = findViewById(R.id.favouritesListview);
        listview.setAdapter(adapter);

        loadDataFromDatabase();

        // List Item click, details viewer
        listview.setOnItemClickListener( (list, item, position, id) -> {

            Bundle dataToPass = new Bundle();
//            dataToPass.putLong("ID", imagelist.get(position).getId());
            dataToPass.putString("TITLE", imagelist.get(position).getTitle());
            dataToPass.putString("DATE", imagelist.get(position).getDate());
            dataToPass.putString("EXPLANATION", imagelist.get(position).getExplanation());
            dataToPass.putString("HDURL", imagelist.get(position).getHDurl());
            dataToPass.putString("FILEPATH", getBaseContext().getFileStreamPath(imagelist.get(position).getFilename()).getPath());

            FavouriteDetailsFragment df = new FavouriteDetailsFragment();
            df.setArguments( dataToPass ); //pass data to the the fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.favFragFrame, df)
                    .commit();
        });

        // Long click, to bring up alert dialog to delete
        listview.setOnItemLongClickListener( (list, item, position, id) -> {

            // popup to ask if the user wants to delete
            String delTitle = imagelist.get(position).getTitle();
            String delDate = imagelist.get(position).getDate();
            String delId = String.valueOf(imagelist.get(position).getId());
            String delFilename = imagelist.get(position).getFilename();
            AlertDialog.Builder alertDeleteMsg = new AlertDialog.Builder(this);
            alertDeleteMsg.setTitle(R.string.likeToDelete)
                    .setMessage("\""+ delTitle + "\", the picture-of-the-day from " + delDate + "?")
                    .setPositiveButton(getString(R.string.yes), (click, arg) -> {
                        // delete from the database
                        db.delete(MyOpener.TABLE_NAME, "_id=?", new String[] {delId});
                        // remove from the list
                        imagelist.remove(position);
                        // delete local file, if it exists
                        File file = getBaseContext().getFileStreamPath(delFilename);
                        if (file.exists()) {
                            file.delete();
                        }
                        // adapter notify
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(getString(R.string.no), (click, arg) -> {
                        // nothing
                    })
                    .create().show();

            return true;
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
                        .setMessage(R.string.howToFavs)
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

    public class TheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imagelist.size();
        }

        @Override
        public NASAimage getItem(int position) {
            return imagelist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            View newView = null;
            LayoutInflater inflater = getLayoutInflater();
            ImageView imageViewRow;
            TextView titleViewRow;
            TextView dateViewRow;
            NASAimage nasaImage = (NASAimage) getItem(position);

            newView = inflater.inflate(R.layout.favourite_row, parent, false);

            imageViewRow = newView.findViewById(R.id.favRowImage);

//            imageViewRow.setImageBitmap(BitmapFactory.decodeFile("/data/data/com.example.fvfinalproject/files/"+nasaImage.getFilename()));
            // Load downloaded image
            imageViewRow.setImageBitmap(BitmapFactory.decodeFile(getBaseContext().getFileStreamPath(nasaImage.getFilename()).getPath()));

            titleViewRow = newView.findViewById(R.id.favRowTitle);
            titleViewRow.setText(nasaImage.getTitle());

            dateViewRow = newView.findViewById(R.id.favRowDate);
            dateViewRow.setText(nasaImage.getDate());

            return newView;
        }
    }

    private void loadDataFromDatabase() {

        // connection
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        String [] columns = {MyOpener.COL_ID, MyOpener.COL_TITLE, MyOpener.COL_DATE, MyOpener.COL_FILENAME, MyOpener.COL_EXPLANATION, MyOpener.COL_HDURL};

        Cursor results = db.query( MyOpener.TABLE_NAME, columns, null, null, null, null, null);

        int idColumnIndex = results.getColumnIndex(MyOpener.COL_ID);
        int titleColumnIndex = results.getColumnIndex(MyOpener.COL_TITLE);
        int dateColumnIndex = results.getColumnIndex(MyOpener.COL_DATE);
        int filenameColumnIndex = results.getColumnIndex(MyOpener.COL_FILENAME);
        int explanationColumnIndex = results.getColumnIndex(MyOpener.COL_EXPLANATION);
        int hdurlColumnIndex = results.getColumnIndex(MyOpener.COL_HDURL);

        while( results.moveToNext() ){
            long id = results.getLong(idColumnIndex);
            String title = results.getString(titleColumnIndex);
            String dateText = results.getString(dateColumnIndex);
            String filename = results.getString(filenameColumnIndex);
            String explanation = results.getString(explanationColumnIndex);
            String hdurl = results.getString(hdurlColumnIndex);

            imagelist.add(new NASAimage(id, title, dateText, filename, explanation, hdurl));
        }
        results.close();
    }

    public boolean fileExistance(String filename) {
        File file = getBaseContext().getFileStreamPath(filename);
        return file.exists();
    }
}