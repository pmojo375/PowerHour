package com.mojo.powerhourapk;

import com.mojo.powerhourapk.Objects.Challenge;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Mojsiejenko on 3/19/15.
 */
public class Challenges {

    private final ArrayList<Challenge> challenges = new ArrayList<>();
    private final Random randomGenerator = new Random();

    public Challenges() {
        setUpChallenges();
    }

    public void setUpChallenges() {
        //challenges.add("Higher or Lower (Cards required)");
        challenges.add(new Challenge(true, "Drink if you refer to someone by their first name", 4));
        challenges.add(new Challenge(true, "Drink if you say a any word in the current song title or artists name", 4));
        challenges.add(new Challenge(false, "Complete silence (first to break drinks)", 5));
        challenges.add(new Challenge(false, "Guys Drink", 1));
        challenges.add(new Challenge(false, "Girls Drink", 1));
        challenges.add(new Challenge(false, "Last to point up drinks", 1));
        challenges.add(new Challenge(false, "Last to put thumb on table drinks", 1));
        challenges.add(new Challenge(false, "Rhyme", 1));
        challenges.add(new Challenge(false, "Create a category", 1));
        challenges.add(new Challenge(false, "Quarters (Last to make drinks)", 1));
        challenges.add(new Challenge(false, "“Buzz”-- go around in circle and count up, when a number that is divisible by 7 comes up say “buzz” (example: 1,2,3,4,5,6, “buzz”, 8, 9,10,11,12,13, “buzz”......)", 2));
        challenges.add(new Challenge(true, "Do not say “drink”, “drank”, or “drunk”", 4));
        challenges.add(new Challenge(true, "If you swear....drink", 4));
        challenges.add(new Challenge(true, "Must take a knee when drinking", 4));
        challenges.add(new Challenge(true, "If you laugh...drink", 4));
        challenges.add(new Challenge(false, "Staring contest (loser drinks)", 1));
        challenges.add(new Challenge(false, "Forehead master...last to touch forehead to table drinks", 2));
        challenges.add(new Challenge(true, "Have to replace “now” with “meow”...drink if they say “now”", 4));
        challenges.add(new Challenge(true, "No eye contact for duration of challenge period...if you lock eyes, both people drink", 4));
        challenges.add(new Challenge(true, "Make a rule that continues until the game ends", 2));
        challenges.add(new Challenge(true, "Can't speak in first person (No “I” or “we”) if broken...drink", 4));
        challenges.add(new Challenge(true, "Questions—must answer a question with a question", 4));
        challenges.add(new Challenge(true, "Drink if you say a number", 4));
        challenges.add(new Challenge(true, "Remove “little green guy” before drinking", 4));
        challenges.add(new Challenge(true, "Speak with an accent", 4));
        challenges.add(new Challenge(true, "Can't say words beginning with a certain letter", 4));
        challenges.add(new Challenge(true, "Must say “cheers” before drinking", 4));
        challenges.add(new Challenge(true, "Drink with left or right hand (most commonly should be done with non-dominant hand for challenge)", 4));
        challenges.add(new Challenge(true, "Pirate mode...say “argh” and pirate accent", 4));
        challenges.add(new Challenge(true, "If you point at someone...drink", 4));
        challenges.add(new Challenge(false, "Heads or Tails...if wrong...drink", 2));
        challenges.add(new Challenge(false, "Do a dance or take a drink", 2));
        challenges.add(new Challenge(true, "Repeat last word you say say. In every sentence sentence.", 4));
        challenges.add(new Challenge(false, "Guess the suit of a card being flipped", 1));
        challenges.add(new Challenge(false, "Pick a category. Each consecutive answer must begin with the last letter of the previous answer. (example: Category: Animals....Cat, Toad, Dog, Goat)", 2));
        challenges.add(new Challenge(true, "Pick a common word. Anytime it is heard in the song being played...drink", 4));
        challenges.add(new Challenge(true, "“Dirty Pint” (everyone pours some drink in a community cup) First to break the rule made drinks it.", 4));
    }

    public Challenge getRandomChallenge() {
        int index = randomGenerator.nextInt(challenges.size());
        return challenges.get(index);
    }
}
