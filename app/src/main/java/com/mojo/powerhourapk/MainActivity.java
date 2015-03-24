package com.mojo.powerhourapk;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

// TODO: add custom icon for app
// TODO: further optimize code to match standards

public class MainActivity extends Activity implements ActionBar.TabListener {

    // media variables
    private static final MediaPlayer mp = new MediaPlayer();
    private static final Challenges challenges = new Challenges();
    private static final String POWERHOUR = "powerhour";
    public static GameTimer gametimer;
    public static Settings settings;
    public static Notification notification;
    public static Context context;
    public static ContentResolver musicResolver;
    private static SongAdapter songAdapter;
    public static String burp = "R.raw.burp.mp3";
    public static String can_opening = "R.raw.can_opening.mp3";
    public static Song currentSong;

    // buttons and spinners
    public static Button pause_button;
    public static Button play_button;
    public static ToggleButton one_ounce_button;
    public static ToggleButton toggle_pause_button;
    public static ToggleButton challenges_button;
    public static Spinner duration_spinner;
    public static Spinner challenge_spinner;
    public static ToggleButton change_sound_button;
    public static ToggleButton beer_sound_button;
    public static TextView song_title_text;
    public static TextView song_artist_text;
    public static ArrayList<Genre> genres;
    public static GenreAdapter genreAdapter;
    public static TextView challenge_text;
    public static TextView challenge_timer_text;
    private static ViewPager mViewPager;
    private static boolean paused = false;

    public static PowerManager pm;
    public static PowerManager.WakeLock wl;
    public static boolean gameRunning = false;
    private static final Random randomGenerator = new Random();

    public static void setChallenge() {
        gametimer.setCurrentChallenge(challenges.getRandomChallenge());
        challenge_text.setText(gametimer.getCurrentChallenge().getChallengeText());
    }

    // gets a random song
    public static Song getRandomSong() {
        int index = randomGenerator.nextInt(MusicFragment.songs.size());
        return MusicFragment.songs.get(index);
    }

    public static void playSong() {
        // plays random song
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }

            currentSong = getRandomSong();

            if (currentSong.isSelected() && !currentSong.isPreviouslyPlayed()) { // if no more playable songs it will crash!
                currentSong.setPreviouslyPlayed(true);
                mp.setDataSource(currentSong.getLocation());
                mp.prepare();
                mp.start();

                notification.mBuilder.setContentText(currentSong.getArtist() + " - " + currentSong.getTitle());


            } else {
                playSong();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: fix bug where song list is rearranged and rechecked
    public static void setSongAdapter(SongAdapter adapter) {
        songAdapter = adapter;
    }

    // needs work
    public static void wakeScreen() {
        pm = (PowerManager) MainActivity.context.getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "FlashActivity");
        wl.acquire();
    }

    /*
     *  Creates the genre dialog
     */
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

        list.setAdapter(genreAdapter);

