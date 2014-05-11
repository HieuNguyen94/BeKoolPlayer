package com.example.bekoolplayer_1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class BeKoolActivity extends Activity implements OnCompletionListener,
		OnSeekBarChangeListener, OnClickListener {

	// All variables here
	private ImageButton bPlay;
	private ImageButton bNext;
	private ImageButton bPrevious;
	private ImageButton bPlaylist;
	private ImageButton bRepeat;
	private ImageButton bShuffle;
	private SeekBar songProgressBar;
	private TextView tvTitleLabel;
	private TextView tvCurrentDuration;
	private TextView tvTotalDuration;
	private TextView tvArtistName;
	private TextView tvAlbumName;
	private ImageView ivAlbumCover;
	// Media Player
	private MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();
	private SongsManager songManager;
	private Utilities utils;
	private int currentSongIndex = 0;
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialize();
	}

	private void initialize() {
		// TODO Auto-generated method stub
		// All player buttons
		bPlay = (ImageButton) findViewById(R.id.bPlay);
		bNext = (ImageButton) findViewById(R.id.bNext);
		bPrevious = (ImageButton) findViewById(R.id.bPrevious);
		bPlaylist = (ImageButton) findViewById(R.id.bPlaylist);
		bRepeat = (ImageButton) findViewById(R.id.bRepeat);
		bShuffle = (ImageButton) findViewById(R.id.bShuffle);
		songProgressBar = (SeekBar) findViewById(R.id.sbProgressBar);
		tvTitleLabel = (TextView) findViewById(R.id.tvSongTitle);
		tvCurrentDuration = (TextView) findViewById(R.id.tvCurrentTime);
		tvTotalDuration = (TextView) findViewById(R.id.tvTotalTime);
		tvArtistName = (TextView) findViewById(R.id.tvArtistName);
		tvAlbumName = (TextView) findViewById(R.id.tvAlbumName);
		ivAlbumCover = (ImageView) findViewById(R.id.ivAlbumCover);
		bPlay.setOnClickListener(this);
		bNext.setOnClickListener(this);
		bPrevious.setOnClickListener(this);
		bPlaylist.setOnClickListener(this);
		bRepeat.setOnClickListener(this);
		bShuffle.setOnClickListener(this);

		// Mediaplayer
		mp = new MediaPlayer();
		songManager = new SongsManager();
		utils = new Utilities();

		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this);
		mp.setOnCompletionListener(this);

		// Getting all songs list
		songsList = songManager.getPlayList();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.aboutUs:
			Intent aboutus = new Intent("android.intent.action.ABOUT");
			startActivity(aboutus);
			break;
		case R.id.preferences:

			break;
		case R.id.exit:
			finish();
			break;
		}
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		// Check for repeat is ON or OFF
		if (isRepeat) {
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if (isShuffle) {
			// Shuffle is on - play random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt(songsList.size());
			playSong(currentSongIndex);
		} else {
			// No repeat or shuffle On - play next song
			if (currentSongIndex < (songsList.size() - 1)) {
				playSong(currentSongIndex + 1);
				currentSongIndex++;
			} else {
				// Play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// Remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(),
				totalDuration);

		// Forward or backward to certain seconds
		mp.seekTo(currentPosition);

		// Update timer progress again
		updateProgressBar();

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		// Play Button
		case R.id.bPlaylist:
			Intent i = new Intent(getApplicationContext(),
					PlayListActivity.class);
			startActivityForResult(i, currentSongIndex);
			break;
		case R.id.bNext:
			if (isRepeat) {
				// repeat is on play same song again
				playSong(currentSongIndex);
			} else if (isShuffle) {
				// Shuffle is on - play random song
				Random rand = new Random();
				currentSongIndex = rand.nextInt(songsList.size());
				playSong(currentSongIndex);
			} else {
				// No repeat or shuffle On - play next song
				if (currentSongIndex < (songsList.size() - 1)) {
					playSong(currentSongIndex + 1);
					currentSongIndex++;
				} else {
					// Play first song
					playSong(0);
					currentSongIndex = 0;
				}
			}
			break;
		case R.id.bPrevious:
			if (currentSongIndex > 0) {
				playSong(currentSongIndex - 1);
				currentSongIndex--;
			} else {
				// Play last song
				playSong(songsList.size() - 1);
				currentSongIndex = songsList.size() - 1;
			}
			break;
		case R.id.bRepeat:
			if (isRepeat) {
				isRepeat = false;
				Toast.makeText(getApplicationContext(), "Repeat is OFF",
						Toast.LENGTH_SHORT).show();
				bRepeat.setImageResource(R.drawable.b_repeat);
			} else {
				// make repeat to true
				isRepeat = true;
				Toast.makeText(getApplicationContext(), "Repeat is ON",
						Toast.LENGTH_SHORT).show();
				// Make shuffle to false
				isShuffle = false;
				bRepeat.setImageResource(R.drawable.b_repeat_focus);
				bShuffle.setImageResource(R.drawable.b_shuffle);
			}
			break;
		case R.id.bShuffle:
			if (isShuffle) {
				isShuffle = false;
				Toast.makeText(getApplicationContext(), "Shuffle is OFF",
						Toast.LENGTH_SHORT).show();
				bShuffle.setImageResource(R.drawable.b_shuffle);
			} else {
				// Make shuffle to true
				isShuffle = true;
				Toast.makeText(getApplicationContext(), "Shuffle is ON",
						Toast.LENGTH_SHORT).show();
				// Make repeat to false
				isRepeat = false;
				bShuffle.setImageResource(R.drawable.b_shuffle_focus);
				bRepeat.setImageResource(R.drawable.b_repeat);
			}
			break;
		case R.id.bPlay:
			if (mp.isPlaying()) {
				mp.pause();
				bPlay.setImageResource(R.drawable.b_play);
			} else {

				mp.start();
				bPlay.setImageResource(R.drawable.b_pause);
			}
			break;

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			currentSongIndex = data.getExtras().getInt("songIndex");

			// Play selected song
			playSong(currentSongIndex);
		}

	}

	/**
	 * Function to play a song
	 * 
	 * @param songIndex
	 *            - index of song
	 * @param currentSongIndex2
	 */
	private void playSong(int songIndex) {
		// TODO Auto-generated method stub
		// Play song
		try {
			mp.reset();
			try {
				mp.setDataSource(songsList.get(songIndex).get("songPath"));
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mp.prepare();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mp.start();
			// Displaying Song title
			String songTitle = songsList.get(songIndex).get("songTitle");
			tvTitleLabel.setText(songTitle);

			// Changing Button Image to pause image
			bPlay.setImageResource(R.drawable.b_pause);

			// Set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);

			// Updating progress bar
			updateProgressBar();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		// ImageView album_art;
		MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
		// Get album cover
		byte[] art;
		metaRetriver.setDataSource(songsList.get(songIndex).get("songPath"));
		try {
			art = metaRetriver.getEmbeddedPicture();
			Bitmap songImage = BitmapFactory
					.decodeByteArray(art, 0, art.length);
			ivAlbumCover.setBackground(null);
			ivAlbumCover.setImageBitmap(songImage);

		} catch (Exception e) {
			ivAlbumCover.setImageBitmap(null);
			ivAlbumCover.setBackgroundResource(R.drawable.bkalbum);
		}

		// Get Artist Name
		try {
			tvAlbumName
					.setText("Album: "
							+ metaRetriver
									.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
		} catch (Exception e) {
			tvAlbumName.setText("Album: " + "Unknown Album");
		}

		// Get Album Name
		try {
			tvArtistName
					.setText("Artist: "
							+ metaRetriver
									.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
		} catch (Exception e) {
			tvArtistName.setText("Artist: " + "Unknown Artist");
		}

	}

	private void updateProgressBar() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background runnable thread
	 */

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = mp.getDuration();
			long currentDuration = mp.getCurrentPosition();

			// Displaying Total Duration time
			tvTotalDuration.setText(""
					+ utils.milliSecondsToTimer(totalDuration));
			// Displaying Time completed playing
			tvCurrentDuration.setText(""
					+ utils.milliSecondsToTimer(currentDuration));
			// Updating progress bar
			int progress = (int) (utils.getProgressPercentage(currentDuration,
					totalDuration));
			songProgressBar.setProgress(progress);

			// Running this thread after 100 milliseconds
			mHandler.postDelayed(this, 100);
		}
	};

}
