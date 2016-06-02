package com.bloocheeze.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;
import android.widget.Toast;

import com.bloocheeze.android.data.Album;
import com.bloocheeze.android.data.Music;
import com.bloocheeze.android.data.Song;
import com.bloocheeze.android.helper.Utilities;
import com.bloocheeze.android.player.MusicController;
import com.bloocheeze.android.views.CircularSeekBar;
import com.bloocheeze.android.views.CircularSeekBar.OnCircularSeekBarChangeListener;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PlayerFragment extends Fragment implements OnClickListener,
		MediaPlayerControl, MediaPlayer.OnPreparedListener,
		MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
		MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

	// media player
	private MediaPlayer player;

	private boolean shuffle = false;
	private Random rand;

	private ImageButton pressPlay;

	private List<Music> mListItems = new ArrayList<Music>();

	private MusicController controller;

	private int SongPosition = 0;

	private boolean playbackPaused = false;

	ParseInstallation installation;

	private ImageButton btnNext, btnPrevious, btnRepeat;
	private CircularSeekBar progress;
	private TextView mTitle, mName, mAlbumTitle, mElapsedTime;

	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private Utilities utils;

	private Boolean isRepeat = false, isPrepared = false;

	private ImageView albumArt, backgroundArt;

	public static PlayerFragment newInstance(String value, String type) {
		PlayerFragment fragment = new PlayerFragment();
		Bundle args = new Bundle();
		args.putString("objectId", value);
		args.putString("type", type);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		pressPlay = (ImageButton) v.findViewById(R.id.play);
		progress = (CircularSeekBar) v.findViewById(R.id.track_seekbar);
		mName = (TextView) v.findViewById(R.id.artist_name);
		mTitle = (TextView) v.findViewById(R.id.song_title);
		mAlbumTitle = (TextView) v.findViewById(R.id.album_title);
		btnPrevious = (ImageButton) v.findViewById(R.id.previous);
		btnNext = (ImageButton) v.findViewById(R.id.next);
		btnRepeat = (ImageButton) v.findViewById(R.id.repeat);
		mElapsedTime = (TextView) v.findViewById(R.id.elapsed_time);
		albumArt = (ImageView) v.findViewById(R.id.album_art);
		backgroundArt = (ImageView) v.findViewById(R.id.background_art);

		// create player
		player = new MediaPlayer();
		initMusicPlayer();
		utils = new Utilities();
		rand = new Random();

		setController();
		pressPlay.setOnClickListener(this);
		btnRepeat.setOnClickListener(this);

		progress.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(CircularSeekBar seekBar) {
				// TODO Auto-generated method stub
				mHandler.removeCallbacks(mUpdateTimeTask);
				int totalDuration = player.getDuration();
				int currentPosition = utils.progressToTimer(
						seekBar.getProgress(), totalDuration);

				mElapsedTime.setText(utils.milliSecondsToTimer(currentPosition)
						+ " - " + utils.milliSecondsToTimer(totalDuration));
				// forward or backward to certain seconds
				player.seekTo(currentPosition);

				// update timer progress again
				updateProgressBar();
			}

			@Override
			public void onStartTrackingTouch(CircularSeekBar seekBar) {
				// TODO Auto-generated method stub
				mHandler.removeCallbacks(mUpdateTimeTask);
			}

			@Override
			public void onProgressChanged(CircularSeekBar seekBar,
					int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				int totalDuration = player.getDuration();
				int currentPosition = utils.progressToTimer(
						seekBar.getProgress(), totalDuration);
				mElapsedTime.setText(utils.milliSecondsToTimer(currentPosition)
						+ " - " + utils.milliSecondsToTimer(totalDuration));
			}
		});

		new getPlaylist().execute();

		btnPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playPrev();
			}
		});

		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				playNext();
			}
		});

		return v;
	}

	public void initMusicPlayer() {
		// set player properties
		player.setWakeMode(getActivity(), PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}

	private void setController() {
		controller = new MusicController(getActivity());
		controller.setPrevNextListeners(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				playNext();
			}
		}, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				playPrev();
			}
		});

		controller.setMediaPlayer(this);
		controller.setEnabled(true);
		controller.setTag(R.id.artist_name);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.play_button:
			if (player.isPlaying()) {
				playbackPaused = true;
				player.pause();
				pressPlay.setImageResource(R.drawable.ic_play);
			} else {
				playbackPaused = false;
				player.start();
				pressPlay.setImageResource(R.drawable.ic_pause);
			}
			break;
		case R.id.repeat:
			if (isRepeat) {
				isRepeat = false;
				Toast.makeText(getActivity(), "Repeat is OFF",
						Toast.LENGTH_SHORT).show();
			} else {
				// make repeat to true
				isRepeat = true;
				Toast.makeText(getActivity(), "Repeat is ON",
						Toast.LENGTH_SHORT).show();
				// make shuffle to false
				shuffle = false;
			}
			break;
		}
	}

	public class getPlaylist extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated methodstub
			super.onPreExecute();
			pressPlay.setEnabled(false);
			btnNext.setEnabled(false);
			btnPrevious.setEnabled(false);
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODOAuto-generated method stub
			ParseQuery<Album> innerQuery = ParseQuery.getQuery(Album.class);
			innerQuery.whereEqualTo("objectId",
					getArguments().getString("objectId"));

			ParseQuery<Music> query = ParseQuery.getQuery(Music.class);
			query.whereMatchesQuery("album", innerQuery);
			query.addAscendingOrder("track_number");
			try {
				mListItems = query.find();
				for (Music music : mListItems) {
					System.out.println(music.getSongTitle());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODOAuto-generated method stub
			super.onPostExecute(result);
			if (mListItems.size() > 0) {
				mName.setText(mListItems.get(SongPosition).getSongArtist()
						.getArtistName());
				mTitle.setText(mListItems.get(SongPosition).getSongTitle());
				mAlbumTitle.setText(mListItems.get(SongPosition).getSongAlbum()
						.getAlbumName()
						+ " - "
						+ mListItems.get(SongPosition).getSongAlbum()
								.getAlbumReleaseyear());
				Picasso.with(getActivity())
						.load(mListItems.get(SongPosition).getSongAlbum()
								.getAlbumArt()).into(albumArt, new Callback() {

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								new GetBlurredImageTask().execute();
							}

							@Override
							public void onError() {
								// TODO Auto-generated method stub

							}
						});
				// set Progress bar values
				progress.setProgress(0);
				progress.setMax(100);
				playSong();

			}

		}
	}

	private void playNext() {
		if (shuffle) {
			int newSong = SongPosition;
			while (newSong == SongPosition) {
				newSong = rand.nextInt(mListItems.size());
			}
			SongPosition = newSong;
		} else {
			SongPosition++;
			if (SongPosition > mListItems.size())
				SongPosition = 0;
		}

		pressPlay.setImageResource(R.drawable.ic_pause);

		playSong();

		mName.setText(mListItems.get(SongPosition).getSongArtist()
				.getArtistName());
		mTitle.setText(mListItems.get(SongPosition).getSongTitle());
		mAlbumTitle.setText(mListItems.get(SongPosition).getSongAlbum()
				.getAlbumName()
				+ " - "
				+ mListItems.get(SongPosition).getSongAlbum()
						.getAlbumReleaseyear());
		Picasso.with(getActivity())
				.load(mListItems.get(SongPosition).getSongAlbum().getAlbumArt())
				.into(albumArt, new Callback() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						new GetBlurredImageTask().execute();
					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				});
		// set Progress bar values
		progress.setProgress(0);
		progress.setMax(100);

		if (playbackPaused) {
			setController();
			playbackPaused = false;
		}
		controller.show(0);
	}

	private void playPrev() {
		SongPosition--;
		if (SongPosition > 0) {
			SongPosition = mListItems.size() - 1;
			playSong();
			pressPlay.setImageResource(R.drawable.ic_pause);
		}

		if (playbackPaused) {
			setController();
			playbackPaused = false;
		}
		controller.show(0);
	}

	public void setList(List<Music> mListItems2) {
		// TODO Auto-generated method stub
		this.mListItems = mListItems2;
	}

	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return getBufferPercentage();
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return player.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return player.getDuration();
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return player.isPlaying();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		playbackPaused = true;
		player.pause();
	}

	@Override
	public void seekTo(int arg0) {
		// TODO Auto-generated method stub
		player.seekTo(arg0);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		player.start();
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if (player.getCurrentPosition() > 0) {
			mp.reset();
			if (isRepeat) {
				player.seekTo(0);
				updateProgressBar();
			} else
				playNext();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mp.start();
		pressPlay.setEnabled(true);
		btnNext.setEnabled(true);
		btnPrevious.setEnabled(true);
		progress.setVisibility(View.VISIBLE);
		isPrepared = true;
		updateProgressBar();
	}

	private void updateProgressBar() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = player.getDuration();
			long currentDuration = player.getCurrentPosition();

			// Displaying Total Duration time
			if (player.isPlaying())
				mElapsedTime.setText(""
						+ utils.milliSecondsToTimer(currentDuration) + " - "
						+ utils.milliSecondsToTimer(totalDuration));
			else if (playbackPaused)
				mElapsedTime.setText(""
						+ utils.milliSecondsToTimer(currentDuration) + " - "
						+ utils.milliSecondsToTimer(totalDuration));
			else if (isPrepared)
				mElapsedTime.setText("0:00" + " - "
						+ utils.milliSecondsToTimer(totalDuration));
			else
				mElapsedTime.setText("");

			// Updating progress bar
			int elapsedTime = (int) (utils.getProgressPercentage(
					currentDuration, totalDuration));
			// Log.d("Progress", ""+progress);
			progress.setProgress(elapsedTime);

			// Running this thread after 100 milliseconds
			mHandler.postDelayed(this, 100);
		}
	};

	public void playSong() {
		progress.setVisibility(View.INVISIBLE);
		// play a song
		player.reset();
		// get song
		Song playSong = mListItems.get(SongPosition).getSong();
		// Updating progress bar
		try {
			player.setDataSource(playSong.getSongStreamUrl());
		} catch (Exception e) {
			Log.e("MUSIC SERVICE", "Error setting data source", e);
		}

		player.prepareAsync();

	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
	}

	public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap,
			int radius) {
		try {

			smallBitmap = RGB565toARGB888(smallBitmap);

			Bitmap bitmap = Bitmap.createBitmap(smallBitmap.getWidth(),
					smallBitmap.getHeight(), Bitmap.Config.ARGB_8888);

			RenderScript renderScript = RenderScript.create(context);
			Allocation blurInput = Allocation.createFromBitmap(renderScript,
					smallBitmap);
			Allocation blurOutput = Allocation.createFromBitmap(renderScript,
					smallBitmap);
			ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
					Element.U8_4(renderScript));
			blur.setInput(blurInput);
			blur.setRadius(radius);
			blur.forEach(blurOutput);
			blurOutput.copyTo(bitmap);
			renderScript.destroy();

			return bitmap;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private static Bitmap RGB565toARGB888(Bitmap smallBitmap) {
		// TODO Auto-generated method stub
		int numPixels = smallBitmap.getWidth() * smallBitmap.getHeight();
		int[] pixels = new int[numPixels];
		smallBitmap.getPixels(pixels, 0, smallBitmap.getWidth(), 0, 0,
				smallBitmap.getWidth(), smallBitmap.getHeight());
		Bitmap result = Bitmap.createBitmap(smallBitmap.getWidth(),
				smallBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(),
				result.getHeight());
		return result;
	}

	public class GetBlurredImageTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return blurRenderScript(getActivity(),
					((BitmapDrawable) albumArt.getDrawable()).getBitmap(), 25);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				backgroundArt.setImageBitmap(result);
			}
		}

	}

}