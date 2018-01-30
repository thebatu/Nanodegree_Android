package com.example.android.movies1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {

    private DetailsClickListener mOnDetailsClickListener;
    private Context con;
    TextView detailsTextView;
    private List<Movie> dMovies;

    public DetailsAdapter(Context applicationContext, DetailsClickListener listener) {
        mOnDetailsClickListener = listener;
        con = applicationContext;
    }

    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(DetailsViewHolder holder, int position) {

    }




    @Override
    public int getItemCount() {
        return 0;
    }

    public interface DetailsClickListener {

    }

    public void setMovieData(ArrayList moviesData) {
        if (moviesData != null) {
            dMovies = moviesData;
        }
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView listDetailsNumerView;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            listDetailsNumerView = itemView.findViewById(R.id.tv_detail_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();


        }
    }
}
