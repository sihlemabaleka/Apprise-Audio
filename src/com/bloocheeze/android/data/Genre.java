package com.bloocheeze.android.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Genre")
public class Genre extends ParseObject {

	public String getTitle() {
		return getString("title");
	}

	public void setTitle(String value) {
		put("title", value);
	}

}
