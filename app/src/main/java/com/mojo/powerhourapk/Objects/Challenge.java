package com.mojo.powerhourapk.Objects;

/**
 * Created by Mojsiejenko on 3/21/15.
 */
public class Challenge {

    private final String challengeText;
    private final boolean timed;
    private final long time;

    public Challenge(boolean timed, String text, long time) {
        this.timed = timed;
        challengeText = text;
        this.time = time;
    }

    public boolean isTimed() {
        return timed;
    }

    public String getChallengeText() {
        return challengeText;
    }

    public long getTime() {
        return time;
    }
}
