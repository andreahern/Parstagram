package com.habraham.parstagram.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.habraham.parstagram.R;
import com.habraham.parstagram.models.Comment;
import com.habraham.parstagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private Context context;
    private List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    // Create a new view holder to contain a comment
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    // Add element to array and notify adapter so element will be rendered
    public void add(Comment comment) {
        comments.add(comment);
        notifyItemInserted(comments.size());
    }

    @Override
    // Bind new data to a existing view holder
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        try {
            holder.bind(comment);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    // Get number of comments
    public int getItemCount() {
        return comments.size();
    }

    // Define how a ViewHolder functions
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBody;
        ImageView ivProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        }

        // Helper function that binds the comment data to the view holder
        public void bind(Comment comment) throws ParseException {
            tvBody.setText(comment.fetchIfNeeded().getString(Comment.KEY_CONTENT));
            ParseFile image = comment.getUser().fetchIfNeeded().getParseFile("profilePic");
            if (image == null)
                Glide.with(context).load(R.drawable.default_pic).transform(new CircleCrop()).into(ivProfileImage);
            else
            Glide.with(context).load(comment.fetchIfNeeded().getParseUser(Comment.KEY_USER).fetchIfNeeded().getParseFile("profilePic").getUrl()).transform(new CircleCrop()).into(ivProfileImage);
        }
    }
}
