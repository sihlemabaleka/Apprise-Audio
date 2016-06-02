package com.bloocheeze.android.player;

import java.util.List;
import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.bloocheeze.android.MainActivity;
import com.bloocheeze.android.R;
import com.bloocheeze.android.data.Music;
import com.bloocheeze.android.data.Song;

public class MusicStreamingService extends Service implements
		MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		MediaPlayer.OnCompletionListener {

	// media player
	private MediaPlayer player;

	// song list
	private List<Music> songs;

	// current position
	private int songPosn;

	private boolean shuffle = false;
	private Random rand;

	AudioManager audioManager;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		// create the service
		super.onCreate();
		// initialize position
		songPosn = 0;
		// create player
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.requestAudioFocus(new OnAudioFocusChangeListener() {

			@Override
			public void onAudioFocusChange(int focusChange) {
				// TODO Auto-generated method stub

			}
		}, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		player = new MediaPlayer();
		initMusicPlayer();

		rand = new Random();
	}

	public void setShuffle() {
		if (shuffle)
			shuffle = false;
		else
			shuffle = true;
	}

	public void initMusicPlayer() {
		// set player properties
		player.setWakeMode(getApplicationContext(),
				PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}

	public void setList(List<Music> mListItems) {
		songs = mListItems;
		playSong();
	}

	public class MusicBinder extends Binder {

		public MusicStreamingService getService() {
			return MusicStreamingService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new MusicBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		player.stop();
		player.release();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		// start playback
		mp.start();

		Intent notIntent = new Intent(this, MainActivity.class);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(this);

		builder.setContentIntent(pendInt)
				.setSmallIcon(R.drawable.play_button_small)
				.setTicker("Apprise Audio")
				.setOngoing(true)
				.setContentTitle(
						songs.get(songPosn).getSongArtist().getArtistName())
				.setContentText(songs.get(songPosn).getSongTitle());
		Notification not = builder.build();

		startForeground(1, not);
	}

	@Override
	public void onDestroy() {
		stopForeground(true);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		System.out.println(what);
		System.out.println(extra);
		return false;
	}

	public void playSong() {
		// play a song
		player.reset();
		// get song
		Song playSong = songs.get(songPosn).getSong();
		try {
			player.setDataSource(playSong.getSongStreamUrl());
		} catch (Exception e) {
			Log.e("MUSIC SERVICE", "Error setting data source", e);
		}

		player.prepareAsync();
	}

	public void playPrev() {
		songPosn--;
		if (songPosn > 0)
			songPosn = songs.size() - 1;
		playSong();
	}

	// skip to next
	public void playNext() {
		if (shuffle) {
			int newSong = songPosn;
			while (newSong == songPosn) {
				newSong = rand.nextInt(songs.size());
			}
			songPosn = newSong;
		} else {
			songPosn++;
			if (songPosn > songs.size())
				songPosn = 0;
		}
		playSong();
	}

	public void setSong(int songIndex) {
		songPosn = songIndex;
	}

	public int getPosn() {
		return player.getCurrentPosition();
	}

	public int getDur() {
		return player.getDuration();
	}

	public boolean isPng() {
		return player.isPlaying();
	}

	public void pausePlayer() {
		player.pause();
	}

	public void seek(int posn) {
		player.seekTo(posn);
	}

	public void go() {
		player.start();
	}

}
