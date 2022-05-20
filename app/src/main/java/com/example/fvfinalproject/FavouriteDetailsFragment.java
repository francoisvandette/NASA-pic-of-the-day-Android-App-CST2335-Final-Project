package com.example.fvfinalproject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FavouriteDetailsFragment extends Fragment {

    private AppCompatActivity parent;

    public FavouriteDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {

        Bundle data = getArguments();

        View result = inflater.inflate(R.layout.favourite_details_fragment, container, false);


//        TextView id = result.findViewById(R.id.favDeetsRowID);
//        id.setText("ID: " + data.getLong("ID"));


        TextView title = result.findViewById(R.id.favDeetsRowTitle);
        title.setText("\""+data.getString("TITLE")+"\"");


        TextView date = result.findViewById(R.id.favDeetsRowDate);
        date.setText(data.getString("DATE"));


        TextView explanation = result.findViewById(R.id.favDeetsRowExplanation);
        explanation.setText(data.getString("EXPLANATION"));


        TextView hdurl = result.findViewById(R.id.favDeetsRowURL);
        hdurl.setText(data.getString("HDURL"));


        ImageView pic = result.findViewById(R.id.favDeetsRowImage);
        pic.setImageBitmap(BitmapFactory.decodeFile(data.getString("FILEPATH")));

        Button hidebtn = result.findViewById(R.id.favDeetsHideBtn);
        hidebtn.setOnClickListener( (click) -> {
            parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
        });




        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (AppCompatActivity) context;
    }
}
