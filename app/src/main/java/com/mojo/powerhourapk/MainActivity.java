package com.mojo.powerhourapk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mojo.powerhourapk.Objects.Challenge;
import com.mojo.powerhourapk.Objects.Genre;
import com.mojo.powerhourapk.Objects.Song;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO: add custom icon for app
// TODO: further optimize code to match standards

public class MainActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "9b88ad7137d941dfa5f5be8b6e2e713a";
    private static final String REDIRECT_URI = "http://0.0.0.0";
    // Request code that will be passed together with authentication result to the onAuthenticationResult callback
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    private static Api api;
    private static String userUrl;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public GenreAdapter genreAdapter;
    public boolean gameRunning = false;
    public Button pause_button;
    public Button play_button;
    private List<Track> tracks;
    private Media media;
    private SharedPreferences preferences;
    private TimerService timerService;
    private Intent timerIntent;
    private SongAdapter songAdapter;
    private TextView timer;
    private TextView challengeTimer;
    private TextView challengeText;
    private TextView shotsText;
    private TextView beersText;
    private TextView ouncesText;
    private TextView songTitle;
    private TextView songArtist;
    private boolean isPaused;
    private ArrayList<Song> songs;
    private ArrayList<Genre> genres;
    private AuthenticationResponse response;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TimerService.UPDATE_INFORMATION.equals(intent.getAction())) {

                int[] information = intent.getIntArrayExtra("INFORMATION");
                double ounces = intent.getDoubleExtra("OUNCES", 0);

                int shots = information[0];
                int beers = information[1];

                shotsText.setText(Integer.toString(shots));
                ouncesText.setText(Double.toString(ounces));
                beersText.setText(Integer.toString(beers));
            } else if (TimerService.UPDATE_TIME.equals(intent.getAction())) {
                int[] timeArray = intent.getIntArrayExtra("TIME_ARRAY");
                timer.setText(timeArray[0] + ":" + String.format("%02d", timeArray[1]));
            } else if (TimerService.UPDATE_CHALLENGE_TIMER.equals(intent.getAction())) {
                int[] challengeTime = intent.getIntArrayExtra("CHALLENGE_TIME");
                challengeTimer.setText(challengeTime[0] + ":" + String.format("%02d", challengeTime[1]));
            } else if (TimerService.UPDATE_CHALLENGE.equals(intent.getAction())) {
                String challengeText = intent.getStringExtra("CHALLENGE_TEXT");
                setChallengeText(challengeText);
            } else if (TimerService.CLEAR_CHALLENGE.equals(intent.getAction())) {
                challengeTimer.setText("");
                challengeText.setText("");
            } else if (TimerService.UPDATE_SONG.equals(intent.getAction())) {
                String[] songInfo = intent.getStringArrayExtra("SONG_INFO");
                songArtist.setText(songInfo[0]);
                songTitle.setText(songInfo[1]);
            }

        }
    };
    private Player mPlayer;
    private boolean mBinded;
    private ServiceConnection timerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(LOG_TAG, "Service connected");
            mBinded = true;
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getService();


            if (gameRunning) {
                int[] time = timerService.getTime();
                String[] song = timerService.getSong();
                Challenge challenge = timerService.getChallenge();
                int[] challengeTime = timerService.getChallengeTime();

                timer.setText(time[0] + ":" + String.format("%02d", time[1]));
                songArtist.setText(song[0]);
                songTitle.setText(song[1]);
                if (timerService.isChallengeActive()) {
                    challengeText.setText(challenge.getChallengeText());
                    if (challenge.isTimed()) {
                        challengeTimer.setText(challengeTime[0] + ":" + String.format("%02d", challengeTime[1]));
                    }
                }

                timerService.sendInformation();

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(LOG_TAG, "Service disconnected");
            mBinded = false;
        }
    };

    public static Api getApi() {
        return api;
    }

    public static void setApi(Api _api) {
        api = _api;

        // get the user url
        new RetrieveUserURL().execute(api);
    }

    public static String getUserUrl() {
        return userUrl;
    }

    public static void setUserUrl(String _userUrl) {
        userUrl = _userUrl;
        Log.v("USER", userUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            response = AuthenticationClient.getResponse(resultCode, intent);

            if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                // build the spotify api with the token
                new RetrieveAPI().execute(response);

                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
//                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        //  mPlayer.addPlayerNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerIntent = new Intent(this, TimerService.class);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        shotsText = (TextView) findViewById(R.id.shots);
        ouncesText = (TextView) findViewById(R.id.ounces);
        beersText = (TextView) findViewById(R.id.beers);
        challengeText = (TextView) findViewById(R.id.challenge);
        challengeTimer = (TextView) findViewById(R.id.chal_timer);
        pause_button = (Button) findViewById(R.id.pause_button);
        play_button = (Button) findViewById(R.id.play_button);
        songTitle = (TextView) findViewById(R.id.song_title);
        songArtist = (TextView) findViewById(R.id.song_artist);
        timer = (TextView) findViewById(R.id.timer);

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        media = new Media(getApplicationContext(), getContentResolver());

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        pause_button.setEnabled(false);

        // Set up the action bar
        assert getActionBar() != null;

        // gets the master list of songs from the device
        songs = Media.getSongs();
        genres = media.getGenres();

        songAdapter = new SongAdapter(this, songs);
        genreAdapter = new GenreAdapter(this, genres);

        // create the custom song and genre adapters
        media.setGenreAdapter(genreAdapter);
        media.setSongAdapter(songAdapter);

        startService(timerIntent);
        bindService(timerIntent, timerServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_music:
                startActivity(new Intent(this, MusicActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRestart() {
        Log.v(LOG_TAG, "Restarted");
        super.onRestart();
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "Resumed");
        super.onResume();

        if (TimerService.isRunning()) {
            play_button.setEnabled(false);

            // enable the pause button if toggled
            if (preferences.getBoolean("pause_key", true)) {
                pause_button.setEnabled(true);
            }
        }
    }

    @Override
    public void onStop() {
        Log.v(LOG_TAG, "Stopped");
        super.onStop();
    }

    @Override
    public void onStart() {
        Log.v(LOG_TAG, "Started");
        super.onStart();

        registerReceiver(broadcastReceiver, TimerService.getIntentFilter());
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "Paused");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // free resources
        unbindService(timerServiceConnection);
        unregisterReceiver(broadcastReceiver);

        Log.v(LOG_TAG, "Destroyed");
    }

    public void pauseButton(View view) {
        Log.d("Button: ", "pauseButton");
        if (isPaused) {
            isPaused = false;
            timerService.resumeTimers();
            pause_button.setText(R.string.pause);
        } else {
            isPaused = true;
            timerService.stopTimers();
            pause_button.setText(R.string.resume);
        }
    }

    public void playButton(View view) {
        gameRunning = true;

        timerService.setUpTimers(getContentResolver());
        timerService.populateTimeLists();
        timerService.startGameTimers();

        // enable the pause button if toggled
        if (preferences.getBoolean("pause_key", true)) {
            pause_button.setEnabled(true);
        }

        play_button.setEnabled(false);
    }

    public void setChallengeText(String text) {
        challengeText.setText(text);
    }

    public void songPressed(View view) {
        songs.get(Integer.parseInt(view.getTag().toString())).setSelected();
        songAdapter.notifyDataSetChanged();
    }

    public void genrePressed(View view) {

        genres.get(Integer.parseInt(view.getTag().toString())).setSelected();

        if (genres.get(Integer.parseInt(view.getTag().toString())).isSelected()) {
            for (int j = 0; j < songs.size(); j++) {
                if (songs.get(j).getGenre() != null && (songs.get(j).getGenre()).equals(genres.get(Integer.parseInt(view.getTag().toString())).getGenre())) {
                    if (!songs.get(j).isSelected()) {
                        songs.get(j).setSelected();
                    }
                }
            }
        } else {
            for (int j = 0; j < songs.size(); j++) {
                if (songs.get(j).getGenre() != null && (songs.get(j).getGenre()).equals(genres.get(Integer.parseInt(view.getTag().toString())).getGenre())) {
                    if (songs.get(j).isSelected()) {
                        songs.get(j).setSelected();
                    }
                }
            }
        }

        genreAdapter.notifyDataSetChanged();
        songAdapter.notifyDataSetChanged();
    }
}

class RetrieveAPI extends AsyncTask<AuthenticationResponse, Void, Api> {

    protected Api doInBackground(AuthenticationResponse... response) {
        Api api = Api.builder().accessToken(response[0].getAccessToken()).build();
        return api;
    }

    protected void onPostExecute(Api api) {
        MainActivity.setApi(api);
    }
}

class RetrieveUserURL extends AsyncTask<Api, Void, String> {

    private Exception exception;
    private String userUrl;

    protected String doInBackground(Api... api) {
        try {
            userUrl = api[0].getMe().build().get().getId();
            return userUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (WebApiException e) {
            e.printStackTrace();
            return null;
        }

    }

    protected void onPostExecute(String userUrl) {
        MainActivity.setUserUrl(userUrl);
    }
}