package com.mojo.powerhourapk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;

// TODO: add custom icon for app
// TODO: further optimize code to match standards

public class MainActivity extends Activity {

    // media variables
    public static Notification notification;
    public static TimedFunctions timers;
    public static Context context;
    public static ContentResolver musicResolver;
    public static SongAdapter songAdapter;
    public static String burp = "R.raw.burp.mp3";
    public static String can_opening = "R.raw.can_opening.mp3";
    public static Button pause_button;
    public static Button play_button;
    public static ArrayList<Genre> genres;
    public static GenreAdapter genreAdapter;
    public static boolean gameRunning = false;
    public static ArrayList<Song> songs;
    private static TextView timer;
    private static TextView challengeTimer;
    private static TextView challengeText;
    private static TextView shotsText;
    private static TextView beersText;
    private static TextView ouncesText;
    private static TextView songTitle;
    private static TextView songArtist;
    private static int shots = 0;
    private static int beers = 0;
    private static SharedPreferences preferences;
    private static double ounces = 0;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean isPaused;

    public static void updateSongText(String title, String artist) {
        songTitle.setText(title);
        songArtist.setText(artist);
    }

    public static void updateMainTimer(int gameMinutes, int formattedSeconds) {
        timer.setText(gameMinutes + ":" + String.format("%02d", formattedSeconds));
    }

    public static void updateChallengeTimer(int challengeMinutes, int formattedChallengeSeconds) {
        challengeTimer.setText(challengeMinutes + ":" + String.format("%02d", formattedChallengeSeconds));
    }

    public static void updateChallengeText(Challenge challenge) {
        challengeText.setText(challenge.getChallengeText());
    }

    public static void updateInformation() {
        shots++;
        double shotSize = 1.5;

        if (preferences.getBoolean("one_ounce_key", false)) {
            shotSize = 1;
        }

        if ((shots - (beers * (12 / shotSize))) * shotSize >= 12) {
            ounces = 0;
            beers++;
            // add beer sound
        } else {
            ounces = ounces + shotSize;
        }

        shotsText.setText(Integer.toString(shots));
        ouncesText.setText(Double.toString(ounces));
        beersText.setText(Integer.toString(beers));
    }

    public static void clearChallenge() {
        challengeTimer.setText("");
        challengeText.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        shotsText = (TextView) findViewById(R.id.shots);
        ouncesText = (TextView) findViewById(R.id.ounces);
        beersText = (TextView) findViewById(R.id.beers);
        challengeText = (TextView) findViewById(R.id.challenge);
        challengeTimer = (TextView) findViewById(R.id.chal_timer);
        MainActivity.pause_button = (Button) findViewById(R.id.pause_button);
        MainActivity.play_button = (Button) findViewById(R.id.play_button);
        songTitle = (TextView) findViewById(R.id.song_title);
        songArtist = (TextView) findViewById(R.id.song_artist);
        timer = (TextView) findViewById(R.id.timer);
        MainActivity.pause_button.setEnabled(false);

        // TODO: Reload all UI elements

        context = this;
        musicResolver = getContentResolver();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        notification = new Notification(this);

        // Set up the action bar
        assert getActionBar() != null;

        // gets the master list of songs from the device
        songs = MusicScanner.getMusicFromStorage(this);

        // create the custom song and genre adapters
        songAdapter = new SongAdapter(this, songs);
        genreAdapter = new GenreAdapter(this, songs);
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
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "Paused");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mp.stop();

        // stop the service

        if (notification.mNotificationManager != null) {
            notification.mNotificationManager.cancel(notification.mId);
        }

        Log.v(LOG_TAG, "Destroyed");
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void pauseButton(View view) {
        Log.d("Button: ", "pauseButton");
        if (preferences.getBoolean("pause_key", true)) {
            isPaused = false;
            timers.resumeTimers();
            pause_button.setText(R.string.pause);
        } else {
            isPaused = true;
            timers.stopTimers();
            pause_button.setText(R.string.resume);
        }
    }

    public void playButton(View view) {
        gameRunning = true;

        timers = new TimedFunctions(getApplicationContext());

        timers.populateTimeLists();
        timers.startGameTimers();

        // enable the pause button if toggled
        if (preferences.getBoolean("pause_key", true)) {
            pause_button.setEnabled(true);
        }

        play_button.setEnabled(false);
    }

    public void setChallengeText(String text) {
        challengeText.setText(text);
    }
}