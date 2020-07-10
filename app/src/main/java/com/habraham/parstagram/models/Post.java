package com.habraham.parstagram.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_COMMENTS = "comments";

    public static ArrayList<String> IDsofPostsLikedByCurrentUser = new ArrayList<>();

    public static void getPostsThatTheCurrentUserHasLiked(final FindCallback<Like> callback) {
       /* ParseQuery<Post> getAllPosts = ParseQuery.getQuery(Post.class);
        getAllPosts.include(Post.KEY_USER);
        getAllPosts.setLimit(limit);
        getAllPosts.addDescendingOrder(Post.KEY_CREATED_AT);
        getAllPosts.setSkip(limit * page);*/

        ParseQuery<Like> getLikedPosts = ParseQuery.getQuery(Like.class);
        getLikedPosts.whereEqualTo("user", ParseUser.getCurrentUser());
        //getLikedPosts.setLimit(limit);
        //getLikedPosts.addDescendingOrder(Post.KEY_CREATED_AT);
        //getLikedPosts.setSkip(limit * page);
        getLikedPosts.findInBackground(callback);
    }

    public ArrayList<Comment> getComments() {
        return (ArrayList<Comment>) get(KEY_COMMENTS);
    }

    public void setComments(ArrayList<Comment> comments) {
        put(KEY_COMMENTS, comments);
    }

    public void addComments(Comment comment) {
        add(KEY_COMMENTS, comment);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
       return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
