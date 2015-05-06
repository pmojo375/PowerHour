package com.mojo.powerhourapk;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mojo.powerhourapk.Objects.Challenge;
import com.mojo.powerhourapk.Objects.Song;

import java.util.ArrayList;

/**
 * Created by Mojsiejenko on 4/9/15.
 */
public class TimerService extends Service {

    public final static String UPDATE_INFORMATION =
            "com.mojo.UPDATE_INFORMATION";
    public final static String UPDATE_TIME =
            "com.mojo.UPDATE_TIME";
    public final static String UPDATE_CHALLENGE =
            "com.mojo.UPDATE_CHALLENGE";
    public final static String UPDATE_CHALLENGE_TIMER =
            "com.mojo.UPDATE_CHALLENGE_TIMER";
    public final static String CLEAR_CHALLENGE =
            "com.mojo.CLEAR_CHALLENGE";
    public final static String UPDATE_SONG =
            "com.mojo.UPDATE_SONG";
    public static Notification notification;
    private static boolean running;
    // time constants in milliseconds
    private final int second = 1000;
    private final int minute = 60000;
    private final int hour = 360000;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    private int gameSeconds;
    private int gameMinutes;
    private int formattedSeconds;
    private SharedPreferences preferences;
    private CountDownTimer mainTimer;
    private int challengeSeconds;
    private int challengeMinutes;
    private int formattedChallengeSeconds;
    private Challenges challenges = new Challenges();
    private Challenge currentChallenge;
    private ArrayList<Long> challengeTimes = new ArrayList<Long>();
    private ArrayList<Long> minuteTimes = new ArrayList<Long>();
    private Media media;
    private int i = 0;
    private int j = 0;
    private long elapsedTime = 0;
    private boolean challengeActive = false;
    private int challengeSeconds2;
    private Intent intent;
    private int shots;
    private int beers;
    private double ounces;

    public static boolean isRunning() {
        return running;
    }

