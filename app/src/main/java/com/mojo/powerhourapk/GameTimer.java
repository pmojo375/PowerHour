package com.mojo.powerhourapk;

import android.app.Activity;
import android.media.MediaPlayer;
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
    private int chal_count = 0;
    private Challenge currentChallenge;
    // game variables
    private int shots = 0;
    private double ounces = 0;
    private int beers = 0;
    private TextView time_text;
    private TextView beers_text;
    private TextView ounces_text;
    private TextView shots_text;
    private MediaPlayer songChange = new MediaPlayer();
    private boolean ongoingChallenge = true;

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
        if (sec == 60 + (min * 60)) {
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
                // checks if it is time for a new challenge
                if (min - (chal_count * MainActivity.settings.getChallengeFrequency()) ==  MainActivity.settings.getChallengeFrequency()) {
                    MainActivity.setChallenge();

                    ongoingChallenge = true; // boolean to show current challenge is being displayed
                    if(currentChallenge.isTimed()) {
                        chal_min = 4;
                    } else {
                        chal_min = 1;
                        MainActivity.challenge_timer_text.setText("");
                    }

                    chal_count++;
                }

                // checks if the timer is not empty at min change
                if (chal_min > 0) { // if the min is not yet 0
                    chal_min -= 1;
                } else { // if the challenge is over at this min change
                    ongoingChallenge = false;
                    chal_min -= 1;
                }
            }
        }


        // done every sec if the challenge is timed
        if (MainActivity.settings.isChallengesEnabled()) {

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
            }
        } else {
                if(chal_sec == 60 && chal_min == -1) {
                    challengeDone();
                }
            }
        }

        // maybe add challenge time to the Challenge object and instead of a boolean isTimed call
        // this will let me just call the challenge time instead of setting the time for timed and
        // not timed challenges and can have some go longer than others

        return "" + Integer.toString(min) + ":" + String.format("%02d", sec - (min * 60));
    }

    private void challengeDone() {
        MainActivity.challenge_timer_text.setText("");
        MainActivity.challenge_text.setText("");
    }

    // update the UI
    public void updateUI(int b, double o, int s) {
        time_text.setText(formatTime());
        beers_text.setText(Integer.toString(b));
        ounces_text.setText(Double.toString(o));
        shots_text.setText(Integer.toString(s));

        MainActivity.song_artist_text.setText(MainActivity.currentSong.getArtist());
        MainActivity.song_title_text.setText(MainActivity.currentSong.getTitle());
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
}

