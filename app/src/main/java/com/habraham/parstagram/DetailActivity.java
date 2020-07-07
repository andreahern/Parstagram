package com.habraham.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    Post post;

    TextView tvUsername;
    TextView tvDescription;
    ImageView ivPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
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
                Glide.with(DetailActivity.this).load(post.getImage().getUrl()).into(ivPost);
            }
        });
    }
}