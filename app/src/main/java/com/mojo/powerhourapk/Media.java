package com.mojo.powerhourapk;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mojo.powerhourapk.Objects.Genre;
import com.mojo.powerhourapk.Objects.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Mojsiejenko on 4/9/15.
 */
public class Media {

    private static SongAdapter songAdapter;
    private static GenreAdapter genreAdapter;
    private static ArrayList<Song> songs;
    private static ArrayList<Genre> genres = new ArrayList<Genre>();
    // media variables
    private final MediaPlayer mp = new MediaPlayer();
    private final Random randomGenerator = new Random();
    private final String LOG_TAG = Media.class.getSimpleName();
    public ContentResolver musicResolver;
    // private int[] songChangeIds = {burp};
    // private int[] beerGoneIds = {can_opening};
    public Song currentSong;
    private MusicScanner musicScanner = new MusicScanner();
    private Context mContext;

    public Media(Context context, ContentResolver contentResolver) {
        // set up the list of songs
        songs = musicScanner.getMusicFromStorage(context, contentResolver);

        mContext = context;

        // set up the list of genres
        for (int i = 0; i < songs.size(); i++) {
            if (!Genre.genres.contains(songs.get(i).getGenre())) {
                if (songs.get(i).getGenre() != null) {
                    genres.add(new Genre(songs.get(i).getGenre()));
                }
            }
        }

    }

    public static ArrayList<Song> getSongs() {
        return songs;
    }

    public static void setSongs(ArrayList<Song> songs) {
        Media.songs = songs;
    }

    public static SongAdapter getSongAdapter() {
        return songAdapter;
    }

    public void setSongAdapter(SongAdapter songAdapter) {
        Media.songAdapter = songAdapter;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public Song getRandomSong() {
        int index = randomGenerator.nextInt(songs.size());
        return songs.get(index);
    }

    public void playSongChangeSound() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int resID = R.raw.burp;
        //mContext.getResources().getIdentifier(preferences.getString("song_sound_key", "0"), "raw", mContext.getPackageName());

        //if(resID != 1) { // whatever no sound is in the array
        MediaPlayer mPlayer = MediaPlayer.create(mContext, resID);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        if (!mPlayer.equals("")) {
            mPlayer.start();
        }
        //  }
    }

    public void playBeerGoneSound() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int resID = R.raw.can_opening;
        // mContext.getResources().getIdentifier(preferences.getString("beer_finished_key", "0"), "raw", mContext.getPackageName());

        ///  if(resID != 1) { // whatever no sound is in the array
        MediaPlayer mPlayer = MediaPlayer.create(mContext, resID);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        if (!mPlayer.equals("")) {
            mPlayer.start();
        }
        //  }
    }

    public Song getCurrentSong() {
        return currentSong;
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

                TimerService.notification.updateNotificationSong(currentSong.getTitle() + " - " + currentSong.getArtist());

            } else {
                Log.v(LOG_TAG, "Song not playable... checking if there are playable songs remaining");
                int count = 0;
                for (int i = 0; i < songs.size(); i++) {
                    if (!songs.get(i).isSelected() ||
                            songs.get(i).isPreviouslyPlayed()) {
                        count++;
                    }
                }

                if (songs.size() == count) {
                    Log.v(LOG_TAG, "No playable songs... Attempting to reset previouslyPlayed tag on all songs");
                    for (int i = 0; i < songs.size(); i++) {
                        songs.get(i).setPreviouslyPlayed(false);
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("No remaining playable songs! Songs will now play multiple times.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

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

    public GenreAdapter getGenreAdapter() {
        return genreAdapter;
    }

    public void setGenreAdapter(GenreAdapter genreAdapter) {
        Media.genreAdapter = genreAdapter;
    }

    public void playChallengeSound() {
        MediaPlayer mPlayer = MediaPlayer.create(mContext, R.raw.msu_fight);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        if (!mPlayer.equals("")) {
            mPlayer.start();
        }
    }
}
