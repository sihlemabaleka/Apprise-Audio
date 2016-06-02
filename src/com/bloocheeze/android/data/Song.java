package com.bloocheeze.android.data;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("songs")
public class Song extends ParseObject {

	String songUrl, songTimeStamp;

	public String getId() {
		return getObjectId();
	}

	public String getSongTimeStamp() {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
				.format(getCreatedAt());
	}

	public String getSongStreamUrl() {
		return getParseFile("song").getUrl();
	}

	public void setSongMusicFile(ParseFile value) {
		put("song", value);
	}
}
