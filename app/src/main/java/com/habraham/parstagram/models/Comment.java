package com.habraham.parstagram.models;

import com.habraham.parstagram.models.Post;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_CONTENT = "content";
    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";

    public String getContent() {
        return getString(KEY_CONTENT);
    }

    public void setContent(String content) {
        put(KEY_CONTENT, content);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Post getPost() {
        return (Post) get(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }
}
