package com.example.android.movies1.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bats on 2/1/18.
 *
 */

    public class Trailer implements Parcelable {

        private String mId;
        private String name;
        private String key;
        private String site;
        private String size;
        private String type;

        public Trailer(String mId, String name, String key, String site, String size, String type) {
            this.mId = mId;
            this.name = name;
            this.key = key;
            this.site = site;
            this.size = size;
            this.type = type;
        }

        public String getmId() {
            return mId;
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }

        public String getSite() {
            return site;
        }

        public String getSize() {
            return size;
        }

        public String getType() {
            return type;
        }


        protected Trailer(Parcel in) {
            mId = in.readString();
            name = in.readString();
            key = in.readString();
            site = in.readString();
            size = in.readString();
            type = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mId);
            dest.writeString(name);
            dest.writeString(key);
            dest.writeString(site);
            dest.writeString(size);
            dest.writeString(type);
        }


        public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
            @Override
            public Trailer createFromParcel(Parcel in) {
                return new Trailer(in);
            }

            @Override
            public Trailer[] newArray(int size) {
                return new Trailer[size];
            }
        };
}

