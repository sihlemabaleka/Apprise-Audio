package com.bloocheeze.android.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Artists")
public class Artist extends ParseObject{

	public String getArtistName() {
		return getString("artist_name");
	}

	public void setArtistName(String value) {
		put("artist_name", value);
	}

	public String getArtistDescription() {
		return getString("artist_description");
	}

	public void setArtistDescription(String value) {
		put("artist_description", value);
	}

	public void setArtistSpan(String value) {
		put("active_years", value);
	}

	public String getArtistSpan() {
		return getString("active_years");
	}

	public void setArtistGenre(String value) {
		put("artist_genre", value);
	}

	public String getArtistGenre() {
		return getString("genre");
	}

}

