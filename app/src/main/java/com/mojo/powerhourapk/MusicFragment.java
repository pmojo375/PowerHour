package com.mojo.powerhourapk;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class MusicFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static ArrayList<Song> songs;

    public MusicFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MusicFragment newInstance(int sectionNumber) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static GenreAdapter createGenreSpinner() {

        MainActivity.genres = new ArrayList<>();

        for (int i = 0; i < songs.size(); i++) {
            if (!Genre.genres.contains(songs.get(i).getGenre())) {
                if (songs.get(i).getGenre() != null) {
                    MainActivity.genres.add(new Genre(songs.get(i).getGenre()));
                }
            }
        }

        return new GenreAdapter(MainActivity.context, MainActivity.genres);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);

        ListView song_lv = (ListView) rootView.findViewById(R.id.music_list);

        // get the array list of songs from the MusicScanner class
        songs = MusicScanner.getMusicFromStorage(MainActivity.context);


        // set the song adapter for the UI component and send it to MainActivity
        SongAdapter songAdt = new SongAdapter(MainActivity.context, songs);
        song_lv.setAdapter(songAdt);
        MainActivity.setSongAdapter(songAdt);

        MainActivity.genreAdapter = MusicFragment.createGenreSpinner();


        return rootView;

    }

}

