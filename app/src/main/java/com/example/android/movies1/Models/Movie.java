package com.example.android.movies1.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by batu on 05/12/17.
 *
 */

public class Movie implements Parcelable {
    private String ID = "id";
    private String RELEASE_DATE = "release_date";
    private String VOTE_AVERAGE = "vote_average";
    private String POSTER_PATH = "poster_path";
    private String BACKDROP_PATH = "backdrop_path";
    private String OVERVIEW = "overview";
    private String TITLE = "original_title";



    public Movie() {}

    //constructor used for parcel
    private Movie(Parcel in){
        //read and set saved values from parcel
        ID = in.readString();
        RELEASE_DATE = in.readString();
        VOTE_AVERAGE = in.readString();
        POSTER_PATH = in.readString();
        BACKDROP_PATH = in.readString();
        OVERVIEW = in.readString();
        TITLE = in.readString();
    }

    public Movie(String id, String date, String rating, String poster, String backdrop, String overview, String title) {
        this.setID(id);
        this.setRELEASE_DATE(date);
        this.setVOTE_AVERAGE(rating);
        this.setPOSTER_PATH(poster);
        this.setBACKDROP_PATH(backdrop);
        this.setOVERVIEW(overview);
        this.setTITLE(title);

    }


    //return hashcode of object
    @Override
    public int describeContents() {
        return hashCode();
    }

//creator - used when un-parceling our parcle (creating the object)
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    //write object values to parcel for storage
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        //write all properties to the parcle
        dest.writeString(ID);
        dest.writeString(RELEASE_DATE);
        dest.writeString(VOTE_AVERAGE);
        dest.writeString(POSTER_PATH);
        dest.writeString(BACKDROP_PATH);
        dest.writeString(OVERVIEW);
        dest.writeString(TITLE);

    }


    public String getOVERVIEW() {
        return OVERVIEW;
    }

    public void setOVERVIEW(String OVERVIEW) {
        this.OVERVIEW = OVERVIEW;
    }

    public String getRELEASE_DATE() {
        return RELEASE_DATE;
    }

    public void setRELEASE_DATE(String RELEASE_DATE) {
        this.RELEASE_DATE = RELEASE_DATE;
    }

    public String getPOSTER_PATH() {
        return POSTER_PATH;
    }

    public void setPOSTER_PATH(String POSTER_PATH) {
        this.POSTER_PATH = POSTER_PATH;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getVOTE_AVERAGE() {
        return VOTE_AVERAGE;
    }

    public void setVOTE_AVERAGE(String VOTE_AVERAGE) {
        this.VOTE_AVERAGE = VOTE_AVERAGE;
    }

    public String getBACKDROP_PATH() {return BACKDROP_PATH;}

    public void setBACKDROP_PATH(String BACKDROP_PATH) {
        this.BACKDROP_PATH = BACKDROP_PATH;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }




}
