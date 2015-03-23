package com.mojo.powerhourapk;

/**
 * Created by Mojsiejenko on 3/21/15.
 */
public class Challenge {

    private final String challengeText;
    private final boolean timed;

    public Challenge(boolean timed, String text) {
        this.timed = timed;
        challengeText = text;
    }

    public boolean isTimed() {
        return timed;
    }

    public String getChallengeText() {
        return challengeText;
    }
}