        genreDialog.setTitle("Select Genres to Include");
        genreDialog.setContentView(genre_view);
        genreDialog.show();
    }

    /*
     *  Settings onClick methods
     */
    public void toggleOneOunce(View view) {
        if (one_ounce_button.isChecked()) {
            settings.setShotSize(1);
        } else {
            settings.setShotSize(1.5);
        }
    }

    public void toggleChallenges(View view) {
        settings.setChallengesEnabled(challenges_button.isChecked());
        challenge_spinner.setEnabled(challenges_button.isChecked());
    }

    public void togglePause(View view) {
        settings.setPauseEnabled(toggle_pause_button.isChecked());
    }

    public void changeSoundToggle(View view) {
        settings.setSongChange(change_sound_button.isChecked());
    }

    public void beerSoundToggle(View view) {
        settings.setBeerGone(beer_sound_button.isChecked());
    }

    /*
     *  Lifecycle Methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String msg = "Android: ";
        Log.d(msg, "created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        musicResolver = getContentResolver();

        gametimer = new GameTimer(this);
        settings = new Settings(this);
        notification = new Notification(this);


        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        assert actionBar != null;
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        actionBar.hide();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /*
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        */

    }

    @Override
    public void onRestart() {
        String msg = "Android: ";
        Log.e(msg, "Restarted");
        super.onRestart();
    }

    @Override
    public void onResume() {
        String msg = "Android: ";
        Log.e(msg, "Resumed");
        super.onResume();
    }

    @Override
    public void onStop() {
        String msg = "Android: ";
        Log.e(msg, "Stopped");

        SharedPreferences.Editor editor = getSharedPreferences(POWERHOUR, MODE_PRIVATE).edit();
        editor.putBoolean("challengesEnabled", settings.isChallengesEnabled());
        editor.putBoolean("pauseEnabled", settings.isPauseEnabled());
        editor.putBoolean("songSoundEnabled", settings.isSongChange());
        editor.putBoolean("beerGoneEnabled", settings.isBeerGone());
        editor.putFloat("shotSize", (float) settings.getShotSize());
        editor.putInt("duration", settings.getDuration());
        editor.putInt("challengeFrequency", settings.getChallengeFrequency());
        editor.commit();
        super.onStop();
    }

    @Override
    public void onStart() {
        String msg = "Android: ";
        Log.e(msg, "Started");
        super.onStart();

        SharedPreferences prefs = getSharedPreferences(POWERHOUR, MODE_PRIVATE);
        settings.setBeerGone(prefs.getBoolean("beerGoneEnabled", false));
        settings.setSongChange(prefs.getBoolean("songSoundEnabled", true));
        settings.setChallengeFrequency(prefs.getInt("challengeFrequency", 5));
        settings.setDuration(prefs.getInt("duration", 60));
        settings.setChallengesEnabled(prefs.getBoolean("challengesEnabled", false));
        settings.setPauseEnabled(prefs.getBoolean("pauseEnabled", false));
        settings.setShotSize((double) prefs.getFloat("shotSize", (float) 1.5));
    }

    @Override
    public void onPause() {
        String msg = "Android: ";
        Log.e(msg, "Paused");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
       // wl.release();
        gametimer.cancelTimer();
        if(notification != null) {
            notification.mNotificationManager.cancel(notification.mId);
        }
        String msg = "Android: ";
        Log.d(msg, "destroyed");
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

    /*
     *  Button Methods
     */
    public void pauseButton(View view) {
        if (paused) {
            paused = false;
            gametimer.setTimer();
            gametimer.startTimer();
            mp.start();
            pause_button.setActivated(false);
            pause_button.setText(R.string.pause);
        } else {
            paused = true;
            gametimer.cancelTimer();
            pause_button.setActivated(false);
            pause_button.setText(R.string.resume);
            mp.pause();
        }
    }

    public void playButton(View view) {

        gameRunning = true;

        //if(MusicFragment.song_lv.getCount() > 60) {
        notification.createNotification();
        gametimer.startTimer();
        //}

        // enable the pause button if toggled
        if (settings.isPauseEnabled()) {
            pause_button.setEnabled(true);
        }


        Challenge nullChallenge = new Challenge(false, "");
        gametimer.setCurrentChallenge(nullChallenge);

        // TODO: code duration function (game end)


        playSong();

        play_button.setEnabled(false);

    }


    public void songPressed(View view) {
        (MusicFragment.songs.get(Integer.parseInt(view.getTag().toString()))).setSelected();
        songAdapter.notifyDataSetChanged();
    }

    public void genrePressed(View view) {
        genres.get(Integer.parseInt(view.getTag().toString())).setSelected();

        //   for(int i = 0; i < genres.size(); i++) {
        if (genres.get(Integer.parseInt(view.getTag().toString())).isSelected()) {

            for (int j = 0; j < MusicFragment.songs.size(); j++) {
                if (MusicFragment.songs.get(j).getGenre() != null && (MusicFragment.songs.get(j).getGenre()).equals(genres.get(Integer.parseInt(view.getTag().toString())).getGenre())) {
                    if (!MusicFragment.songs.get(j).isSelected()) {
                        MusicFragment.songs.get(j).setSelected();
                    }
                }
            }
            // }


        } else {
            for (int j = 0; j < MusicFragment.songs.size(); j++) {
                if (MusicFragment.songs.get(j).getGenre() != null && (MusicFragment.songs.get(j).getGenre()).equals(genres.get(Integer.parseInt(view.getTag().toString())).getGenre())) {
                    if (MusicFragment.songs.get(j).isSelected()) {
                        MusicFragment.songs.get(j).setSelected();
                    }
                }
            }
        }
        genreAdapter.notifyDataSetChanged();
        songAdapter.notifyDataSetChanged();
    }

    /*
     *  Tab Methods
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = Fragment.instantiate(context, PlayFragment.class.getName());
                    break;
                case 1:
                    fragment = Fragment.instantiate(context, MusicFragment.class.getName());
                    break;
                case 2:
                    fragment = Fragment.instantiate(context, SettingsFragment.class.getName());
                    break;
            }

            return fragment;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section1).toUpperCase(l);

            }
            return null;
        }
    }
}
