package com.habraham.parstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Like.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("abe-parstagram")
        .clientKey("LearnSomeParseMMM")
        .server("https://abe-parstagram.herokuapp.com/parse/").build());
    }
}
