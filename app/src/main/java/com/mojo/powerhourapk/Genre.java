package com.mojo.powerhourapk;

import java.util.ArrayList;

/**
 * Created by Mojsiejenko on 3/20/15.
 */
public class Genre {

    public static final ArrayList<String> genres = new ArrayList<>();
    private String genre;
    private boolean selected = true;


    public Genre(String genre_in) {
        genre = genre_in;

        if (!genres.contains(genre_in)) {
            genres.add(genre);
        }

    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected() {
        this.selected = !selected;
    }
}
