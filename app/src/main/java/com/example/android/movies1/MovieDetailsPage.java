package com.example.android.movies1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MovieDetailsPage extends AppCompatActivity {

    String TAG = MovieDetailsPage.class.getSimpleName();
    TextView tvName;
//    Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_page);

        tvName = findViewById(R.id.title);
//        Bundle bundle =  getIntent().getExtras();

//        String s = bundle.getString("id");

        Movie movie_obj = getIntent().getParcelableExtra("movie");
        int i = 0;
        Log.i(TAG, "IN MovieDetail page");

//        tvName.setText(s);
    }
}
