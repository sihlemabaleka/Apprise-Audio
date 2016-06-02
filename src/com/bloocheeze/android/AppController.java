package com.bloocheeze.android;

import android.app.Application;

import com.bloocheeze.android.data.Album;
import com.bloocheeze.android.data.Artist;
import com.bloocheeze.android.data.Music;
import com.bloocheeze.android.data.Song;
import com.parse.Parse;
import com.parse.ParseObject;

public class AppController extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ParseObject.registerSubclass(Music.class);
		ParseObject.registerSubclass(Artist.class);
		ParseObject.registerSubclass(Album.class);
		ParseObject.registerSubclass(Song.class);
		Parse.enableLocalDatastore(this);
		Parse.initialize(this);
	}
}
