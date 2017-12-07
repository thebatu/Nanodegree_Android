package com.example.android.movies1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by batu on 05/12/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    ImageView imageView;
    private int mNumberItems;

    private List<Movie> mMovies;
    private Context mContext;

    // Pass in the movies array into the constructor


    private Context getContext() {
        return mContext;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context mContext = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        Movie movie = mMovies.get(position);
        holder.imageView.setText(movie);

    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void setWeatherData(ArrayList MoviesData) {
        mMovies = MoviesData;
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder  {
        ImageView listMovieNumberView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            listMovieNumberView = (ImageView) itemView.findViewById(R.id.im_item_number);

        }
    }







}
