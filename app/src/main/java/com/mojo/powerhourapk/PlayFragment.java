package com.mojo.powerhourapk;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlayFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlayFragment newInstance(int sectionNumber) {
        PlayFragment fragment = new PlayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);

        MainActivity.gametimer.setTextViews(
                (TextView) rootView.findViewById(R.id.timer),
                (TextView) rootView.findViewById(R.id.shots),
                (TextView) rootView.findViewById(R.id.ounces),
                (TextView) rootView.findViewById(R.id.beers));

        MainActivity.challenge_text = (TextView) rootView.findViewById(R.id.challenge);
        MainActivity.challenge_timer_text = (TextView) rootView.findViewById(R.id.chal_timer);
        MainActivity.pause_button = (Button) rootView.findViewById(R.id.pause_button);
        MainActivity.play_button = (Button) rootView.findViewById(R.id.play_button);


        MainActivity.song_title_text = (TextView) rootView.findViewById(R.id.song_title);
        MainActivity.song_artist_text = (TextView) rootView.findViewById(R.id.song_artist);

        MainActivity.pause_button.setEnabled(false);

        // prevents buttons from being enabled when fragment is created mid game after swipes
        if (MainActivity.gameRunning) {
            MainActivity.play_button.setEnabled(false);
            if (MainActivity.settings.isPauseEnabled()) {
                MainActivity.pause_button.setEnabled(true);
                //MainActivity.pause_button.setText(); WANT TO SET THE TEXT TO MATCH IF PAUSED OR NOT
            } else {
                MainActivity.pause_button.setEnabled(false);
            }
        } else {
            MainActivity.play_button.setEnabled(true);
        }

        return rootView;
    }
}
