package com.example.android.movies1;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailsPage extends AppCompatActivity {

     String TAG = MovieDetailsPage.class.getSimpleName();
     TextView movieTitle;
     ImageView movieImage;
     TextView movieOverview;
     TextView movieDate;
     TextView movieRating;
//    Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_page);

        Context context = this.getApplicationContext();

        movieTitle = findViewById(R.id.title);
        movieImage = findViewById(R.id.poster_image);
        movieOverview= findViewById(R.id.overview);
        movieDate= findViewById(R.id.date);
        movieRating= findViewById(R.id.rate);
//        Bundle bundle =  getIntent().getExtras();

//        String s = bundle.getString("id");

        Movie movie_obj = getIntent().getParcelableExtra("movie_obj");
        if (movie_obj != null ) {
            String posterPath = movie_obj.getBACKDROP_PATH();
            Picasso.with(context).load(posterPath).into(movieImage);

            String titleOfMovie = movie_obj.getTITLE();
            movieTitle.setText(titleOfMovie);

            String overView = movie_obj.getOVERVIEW();
            movieOverview.setText(overView);

            String date = movie_obj.getRELEASE_DATE();
            movieDate.setText(date);





        }
        Log.i(TAG, "in MovieDetail page");
        //movieImage.setImageBitmap(movie_obj.getBACKDROP_PATH());


//        tvName.setText(s);
    }
}
