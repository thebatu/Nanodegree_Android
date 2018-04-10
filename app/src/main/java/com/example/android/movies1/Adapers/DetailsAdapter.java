package com.example.android.movies1.Adapers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies1.R;
import com.example.android.movies1.Models.Review;
import com.example.android.movies1.Models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String YOUTUBE_IMG_URL = "https://img.youtube.com/vi/";
    String finalUrl = "/0.jpg";
    public final int TRAILER_ID = 0;
    public final int REVIEWS_ID = 1;
    private DetailsClickListener mOnDetailsClickListener;
    private ReviewAdapterOnClickHandler mOnClick;
    private Context context;
    TextView detailsTextView;
    private ArrayList<Trailer> trailer;
    private ArrayList<Review> review;
    private ArrayList<Object> objects;


    public DetailsAdapter(DetailsClickListener mOnDetailsClickListener, ArrayList<Trailer> trailerArrayList,
                          ArrayList<Review> reviewArrayList, ReviewAdapterOnClickHandler mOnClick, ArrayList<Object> objects) {

        this.trailer = trailerArrayList;
        this.review = reviewArrayList;
        this.mOnDetailsClickListener = mOnDetailsClickListener;
        this.mOnClick = mOnClick;
        this.objects = objects;

    }


    public interface DetailsClickListener {
        void onListItemClick(int clickedItemPosition);
    }


    public interface ReviewAdapterOnClickHandler{
        void onClick(int click);
    }


    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView tv_trailer,reviews;
        public final ImageView icon;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            tv_trailer = (TextView) itemView.findViewById(R.id.trailer_name_view);
            icon = (ImageView) itemView.findViewById(R.id.iv);
            reviews= (TextView) itemView.findViewById(R.id.reviews);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnDetailsClickListener.onListItemClick(clickedPosition);
        }
    }


    public class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView tv_author;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            tv_author = (TextView) itemView.findViewById(R.id.tv_author);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition() - trailer.size();
            mOnClick.onClick(clickedPosition);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        switch (viewType) {
            case TRAILER_ID: {
                int layoutForTrailer = R.layout.activity_trailer;
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                boolean shouldAttachToParentImmediately = false;
                View view = layoutInflater.inflate(layoutForTrailer, parent, shouldAttachToParentImmediately);
                return new TrailerViewHolder(view);

            }
            case REVIEWS_ID: {
                int layoutForReviews = R.layout.activity_reviews;
                LayoutInflater layoutInflator = LayoutInflater.from(context);
                View view = layoutInflator.inflate(layoutForReviews, parent, false);
                return new ReviewsViewHolder(view);
            }
        }



        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TRAILER_ID:
                Trailer trailorList = trailer.get(position);

                ((TrailerViewHolder) holder).tv_trailer.setText(trailorList.getType());
                Picasso.with(context).load(YOUTUBE_IMG_URL.concat(trailorList.getKey()).concat(finalUrl)).placeholder(R.drawable.placeholder).fit().into(((TrailerViewHolder) holder).icon);

                break;
            case REVIEWS_ID:
                Review reviewList = review.get(position - trailer.size());
                ((ReviewsViewHolder) holder).tv_author.setText("".concat(reviewList.getAuthor()));

                break;
            default:
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                break;
        }

    }

    @Override
    public int getItemCount() {
//        if (trailer.size() != 0 &&  review.size() !=0 && trailer != null && review != null){
        if (trailer != null && review != null) {
            return review.size() + trailer.size();
        }
        else if (review == null) {
            return trailer.size();
        }

        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        if (position < trailer.size()) {
            return TRAILER_ID;
        } else {
            return REVIEWS_ID;
        }
    }


    public void setMovieData(ArrayList moviesData) {
        if (moviesData != null) {
            trailer = moviesData;
            notifyDataSetChanged();
        }
    }

    public void setReiewData(ArrayList reviewData){
        if(reviewData != null){
            review = reviewData;
            notifyDataSetChanged();
        }
    }

}