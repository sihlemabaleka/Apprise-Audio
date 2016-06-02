package com.bloocheeze.android.data;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("Music")
public class Music extends ParseObject {

	public Artist getSongArtist() {
		Artist artist = null;
		try {
			artist = (Artist) getParseObject("artist").fetchIfNeeded();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return artist;
	}

	public void setSongArtist(Artist value) {
		put("artist", value);
	}

	public String getSongNumber() {
		return getString("track_number");
	}

	public void setSongNumber(String value) {
		put("track_number", value);
	}

	public String getSongTitle() {
		return getString("title");
	}

	public void setSongTitle(String value) {
		put("title", value);
	}

	public Album getSongAlbum() {
		Album album = null;
		try {
			album = (Album) getParseObject("album").fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return album;
	}

	public void setSongAlbum(Album value) {
		put("album", value);
	}

	public String getSongFeaturedArtist() {
		return getString("featured_artist");
	}

	public void setSongFeaturedArtist(String value) {
		put("featured_artist", value);
	}

	public Song getSong() {
		Song song = null;
		try {
			song = (Song) getParseObject("song").fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return song;
	}

	public void setSong(Song value) {
		put("song", value);
	}
}
