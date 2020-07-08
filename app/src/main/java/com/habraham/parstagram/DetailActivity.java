package com.habraham.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    Post post;

    TextView tvUsername;
    TextView tvDescription;
    TextView tvTime;
    ImageView ivPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvTime = findViewById(R.id.tvTime);
        ivPost = findViewById(R.id.ivPostImage);

        String postID = getIntent().getStringExtra("Post");
        setPost(postID);
    }

    private void setPost(final String postID) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_OBJECT_ID, postID);
        final List<Post> rPosts = new ArrayList<>();
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                post = posts.get(0);
                tvUsername.setText(post.getUser().getUsername());
                tvDescription.setText(post.getDescription());
                tvTime.setText(setTime(post.getCreatedAt().toString()));
                Glide.with(DetailActivity.this).load(post.getImage().getUrl()).into(ivPost);
            }
        });
    }

    private String setTime(String createdAt) {
        String format = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}