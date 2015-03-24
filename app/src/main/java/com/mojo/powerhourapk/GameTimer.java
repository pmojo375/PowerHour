package com.mojo.powerhourapk;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.floor;

/**
 * Created by Mojsiejenko on 3/21/15.
 */
public class GameTimer {

    private final Activity activity;
    private MediaPlayer beerGone = new MediaPlayer();
    // timer variables
    private int min = 0;
    private int sec = 0;
    private Timer timer;
    // challenge variables
    private int chal_min = 0;
    private int chal_sec = 60;
    private int chal_count5 = 0;
    private int chal_count = 0;
    private int chal_count10 = 0;
    private int chal_count15 = 0;
    private int chal_count20 = 0;
    private Challenge currentChallenge;
    // game variables
    public int shots = 0;
    public double ounces = 0;
    public int beers = 0;
    private TextView time_text;
    private TextView beers_text;
    private TextView ounces_text;
    private TextView shots_text;
    private MediaPlayer songChange = new MediaPlayer();
    private boolean ongoingChallenge = true;
    private boolean challengeActive = false;

    public GameTimer(Activity activity) {
        this.activity = activity;
        timer = new Timer();
        songChange = MediaPlayer.create(MainActivity.context, R.raw.burp);
        beerGone = MediaPlayer.create(MainActivity.context, R.raw.can_opening);
    }

    public void setTextViews(TextView time, TextView shots, TextView ounces, TextView beers) {
        time_text = time;
        beers_text = beers;
        ounces_text = ounces;
        shots_text = shots;
    }

    // starts the timer which runs the core app functions
    void startTimer() {
        Log.d("Timer: ", "Timer started...");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(beers, ounces, shots);
                    }
                });

                sec = sec + 1; //increase every sec

                shots = min;

                ounces = shots * MainActivity.settings.getShotSize();
                ounces = ounces - beers * 12;

                beers = (int) floor(shots / (12 / MainActivity.settings.getShotSize()));


                // update the notification object if a a shot is needed to be taken
                MainActivity.notification.mBuilder.setContentTitle("Powerhour - Time: " + Integer.toString(min) + ":"
                        + String.format("%02d", sec - (min * 60)));

                // update notification with ID mId
                MainActivity.notification.mNotificationManager.notify(MainActivity.notification.mId, MainActivity.notification.mBuilder.build());
            }
        }, 0, 1000);
    }

    private String formatTime() {
        if (sec > 59 + (min * 60)) {
            Log.d("Timer: ", "Minute change code running. Time:" + min + ":" + sec);
            min += 1;
            MainActivity.notification.createNotification();
            //  MainActivity.wakeScreen();

            if (MainActivity.settings.isSongChange()) {
                songChange.start();
            }

            if (min == 12 / MainActivity.settings.getShotSize() && MainActivity.settings.isBeerGone()) {
                beerGone.start();
            }

            MainActivity.playSong();

            // the challenge code fun at every min change
            if (MainActivity.settings.isChallengesEnabled()) {
                Log.d("Timer: ", "Challenges are enabled...");

                // set the universal challenge count to be the correct one
                switch (MainActivity.settings.getChallengeFrequency()) {
                    case 5:
                        chal_count = chal_count5;
                        break;
                    case 10:
                        chal_count = chal_count10;
                        break;
                    case 15:
                        chal_count = chal_count15;
                        break;
                    case 20:
                        chal_count = chal_count20;
                        break;
                    default:
                        chal_count = chal_count5;
                        break;
                }


                // checks if it is time for a new challenge
                if (min == chal_count * MainActivity.settings.getChallengeFrequency() + MainActivity.settings.getChallengeFrequency()) {
                    Log.d("Timer: ", "New challenged being created...");
                    MainActivity.setChallenge();
                    challengeActive = true;

                    ongoingChallenge = true; // boolean to show current challenge is being displayed
                    if (currentChallenge.isTimed()) {
                        Log.d("Timer: ", "New challenge is a timed challenge");
                        chal_min = 4;
                    } else {
                        Log.d("Timer: ", "New challenge is NOT a timed challenge");
                        chal_min = 1;
                        MainActivity.challenge_timer_text.setText("");
                    }
                }
            }
            if (challengeActive) {
                Log.d("Timer: ", "There is an active challenge");
                // checks if the timer is not empty at min change
                if (chal_min > 0) { // if the min is not yet 0
                    Log.d("Timer: ", "Challenge minute reduced but not over");
                    chal_min -= 1;
                } else { // if the challenge is over at this min change
                    Log.d("Timer: ", "Challenge minute reduced and challenge is over");
                    ongoingChallenge = false;
                    chal_min -= 1;
                }
            }
        }

        // updates the challenge counts for all the possible frequencies
        if (chal_count5 * 5 + MainActivity.settings.getChallengeFrequency() == min) {
            chal_count5++;
        }
        if (chal_count10 * 10 + MainActivity.settings.getChallengeFrequency() == min) {
            chal_count10++;
        }
        if (chal_count15 * 15 + MainActivity.settings.getChallengeFrequency() == min) {
            chal_count15++;
        }
        if (chal_count20 * 20 + MainActivity.settings.getChallengeFrequency() == min) {
            chal_count20++;
        }


        // done every sec if the challenge is timed
        if (challengeActive) {

            chal_sec -= 1;

            if(chal_sec == 0) {
                chal_sec = 60;
            }

            if(currentChallenge.isTimed()) { // if the challenge is a timed challenge

            if (chal_sec == 60 && ongoingChallenge) { // if there is a challenge out AND the timer is at 60 sec
                MainActivity.challenge_timer_text.setText(chal_min + 1 + ":" + String.format("%02d", 0) + " left in challenge");
            } else if (ongoingChallenge) { // if there is an ongoing challenge out
                MainActivity.challenge_timer_text.setText(chal_min + ":" + String.format("%02d", chal_sec) + " left in challenge");
            } else {
                challengeDone();
                challengeActive = false;
            }
        } else {
                if (chal_sec == 60 && chal_min == -1) {
                    Log.d("Timer: ", "Challenge sec is 60 and the min is -1 indicating it is over");
                    challengeDone();
                    challengeActive = false;
                }
            }
        }

        // maybe add challenge time to the Challenge object and instead of a boolean isTimed call
        // this will let me just call the challenge time instead of setting the time for timed and
        // not timed challenges and can have some go longer than others

        return "" + Integer.toString(min) + ":" + String.format("%02d", sec - (min * 60));
    }

    private void challengeDone() {
        Log.d("Timer: ", "Challenge information cleared");
        MainActivity.challenge_timer_text.setText("");
        MainActivity.challenge_text.setText("");
    }

    // update the UI
    public void updateUI(int b, double o, int s) {
        time_text.setText(formatTime());
        beers_text.setText(Integer.toString(b));
        ounces_text.setText(Double.toString(o));
        shots_text.setText(Integer.toString(s));
    }

    public void setTimer() {
        timer = new Timer();
    }

    public void cancelTimer() {
        timer.cancel();
    }

    public int getSec() {
        return sec;
    }

    public int getMin() {
        return min;
    }

    public Challenge getCurrentChallenge() {
        return currentChallenge;
    }

    public void setCurrentChallenge(Challenge currentChallenge) {
        this.currentChallenge = currentChallenge;
    }

    public boolean isChallengeActive() {
        return challengeActive;
    }
}

