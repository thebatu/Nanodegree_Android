package com.example.android.movies1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {

    private DetailsClickListener mOnDetailsClickListener;
    private Context con;
    TextView detailsTextView;
    private ArrayList dMovies;

    public DetailsAdapter(Context applicationContext, DetailsClickListener listener) {
        mOnDetailsClickListener = listener;
        con = applicationContext;
    }

    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context mContext = parent.getContext();
        int layoutIdForListItem = R.layout.details_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        DetailsViewHolder viewHolder = new DetailsViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(DetailsViewHolder holder, int position) {
       //int smovie = dMovies.get(position);

    }

    @Override
    public int getItemCount() {

        if (dMovies !=null) return dMovies.size(); else return 0;
    }

    //----------------------
    public interface DetailsClickListener {
        void onMovieItemClick(int clickedItemPosition, Movie clickedOnMovie);

    }

    public void setMovieData(ArrayList moviesData) {
        if (moviesData != null) {
            dMovies = moviesData;
        }
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView listDetailsNumberView;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            listDetailsNumberView = itemView.findViewById(R.id.tv_detail_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            int clickedPosition = getAdapterPosition();
//            Movie clickedOnMovie = dMovies.get(clickedPosition);
//            mOnDetailsClickListener.onMovieItemClick(clickedPosition, clickedOnMovie);
        }
    }
}
