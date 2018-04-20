package com.example.android.movies1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.movies1.Models.Review;

public class ReviewContent extends AppCompatActivity {

    TextView content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        content = findViewById(R.id.tv_reviews);

        Review item = getIntent().getParcelableExtra("reviews");
        if (item != null) {
            content.setText(item.getContent());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}