package com.habraham.parstagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.habraham.parstagram.EndlessRecyclerViewScrollListener;
import com.habraham.parstagram.models.Like;
import com.habraham.parstagram.models.Post;
import com.habraham.parstagram.adapters.PostsAdapter;
import com.habraham.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {
    private static final String TAG = "PostsFragment";

    protected RecyclerView rvPosts;
    protected SwipeRefreshLayout swipeContainer;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;
    protected EndlessRecyclerViewScrollListener scrollListener;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);

        GridLayoutManager glm = new GridLayoutManager(getContext(), 3);
        rvPosts.setLayoutManager(glm);

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

        scrollListener = new EndlessRecyclerViewScrollListener(glm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextQueryPosts(page);
            }
        };

        rvPosts.addOnScrollListener(scrollListener);

        queryPosts();
    }

    protected void queryPosts() {
        Post.getPostsThatTheCurrentUserHasLiked(new FindCallback<Like>() {
            @Override
            public void done(List<Like> objects, ParseException e) {
                Post.IDsofPostsLikedByCurrentUser.clear();
                for (Like like : objects) {
                    Post.IDsofPostsLikedByCurrentUser.add(like.getPost().getObjectId());
                }
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.include(Post.KEY_USER);
                query.setLimit(20);
                query.addDescendingOrder(Post.KEY_CREATED_AT);
                query.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting posts: ", e);
                            return;
                        }

                        for (Post post : posts) {
                            Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                        }

                        adapter.addAll(posts);
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });


    }

    protected void loadNextQueryPosts(final int page) {
            ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
            query.include(Post.KEY_USER);

            query.setLimit(20);
            query.addDescendingOrder(Post.KEY_CREATED_AT);
            query.setSkip(20 * page);
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