package com.mojo.powerhourapk;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Mojsiejenko on 4/9/15.
 */
public class Media {

    // media variables
    private static final MediaPlayer mp = new MediaPlayer();
    private static final Random randomGenerator = new Random();
    public static ContentResolver musicResolver;
    public static String burp = "R.raw.burp.mp3";
    public static String can_opening = "R.raw.can_opening.mp3";
    public static Song currentSong;
    private static SongAdapter songAdapter;
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    public Song getRandomSong() {
        int index = randomGenerator.nextInt(MainActivity.songs.size());
        return MainActivity.songs.get(index);
    }

    public void playSongChangeSound(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        MediaPlayer songChange = new MediaPlayer();
        try {
            songChange.setDataSource(preferences.getString("song_sound_key", ""));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Song change sound error");
        }

        songChange.start();
    }

    public void playBeerGoneSound(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        MediaPlayer beerGone = new MediaPlayer();
        try {
            beerGone.setDataSource(preferences.getString("beer_finished_key", ""));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Song change sound error");
        }

        beerGone.start();
    }

    public void playSong() {
        // plays random song
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }

            currentSong = getRandomSong();

            if (currentSong.isSelected() && !currentSong.isPreviouslyPlayed()) { // if no more playable songs it will crash!
                Log.v(LOG_TAG, "Song okay to play");
                currentSong.setPreviouslyPlayed(true);
                mp.setDataSource(currentSong.getLocation());
                mp.prepare();
                mp.start();

                //GameService.notification.updateNotification(currentSong.getArtist() + " - " + currentSong.getTitle());
                TimedFunctions.notification.updateNotificationSong(currentSong.getTitle() + " - " + currentSong.getArtist());
                MainActivity.updateSongText(currentSong.getTitle(), currentSong.getArtist());

            } else {
                Log.v(LOG_TAG, "Song not playable... checking if there are playable songs remaining");
                int count = 0;
                for (int i = 0; i < MainActivity.songs.size(); i++) {
                    if (!MainActivity.songs.get(i).isSelected() ||
                            MainActivity.songs.get(i).isPreviouslyPlayed()) {
                        count++;
                    }
                }

                if (MainActivity.songs.size() == count) {
                    Log.v(LOG_TAG, "No playable songs... Attempting to reset previouslyPlayed tag on all songs");
                    for (int i = 0; i < MainActivity.songs.size(); i++) {
                        MainActivity.songs.get(i).setPreviouslyPlayed(false);
                    }
                    /*
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("No remaining playable songs! Songs will now play multiple times.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    */
                }
                playSong();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseSong() {
        if (mp.isPlaying()) {
            mp.pause();
        }
    }

    public void resumeSong() {
        mp.start();
    }
}
