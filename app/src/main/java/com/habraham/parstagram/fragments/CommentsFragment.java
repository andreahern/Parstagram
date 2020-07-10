package com.habraham.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.habraham.parstagram.R;
import com.habraham.parstagram.adapters.CommentsAdapter;
import com.habraham.parstagram.models.Comment;
import com.habraham.parstagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class CommentsFragment extends Fragment {
    private static final String TAG = "CommentsFragment";

    private static final String ARG_COMMENTS = "comments";
    public static final String ARG_POST = "post";

    private ArrayList<Comment> comments;
    Post post;

    Toolbar toolbar;
    RecyclerView rvComments;
    CommentsAdapter adapter;
    TextView etComment;
    Button btnSend;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public static CommentsFragment newInstance(ArrayList<Comment> comments, Post post) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_COMMENTS, comments);
        args.putParcelable(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            comments = getArguments().getParcelableArrayList(ARG_COMMENTS);
            post = getArguments().getParcelable(ARG_POST);
            if (comments == null) comments = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: " + comments);
        if (comments == null) comments = new ArrayList<>();

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        rvComments = view.findViewById(R.id.rvComments);
        adapter = new CommentsAdapter(getContext(), comments);
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        etComment = view.findViewById(R.id.etComment);
        btnSend = view.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentContent = etComment.getText().toString();
                if (commentContent.isEmpty()) return;

                final Comment comment = new Comment();
                comment.setContent(commentContent);
                comment.setPost(post);
                comment.setUser(ParseUser.getCurrentUser());
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (post.getComments() == null) {
                            ArrayList<Comment> comments = new ArrayList<>();
                            comments.add(comment);
                            Log.i(TAG, "done: " + comments);
                            post.setComments(comments);
                        } else {
                            post.addComments(comment);

                        }
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.i(TAG, "done: " + post.getComments());
                                adapter.add(comment);
                            }
                        });
                    }
                });

                etComment.setText(null);
            }
        });
    }
}