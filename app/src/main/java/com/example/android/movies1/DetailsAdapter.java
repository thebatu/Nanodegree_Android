package com.example.android.movies1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.TrailerViewHolder> {

    private static final String YOUTUBE_IMG_URL = "https://img.youtube.com/vi/";
    String finalUrl = "/0.jpg";
    private DetailsClickListener mOnDetailsClickListener;
    private Context context;
    TextView detailsTextView;
    private ArrayList dMovies;
    private ArrayList<Trailer> trailer;


    public interface DetailsClickListener {
        void onListItemClick(int clickedItemPosition);
    }

    public DetailsAdapter(Context applicationContext, DetailsClickListener listener, ArrayList<Trailer> trailer) {
        mOnDetailsClickListener = listener;
        context = applicationContext;
        this.trailer = trailer;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView icon;
        TextView listDetailsNumberView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            listDetailsNumberView = itemView.findViewById(R.id.tv_detail_number);
            icon = (ImageView) itemView.findViewById(R.id.iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
//            Movie clickedOnMovie = dMovies.get(clickedPosition);
            mOnDetailsClickListener.onListItemClick(clickedPosition);
        }
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context mContext = parent.getContext();
        int layoutIdForListItem = R.layout.details_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
       //int smovie = dMovies.get(position);
        Trailer trailerList = trailer.get(position);
        ((TrailerViewHolder) holder).listDetailsNumberView.setText(trailerList.getType());
        Picasso.with(context).load(YOUTUBE_IMG_URL.concat(trailerList.getKey()).concat(finalUrl)).placeholder(R.drawable.placeholder).fit().into(((TrailerViewHolder) holder).icon);

    }

    @Override
    public int getItemCount() {

        if (trailer !=null) return trailer.size(); else return 0;
    }

    //----------------------


    public void setMovieData(ArrayList moviesData) {
        if (moviesData != null) {
            trailer = moviesData;
            notifyDataSetChanged();
        }
    }


}
