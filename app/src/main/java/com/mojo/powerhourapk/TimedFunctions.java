package com.mojo.powerhourapk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mojo.powerhourapk.Objects.Challenge;

import java.util.ArrayList;

/**
 * Created by Mojsiejenko on 4/9/15.
 */
public class TimedFunctions {

    public static Notification notification;
    // time constants in milliseconds
    private final int second = 1000;
    private final int minute = 60000;
    private final int hour = 360000;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
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
    private CountDownTimer challengeTimer;
    private Context context;
    private Media media;
    private int i = 0;
    private int j = 0;
    private long elapsedTime = 0;
    private long challengeValue = 0;
    private boolean challengeActive = false;
    private int challengeSeconds2;

    public TimedFunctions(Context context) {
        this.context = context;
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        notification = new Notification(context);
        notification.createNotification();

        media = new Media();
        media.playSong();


        newMainTimer(elapsedTime);
    }

    // populates lists with important times to check for at the onTick method
    // do everytime settings change
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

    private void newMainTimer(final long oldElapsedTime) {
        mainTimer = new CountDownTimer((Integer.parseInt(preferences.getString("duration_key", "")) * minute) - oldElapsedTime, second) {

            // run every second
            public void onTick(long millisUntilFinished) {
                elapsedTime = (Integer.parseInt(preferences.getString("duration_key", "")) * minute - oldElapsedTime) - (millisUntilFinished) + oldElapsedTime + (second);
                formatMainTime(elapsedTime);
                MainActivity.updateMainTimer(gameMinutes, formattedSeconds);

                // update the notification with the current time
                notification.updateNotificationTime("Powerhour - Time: " + gameMinutes + ":" + formattedSeconds);

                // the following runs at the minute change
                if (elapsedTime >= minuteTimes.get(i)) {
                    MainActivity.updateInformation();
                    i++;

                    media.playSongChangeSound(context);

                    if (elapsedTime >= challengeTimes.get(j)) {
                        currentChallenge = challenges.getRandomChallenge();
                        // play challenge reminder sound
                        challengeActive = true;
                        j++;
                        challengeSeconds = (int) ((currentChallenge.getTime() * minute));
                        MainActivity.updateChallengeText(currentChallenge);
                        if (currentChallenge.isTimed()) {
                            MainActivity.updateChallengeTimer((int) currentChallenge.getTime(), 0);
                        }
                    }
                }

                if (challengeActive) {
                    challengeSeconds = challengeSeconds - second;
                    formatChallengeTime(challengeSeconds);
                    if (challengeSeconds <= 0) {
                        challengeActive = false;
                        MainActivity.clearChallenge();
                        // play challenge done sound
                    }
                    if (currentChallenge.isTimed()) {
                        MainActivity.updateChallengeTimer(challengeMinutes, formattedChallengeSeconds);
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
        Log.v(LOG_TAG, challengeMinutes + " " + challengeSeconds + " " + formattedChallengeSeconds);
    }

    public void stopTimers() {
        mainTimer.cancel();
        if (challengeValue > 0) {
            challengeTimer.cancel();
        }

        media.pauseSong();
    }

    public void resumeTimers() {
        newMainTimer(elapsedTime);

        mainTimer.start();
        media.resumeSong();
    }
}
