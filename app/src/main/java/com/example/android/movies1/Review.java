package com.example.android.movies1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by batu on 04/02/18.
 *
 */

public class Review implements Parcelable {
    private String mId;
    private String author;
    private String content;
    private String url;

    public Review(String mId, String author, String content, String url) {
        this.mId = mId;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getmId() {
        return mId;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    protected Review(Parcel in) {
        mId = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }


    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

}

