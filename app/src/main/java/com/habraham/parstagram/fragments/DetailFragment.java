package com.habraham.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.habraham.parstagram.Post;
import com.habraham.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DetailFragment extends Fragment implements View.OnClickListener {
    Post post;
    String objectId;
    TextView tvUsername;
    TextView tvDescription;
    TextView tvTime;
    ImageView ivProfile;
    ImageView ivPost;

    public DetailFragment() {
        // Required empty public constructor
    }

    public DetailFragment(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvTime = view.findViewById(R.id.tvTime);
        ivProfile = view.findViewById(R.id.ivProfile);
        ivPost = view.findViewById(R.id.ivProfileImage);

        ivProfile.setOnClickListener(this);
        tvUsername.setOnClickListener(this);

        setPost(objectId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    private void setPost(final String postID) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_OBJECT_ID, postID);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                post = posts.get(0);
                tvUsername.setText(post.getUser().getUsername());
                tvDescription.setText(post.getDescription());
                tvTime.setText(setTime(post.getCreatedAt().toString()));

                if (post.getImage() != null)
                    Glide.with(getContext()).load(post.getImage().getUrl()).into(ivPost);

                ParseFile profilePic = post.getUser().getParseFile("profilePic");
                if (profilePic != null)
                    Glide.with(getContext()).load(profilePic.getUrl()).into(ivProfile);
                else
                    Glide.with(getContext()).load(R.drawable.default_pic).into(ivProfile);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivProfile:
            case R.id.tvUsername:
                ProfileFragment profileFragment = ProfileFragment.newInstance(post.getObjectId());
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, profileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
        }
    }
}