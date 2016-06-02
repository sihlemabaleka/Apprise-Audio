package com.bloocheeze.android.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloocheeze.android.PlayerFragment;
import com.bloocheeze.android.R;
import com.bloocheeze.android.data.Album;
import com.squareup.picasso.Picasso;

public class AlbumAdapter extends BaseAdapter {

	Activity activity;
	List<Album> albums;

	public AlbumAdapter(Activity activity, List<Album> albums) {
		super();
		this.activity = activity;
		this.albums = albums;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return albums.size();
	}

	@Override
	public Album getItem(int position) {
		// TODO Auto-generated method stub
		return albums.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder vh;
		if (convertView == null) {
			convertView = View.inflate(activity, R.layout.ui_album_item, null);
			vh = new ViewHolder();
			vh.image = (ImageView) convertView.findViewById(R.id.album_art);
			vh.albumTitle = (TextView) convertView.findViewById(R.id.album);
			vh.artistName = (TextView) convertView.findViewById(R.id.artist);
			vh.mPlayButton = (ImageButton) convertView
					.findViewById(R.id.play_button);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		vh.image.setTag(R.id.album_art, albums.get(position));
		Picasso.with(convertView.getContext())
				.load(albums.get(position).getAlbumArt()).into(vh.image);

		vh.albumTitle.setText(albums.get(position).getAlbumName());

		vh.artistName.setText(albums.get(position).getAlbumArtist()
				.getArtistName());

		vh.mPlayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.getFragmentManager()
						.beginTransaction()
						.add(R.id.container,
								PlayerFragment.newInstance(getItem(position)
										.getObjectId(), "album"))
						.addToBackStack("albums").commit();
			}
		});

		return convertView;
	}

	private static class ViewHolder {
		ImageView image;
		TextView albumTitle, artistName;
		ImageButton mPlayButton;
	}
}