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
import android.widget.AdapterView;
import android.widget.ListView;


public class MusicActivity extends Activity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private ListView songListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        songListView = (ListView) this.findViewById(R.id.music_list);
        songListView.setAdapter(MainActivity.songAdapter);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                songPressed(view);
            }
        });
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
        Dialog genreDialog = new Dialog(MainActivity.context);
        LayoutInflater li = (LayoutInflater) MainActivity.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View genre_view = li.inflate(R.layout.genreselctor, null, false);
        final ListView list = (ListView) genre_view.findViewById(R.id.genre_list);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(genreDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        genreDialog.getWindow().setAttributes(params);

        list.setAdapter(MainActivity.genreAdapter);

        genreDialog.setTitle("Select Genres to Include");
        genreDialog.setContentView(genre_view);
        genreDialog.show();
    }

    public void genrePressed(View view) {
        MainActivity.genres.get(Integer.parseInt(view.getTag().toString())).setSelected();

        //   for(int i = 0; i < genres.size(); i++) {
        if (MainActivity.genres.get(Integer.parseInt(view.getTag().toString())).isSelected()) {

            for (int j = 0; j < MainActivity.songs.size(); j++) {
                if (MainActivity.songs.get(j).getGenre() != null && (MainActivity.songs.get(j).getGenre()).equals(MainActivity.genres.get(Integer.parseInt(view.getTag().toString())).getGenre())) {
                    if (!MainActivity.songs.get(j).isSelected()) {
                        MainActivity.songs.get(j).setSelected();
                    }
                }
            }
            // }


        } else {
            for (int j = 0; j < MainActivity.songs.size(); j++) {
                if (MainActivity.songs.get(j).getGenre() != null && (MainActivity.songs.get(j).getGenre()).equals(MainActivity.genres.get(Integer.parseInt(view.getTag().toString())).getGenre())) {
                    if (MainActivity.songs.get(j).isSelected()) {
                        MainActivity.songs.get(j).setSelected();
                    }
                }
            }
        }
        MainActivity.genreAdapter.notifyDataSetChanged();
        MainActivity.songAdapter.notifyDataSetChanged();
    }

    public void songPressed(View view) {
        (MainActivity.songs.get(Integer.parseInt(view.getTag().toString()))).setSelected();
        MainActivity.songAdapter.notifyDataSetChanged();
    }
}
