package com.mojo.powerhourapk;

import android.content.Context;

/**
 * Created by Mojsiejenko on 3/21/15.
 */
public class Settings {

    private final Context context;
    private int duration;
    private int challengeFrequency;
    private double shotSize;
    private boolean songChange;
    private boolean pauseEnabled;
    private boolean beerGone;
    private boolean ChallengesEnabled;

    Settings(Context context) {
        this.context = context;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getChallengeFrequency() {
        return challengeFrequency;
    }

    public void setChallengeFrequency(int challengeFrequency) {
        this.challengeFrequency = challengeFrequency;
    }

    public double getShotSize() {
        return shotSize;
    }

    public void setShotSize(double shotSize) {
        this.shotSize = shotSize;
    }

    public boolean isSongChange() {
        return songChange;
    }

    public void setSongChange(boolean songChange) {
        this.songChange = songChange;
    }

    public boolean isPauseEnabled() {
        return pauseEnabled;
    }

    public void setPauseEnabled(boolean pauseEnabled) {
        this.pauseEnabled = pauseEnabled;
    }

    public boolean isChallengesEnabled() {
        return ChallengesEnabled;
    }

    public void setChallengesEnabled(boolean challengesEnabled) {
        ChallengesEnabled = challengesEnabled;
    }

    public boolean isBeerGone() {
        return beerGone;
    }

    public void setBeerGone(boolean beerGone) {
        this.beerGone = beerGone;
    }
}
