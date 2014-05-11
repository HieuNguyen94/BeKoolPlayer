package com.example.bekoolplayer_1;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PlayListActivity extends ListActivity implements OnItemClickListener {
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);
		
		ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
		
		SongsManager plm = new SongsManager();
		
		// Get all songs from sdcard
		this.songsList = plm.getPlayList();
		
		
		
		// Looping through playlist
		for (int i = 0; i < songsList.size(); i++){
			// Creating new HashMap
			HashMap<String, String> song = songsList.get(i);
			
			// Adding HashList to ArrayList
			songsListData.add(song);
		}
		
		
		/*HashMap<String, String> song1 = new HashMap<String, String>();
		song1.put("songTitle", "Cho ngay mua tan");
		song1.put("songPaht", "disk1");
		songsListData.add(song1);
		HashMap<String, String> song2 = new HashMap<String, String>();
		song2.put("songTitle", "Yeu dau theo gio bay");
		song2.put("songPaht", "disk2");
		songsListData.add(song2);
		*/
		// Adding menuItems to ListView
		ListAdapter adapter = new SimpleAdapter(this, songsListData, R.layout.playlist_item, new String[] {"songTitle"}, new int[] {R.id.songTitle});
		
		setListAdapter(adapter);
		
		// Selecting single ListView item
		ListView lv = getListView();
		
		//Listening to single listitem click
		lv.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		// Getting listitem index
		int songIndex = position;
		
		// Starting new intent
		Intent in = new Intent(getApplicationContext(),BeKoolActivity.class);
		
		// Sending songIndex to PlayerActivity
		
		in.putExtra("songIndex", songIndex);
		
		setResult(RESULT_OK, in);
		
		// Closing PlayListView
		finish();
	}
	
}
