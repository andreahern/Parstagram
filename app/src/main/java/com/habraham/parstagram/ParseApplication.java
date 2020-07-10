package com.habraham.parstagram;

import android.app.Application;

import com.habraham.parstagram.models.Comment;
import com.habraham.parstagram.models.Like;
import com.habraham.parstagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register Parse subclasses
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Like.class);
        ParseObject.registerSubclass(Comment.class);

        // Create Parse connection
        Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("abe-parstagram")
        .clientKey(getString(R.string.masterKey))
        .server("https://abe-parstagram.herokuapp.com/parse/").build());
    }
}
