package com.mojo.powerhourapk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.mojo.powerhourapk.Objects.Genre;
import com.mojo.powerhourapk.Objects.Song;

import java.util.ArrayList;


public class MusicActivity extends Activity {

    private final String LOG_TAG = MusicActivity.class.getSimpleName();
    private ListView songListView;
    private ListView genreListView;
    private SongAdapter songAdapter;
    private Media media;
    private ArrayList<Song> songs;
    private ArrayList<Genre> genres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        media = new Media(this, getContentResolver());

        songs = Media.getSongs();
        songAdapter = Media.getSongAdapter();

        songListView = (ListView) this.findViewById(R.id.music_list);
        songListView.setAdapter(songAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void genreButton(View view) {
        Dialog genreDialog = new Dialog(MusicActivity.this);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View genre_view = li.inflate(R.layout.genre_selector, null, false);
        genreListView = (ListView) genre_view.findViewById(R.id.genre_list);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(genreDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        genreDialog.getWindow().setAttributes(params);

        final GenreAdapter genreAdapter = media.getGenreAdapter();

        genreListView.setAdapter(genreAdapter);


        genreDialog.setTitle("Select Genres to Include");
        genreDialog.setContentView(genre_view);
        genreDialog.show();
    }
}
