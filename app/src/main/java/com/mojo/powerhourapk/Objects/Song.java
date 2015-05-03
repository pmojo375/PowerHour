package com.mojo.powerhourapk.Objects;

/**
 * Created by Mojsiejenko on 3/16/15.
 */
public class Song {

    private static int count = 0;
    private final int id;
    private final String title;
    private final String artist;
    private final String location;
    private final int duration;
    private final String genre;
    private boolean selected = true;
    private boolean previouslyPlayed = false;

    public Song(int song_id, String song_title, String song_artist, String song_location, int song_duration, String song_genre) {
        id = song_id;
        title = song_title;
        artist = song_artist;
        location = song_location;
        duration = song_duration;
        if (song_genre == null) {
            genre = "Unknown";
        } else {
            genre = song_genre;
        }

    }


    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getLocation() {
        return location;
    }

    public int getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected() {
        if (this.selected) {
            count = count - 1;
        } else {
            count = count + 1;
        }

        this.selected = !selected;
    }

    public String getGenre() {
        return genre;
    }

    public boolean isPreviouslyPlayed() {
        return previouslyPlayed;
    }

    public void setPreviouslyPlayed(boolean previouslyPlayed) {
        this.previouslyPlayed = previouslyPlayed;
    }
}
