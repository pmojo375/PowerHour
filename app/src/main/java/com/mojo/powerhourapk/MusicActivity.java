package com.mojo.powerhourapk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mojo.powerhourapk.Objects.Genre;
import com.mojo.powerhourapk.Objects.Song;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimplePlaylist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MusicActivity extends Activity {

    private static ListView songListView;
    private static List<SimplePlaylist> playlists;
    private static ArrayAdapter<String> playlistAdapter;
    private final String LOG_TAG = MusicActivity.class.getSimpleName();
    private ListView genreListView;
    private SongAdapter songAdapter;
    private Media media;
    private ArrayList<Song> songs;
    private ArrayList<Genre> genres;
    private SharedPreferences preferences;
    private boolean spotifyOn;
    private String userUrl;
    private Api api;
    private ListView playlistTracksListView;
    private ArrayList<com.wrapper.spotify.models.Track> tracks = new ArrayList<com.wrapper.spotify.models.Track>();
    private ArrayList<String> trackStrings = new ArrayList<String>();
    private ArrayAdapter<String> playlistTrackAdapter;
    private PlaylistTracksInterface playlistTracksInterface;
    private Dialog playlistDialog;
    private View genre_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        ASyncInterface aSyncInterface = new ASyncInterface() {
            @Override
            public void processFinish(List<SimplePlaylist> output) {
                playlists = output;

                ArrayList<String> playlistNames = new ArrayList<>();

                for (SimplePlaylist playlist : playlists) {
                    playlistNames.add(playlist.getName());
                }

                playlistAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.playlist, R.id.playlist, playlistNames);


                songListView.setAdapter(playlistAdapter);
            }
        };


        playlistTracksInterface = new PlaylistTracksInterface() {
            @Override
            public void processDone(List<PlaylistTrack> output) {
                for (PlaylistTrack track : output) {
                    tracks.add(track.getTrack());
                }

                for (com.wrapper.spotify.models.Track track : tracks) {
                    trackStrings.add(track.getName());
                }

                playlistTrackAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.playlist, R.id.playlist, trackStrings);

                playlistTracksListView.setAdapter(playlistTrackAdapter);

                playlistDialog.setTitle("Playlist Tracks:");
                playlistDialog.setContentView(genre_view);
                playlistDialog.show();
            }
        };

        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref_general, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        spotifyOn = preferences.getBoolean("spotify_key", false);

        songListView = (ListView) this.findViewById(R.id.music_list);

        media = new Media(this, getContentResolver());

        if (spotifyOn) {
            userUrl = MainActivity.getUserUrl();
            api = MainActivity.getApi();

            final UserPlaylistsRequest request = api.getPlaylistsForUser(userUrl).build();

            new RetrievePlaylists(aSyncInterface).execute(request);

            songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String playlistId = playlists.get(position).getId();

                    playlistDialog = new Dialog(MusicActivity.this);
                    LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    genre_view = li.inflate(R.layout.genre_selector, null, false);
                    playlistTracksListView = (ListView) genre_view.findViewById(R.id.genre_list);

                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.copyFrom(playlistDialog.getWindow().getAttributes());
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    params.height = WindowManager.LayoutParams.MATCH_PARENT;
                    playlistDialog.getWindow().setAttributes(params);

                    final PlaylistTracksRequest request = api.getPlaylistTracks(userUrl, playlistId).build();

                    new RetrievePlaylistTracks(playlistTracksInterface).execute(request);
                }
            });
        } else {
            songs = Media.getSongs();
            songAdapter = Media.getSongAdapter();

            songListView.setAdapter(songAdapter);
        }
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

class RetrievePlaylists extends AsyncTask<UserPlaylistsRequest, Void, List<SimplePlaylist>> {

    public ASyncInterface response = null;
    private List<SimplePlaylist> playlists;

    RetrievePlaylists(ASyncInterface aSyncInterface) {
        response = aSyncInterface;
    }

    protected List<SimplePlaylist> doInBackground(UserPlaylistsRequest... request) {
        Page<SimplePlaylist> playlistsPage = null;

        try {
            playlistsPage = request[0].get();

            playlists = playlistsPage.getItems();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebApiException e) {
            e.printStackTrace();
        }

        return playlists;
    }

    protected void onPostExecute(List<SimplePlaylist> playlists) {
        response.processFinish(playlists);
    }
}

class RetrievePlaylistTracks extends AsyncTask<PlaylistTracksRequest, Void, List<PlaylistTrack>> {

    public PlaylistTracksInterface response = null;
    private List<PlaylistTrack> playlistTracks;

    RetrievePlaylistTracks(PlaylistTracksInterface aSyncInterface) {
        response = aSyncInterface;
    }

    protected List<PlaylistTrack> doInBackground(PlaylistTracksRequest... request) {
        Page<PlaylistTrack> playlistsTrackPage = null;

        try {
            playlistsTrackPage = request[0].get();

            playlistTracks = playlistsTrackPage.getItems();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebApiException e) {
            e.printStackTrace();
        }

        return playlistTracks;
    }

    protected void onPostExecute(List<PlaylistTrack> playlistTracks) {
        response.processDone(playlistTracks);
    }
}