package com.bloocheeze.android;

import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;

import com.bloocheeze.android.adapter.AlbumAdapter;
import com.bloocheeze.android.data.Album;
import com.bloocheeze.android.data.Artist;
import com.bloocheeze.android.data.Genre;
import com.bloocheeze.android.data.Music;
import com.bloocheeze.android.helper.Utilities;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class AlbumsFragment extends Fragment {

	private GridView mGridView;
	private AlbumAdapter adapter;
	private ProgressDialog pDialog;
	

	private Utilities utils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.ui_albums_layout, container, false);
		utils = new Utilities(getActivity());
		mGridView = (GridView) v.findViewById(R.id.grid);
		InitilizeGridLayout();
		if (savedInstanceState == null)
			new GetAlbums().execute();
		return v;
	}

	class GetSongs extends AsyncTask<Void, Void, List<Music>> {

		@Override
		protected List<Music> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ParseQuery<Music> query = ParseQuery.getQuery(Music.class);
			query.setLimit(10);
			query.addDescendingOrder("createdAt");
			try {
				return query.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Music> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	class GetArtists extends AsyncTask<Void, Void, List<Artist>> {

		@Override
		protected List<Artist> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ParseQuery<Artist> query = ParseQuery.getQuery(Artist.class);
			query.addDescendingOrder("createdAt");
			query.setLimit(4);
			try {
				return query.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Artist> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	class GetGenres extends AsyncTask<Void, Void, List<Genre>> {

		@Override
		protected List<Genre> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ParseQuery<Genre> query = ParseQuery.getQuery(Genre.class);
			query.addDescendingOrder("createdAt");
			query.setLimit(4);
			try {
				return query.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(List<Genre> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	class GetAlbums extends AsyncTask<Void, Void, List<Album>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(getActivity());
			pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			pDialog.setMessage("Just A Second bru...");
			pDialog.show();
		}

		@Override
		protected List<Album> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ParseQuery<Album> query = ParseQuery.getQuery(Album.class);
			query.whereExists("album_art");
			query.addDescendingOrder("createdAt");
			try {
				return query.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Album> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result.size() > 0) {
				adapter = new AlbumAdapter(getActivity(), result);
				mGridView.setAdapter(adapter);
			}
		}

	}
	
	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());

		int columnWidth = (int) (utils.getScreenWidth() / 2);

		mGridView.setNumColumns(2);
		mGridView.setColumnWidth(columnWidth);
		mGridView.setStretchMode(GridView.NO_STRETCH);
		mGridView.setPadding((int) padding, (int) padding, (int) padding,
				(int) padding);
		mGridView.setHorizontalSpacing((int) padding);
		mGridView.setVerticalSpacing((int) padding);
	}

}
