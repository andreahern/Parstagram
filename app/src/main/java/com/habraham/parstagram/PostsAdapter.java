package com.habraham.parstagram;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.habraham.parstagram.fragments.DetailFragment;
import com.parse.ParseFile;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private static final String TAG = "PostsAdapter";

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gride_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
//        private TextView tvUsername;
//        private TextView tvDescription;
        private ImageView ivPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvUsername = itemView.findViewById(R.id.tvUsername);
//            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivPost = itemView.findViewById(R.id.ivGrideImage);
        }

        public void bind(Post post) {
//            tvDescription.setText(post.getDescription());
//            tvUsername.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(ivPost);
            }

            ivPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick");
                    int position = getAdapterPosition();
                    Post post = posts.get(position);
//                    Intent i = new Intent(context, DetailActivity.class);
//                    i.putExtra("Post", post.getObjectId());
//                    context.startActivity(i);
                    DetailFragment detailFragment = new DetailFragment(post.getObjectId());
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, detailFragment).addToBackStack(null).commit();
                }
            });
        }
    }
}