    // builds and returns an intent filter for receiving broadcasts
    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_CHALLENGE);
        filter.addAction(UPDATE_CHALLENGE_TIMER);
        filter.addAction(UPDATE_INFORMATION);
        filter.addAction(UPDATE_TIME);
        filter.addAction(CLEAR_CHALLENGE);
        filter.addAction(UPDATE_SONG);
        return filter;
    }

    public int[] getTime() {
        int[] timeInfo = {gameMinutes, formattedSeconds};
        return timeInfo;
    }

    public String[] getSong() {
        Song song = media.getCurrentSong();

        String[] songInfo = {song.getArtist(), song.getTitle()};
        Log.v(LOG_TAG, song.getArtist());
        return songInfo;
    }

    public Challenge getChallenge() {
        return currentChallenge;
    }

    public int[] getChallengeTime() {
        int[] challengeTimeInfo = {challengeMinutes, formattedChallengeSeconds};
        return challengeTimeInfo;
    }

    public boolean isChallengeActive() {
        return challengeActive;
    }

    // initializes the service and starts the timers
    public void setUpTimers(ContentResolver contentResolver) {

        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref_general, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        notification = new Notification(getApplicationContext());
        notification.createNotification();

        media = new Media(getApplicationContext(), contentResolver);
        media.playSong();

        Intent intent = new Intent(UPDATE_SONG);
        String[] songInfo = {media.getCurrentSong().getArtist(), media.getCurrentSong().getTitle()};
        intent.putExtra("SONG_INFO", songInfo);
        sendBroadcast(intent);

        running = true;

        newMainTimer(elapsedTime);
    }

    // populates lists with important times to check for at the onTick method
    // do every time settings change
    public void populateTimeLists() {
        int duration = Integer.parseInt(preferences.getString("duration_key", "60"));
        int frequency = Integer.parseInt(preferences.getString("challenge_frequency_key", "5"));

        for (int i = 0; i * frequency < duration; i++) {
            challengeTimes.add((long) i * frequency * minute);
        }

        for (int i = 1; i <= duration; i++) {
            minuteTimes.add((long) i * minute);
        }
    }

    public void sendInformation() {
        int[] information = {shots, beers};

        intent = new Intent(UPDATE_INFORMATION);
        intent.putExtra("INFORMATION", information);
        intent.putExtra("OUNCES", ounces);
        sendBroadcast(intent);
    }

    private void newMainTimer(final long oldElapsedTime) {
        mainTimer = new CountDownTimer((Integer.parseInt(preferences.getString("duration_key", "")) * minute) - oldElapsedTime, second) {

            // run every second
            public void onTick(long millisUntilFinished) {
                elapsedTime = (Integer.parseInt(preferences.getString("duration_key", "")) * minute - oldElapsedTime) - (millisUntilFinished) + oldElapsedTime + (second);
                formatMainTime(elapsedTime);

                // send timer broadcast
                int[] timeArray = {gameMinutes, formattedSeconds};
                intent = new Intent(UPDATE_TIME);
                intent.putExtra("TIME_ARRAY", timeArray);
                sendBroadcast(intent);

                // update the notification with the current time
                notification.updateNotificationTime("Powerhour - Time: " + gameMinutes + ":" + String.format("%02d", formattedSeconds));

                // the following runs at the minute change
                if (elapsedTime >= minuteTimes.get(i)) {
                    // send information update broadcast
                    shots++;
                    double shotSize = 1.5;

                    // accounts for the 1 ounce shot setting
                    if (preferences.getBoolean("one_ounce_key", false)) {
                        shotSize = 1;
                    }

                    if ((shots - (beers * (12 / shotSize))) * shotSize >= 12) {
                        beers++;
                        media.playBeerGoneSound();
                        ounces = 0;
                    } else {
                        ounces = ounces + shotSize; // do not do this when app starts up
                    }

                    int[] information = {shots, beers};

                    intent = new Intent(UPDATE_INFORMATION);
                    intent.putExtra("INFORMATION", information);
                    intent.putExtra("OUNCES", ounces);
                    sendBroadcast(intent);

                    // increment index for times
                    i++;

                    media.playSongChangeSound();
                    media.playSong();

                    Intent intent = new Intent(UPDATE_SONG);
                    String[] songInfo = {media.getCurrentSong().getArtist(), media.getCurrentSong().getTitle()};
                    intent.putExtra("SONG_INFO", songInfo);
                    sendBroadcast(intent);

                    if (elapsedTime > challengeTimes.get(j)) {
                        currentChallenge = challenges.getRandomChallenge();

                        media.playChallengeSound();

                        challengeActive = true;
                        j++;
                        challengeSeconds = (int) ((currentChallenge.getTime() * minute) + second);

                        // update challenge text broadcast
                        intent = new Intent(UPDATE_CHALLENGE);
                        intent.putExtra("CHALLENGE_TEXT", currentChallenge.getChallengeText());
                        sendBroadcast(intent);

                        if (currentChallenge.isTimed()) {

                            int[] challengeTime = {(int) currentChallenge.getTime(), 0};
                            // challenge timer broadcast
                            intent = new Intent(UPDATE_CHALLENGE_TIMER);
                            intent.putExtra("CHALLENGE_TIME", challengeTime);
                            sendBroadcast(intent);
                        }
                    }
                }

                if (challengeActive) {
                    challengeSeconds = challengeSeconds - second;
                    formatChallengeTime(challengeSeconds);
                    if (challengeSeconds <= 0) {
                        challengeActive = false;

                        // clear challenge broadcast
                        Intent intent = new Intent(CLEAR_CHALLENGE);
                        sendBroadcast(intent);

                        // play challenge done sound
                    }
                    if (currentChallenge.isTimed()) {

                        // update challenge time
                        intent = new Intent(UPDATE_CHALLENGE_TIMER);
                        int[] challengeTime = {challengeMinutes, formattedChallengeSeconds};
                        intent.putExtra("CHALLENGE_TIME", challengeTime);
                        sendBroadcast(intent);
                    }
                }
            }

            public void onFinish() {
                // game finished code
            }
        };
    }

    public void startGameTimers() {
        mainTimer.start();
    }

    private void formatMainTime(long milliseconds) {
        gameSeconds = (int) milliseconds / second;
        gameMinutes = (int) milliseconds / minute;

        formattedSeconds = gameSeconds - (gameMinutes * 60);
    }

    private void formatChallengeTime(long milliseconds) {
        challengeSeconds2 = (int) milliseconds / second;
        challengeMinutes = (int) milliseconds / minute;

        formattedChallengeSeconds = challengeSeconds2 - challengeMinutes * 60;
    }

    public void stopTimers() {
        mainTimer.cancel();

        media.pauseSong();
    }

    public void resumeTimers() {
        newMainTimer(elapsedTime);

        mainTimer.start();
        media.resumeSong();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }
}
