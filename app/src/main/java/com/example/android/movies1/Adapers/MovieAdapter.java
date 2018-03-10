package com.example.android.movies1.Adapers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movies1.Movie;
import com.example.android.movies1.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    ImageView mImageView;
    private int mNumberItems;

    private List<Movie> mMovies;
    private Context mContext;

    private final MovieClickListener mOnMovieClickListener;

    private Context getContext() {
        return mContext;
    }

    public MovieAdapter (Context con, MovieClickListener listener){
        mContext = con;
        mOnMovieClickListener = listener;
//        mMovies = movies;
    }

    public interface MovieClickListener{
        void onMovieItemClick (int clickedItemPosition, Movie clickedOnMovie);
    }

    /*
    create new items in the form of view holders (public class MovieViewHolder)
     */
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context mContext = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    /*
    populate the items(view holders) with data

     */
    @Override
    public void onBindViewHolder(MovieViewHolder viewHolder, int position) {

        Movie movie = mMovies.get(position);
        Picasso.with(mContext).load(movie.getBACKDROP_PATH()).into(viewHolder.listMovieNumberView);

    }

    /*
    return information about the data. such as how many items in a given data source, this data may come from an arrayList or the result
    of network request ir any other data soure u can model
     */
    @Override
    public int getItemCount() {
    if (mMovies !=null) return mMovies.size(); else return 0;
    }

    public void setMovieData(ArrayList moviesData) {
        if (moviesData != null) {
            mMovies = moviesData;
        }

    }

    //-----------------------
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView listMovieNumberView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            listMovieNumberView = itemView.findViewById(R.id.im_item_number);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Movie clickedOnMovie = mMovies.get(clickedPosition);
            mOnMovieClickListener.onMovieItemClick(clickedPosition, clickedOnMovie);
        }
    }
}
