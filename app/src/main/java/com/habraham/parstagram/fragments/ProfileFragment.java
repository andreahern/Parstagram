package com.habraham.parstagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.habraham.parstagram.LoginActivity;
import com.habraham.parstagram.Post;
import com.habraham.parstagram.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostsFragment {
    private static final String TAG = "ProfileFragment";
    private ParseUser user;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String postId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String postId = getArguments().getString("postId");

            ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
            query.include(Post.KEY_USER);
            query.getInBackground(postId, new GetCallback<Post>() {
                @Override
                public void done(Post post, ParseException e) {
                    if (e == null) {
                        user = post.getUser();
                        Log.d(TAG, "onCreate: " + user.getUsername());
                        queryPosts();
                    }
                }
             });
        }
    }

    @Override
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        if (user == null)
            query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        else {
            Log.d(TAG, "queryPosts False");
            query.whereEqualTo(Post.KEY_USER, user);
        }

        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts: ", e);
                    return;
                }
                adapter.clear();
                adapter.addAll(posts);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = ((Toolbar)view.findViewById(R.id.toolbar));
        toolbar.setTitle("Test");
        toolbar.inflateMenu(R.menu.menu_profile);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Logout:
                        Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
                        ParseUser.logOut();
                        Intent i = new Intent(getContext(), LoginActivity.class);
                        startActivity(i);
                        getActivity().onBackPressed();
                        return true;
                    case R.id.setProfilePic:
                        Toast.makeText(getContext(), "set Profile", Toast.LENGTH_SHORT).show();
                        SetProfilePictureFragment profilePictureFragment = new SetProfilePictureFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.flContainer, profilePictureFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        return true;
                }
                return false;
            }
        });
    }
}
