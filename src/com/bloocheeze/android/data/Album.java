package com.bloocheeze.android.data;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("albums")
public class Album extends ParseObject {

	public Artist getAlbumArtist() {
		Artist artist = null;
		try {
			artist = (Artist) getParseObject("artist").fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return artist;
	}

	public void setAlbumArtist(Artist value) {
		put("artist", value);
	}

	public String getAlbumDescription() {
		return getString("album_description");
	}

	public void setAlbumDescription(String value) {
		put("album_description", value);
	}

	public String getAlbumGenre() {
		return getString("album_genre");
	}

	public void setAlbumGenre(String value) {
		put("album_genre", value);
	}

	public void setAlbumReleaseYear(String value) {
		put("release_year", value);
	}

	public String getAlbumReleaseyear() {
		return getString("release_year");
	}

	public void setAlbumArt(ParseFile value) {
		put("album_art", value);
	}

	public String getAlbumArt() {
		return getParseFile("album_art").getUrl();
	}

	public void setAlbumName(String value) {
		// TODO Auto-generated method stub
		put("album_name", value);
	}

	public String getAlbumName() {
		// TODO Auto-generated method stub
		return getString("album_name");
	}

}
