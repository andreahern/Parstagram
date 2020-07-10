package com.habraham.parstagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.habraham.parstagram.EndlessRecyclerViewScrollListener;
import com.habraham.parstagram.LoginActivity;
import com.habraham.parstagram.models.Post;
import com.habraham.parstagram.adapters.PostsAdapter;
import com.habraham.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private ParseUser user;

    RecyclerView rvPosts;
    PostsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    List<Post> allPosts;
    EndlessRecyclerViewScrollListener scrollListener;
    TextView tvUsername;
    ImageView ivProfile;

    public ProfileFragment() {
    }

    // Create a new instance of ProfileFragment with args
    public static ProfileFragment newInstance(ParseUser user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ParseUser pUser = getArguments().getParcelable("user");
            user = pUser;
            queryPosts();
        } else queryPosts();
    }

    // Query initial posts to be displayed
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        if (user == null)
            query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        else {
            query.whereEqualTo(Post.KEY_USER, user);
        }

        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        // Makes query to parse backend
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfile = view.findViewById(R.id.ivProfile);

        tvUsername.setText(user.getUsername());
        ParseFile image = user.getParseFile("profilePic");
        if (image == null)
            Glide.with(getContext()).load(R.drawable.default_pic).transform(new CircleCrop()).into(ivProfile);
        else
            Glide.with(getContext()).load(user.getParseFile("profilePic").getUrl()).transform(new CircleCrop()).into(ivProfile);

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);

        GridLayoutManager glm = new GridLayoutManager(getContext(), 3);
        rvPosts.setLayoutManager(glm);

        // Pull down to refresh
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                queryPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Listens for when user scrolls to the bottom
        scrollListener = new EndlessRecyclerViewScrollListener(glm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextQueryPosts(allPosts.get(allPosts.size() - 1).getCreatedAt());
            }
        };
        rvPosts.addOnScrollListener(scrollListener);

        Toolbar toolbar = ((Toolbar) view.findViewById(R.id.toolbar));
        toolbar.inflateMenu(R.menu.menu_profile);

        // Listens for when a toolbar menu item was been clicked
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

        queryPosts();
    }

    // Loads next set of data once user scrolls to the end
    protected void loadNextQueryPosts(Date maxCreatedAt) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);

        if (user == null)
            query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        else {
            query.whereEqualTo(Post.KEY_USER, user);
        }

        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.whereLessThan(Post.KEY_CREATED_AT, maxCreatedAt);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting next posts: ", e);
                    return;
                }

                for (Post post : posts) {
                    Log.i(TAG, "Next Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                adapter.addAll(posts);
            }
        });
    }
}
