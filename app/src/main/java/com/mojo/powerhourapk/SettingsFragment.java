package com.mojo.powerhourapk;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ToggleButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SettingsFragment newInstance(int sectionNumber) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        MainActivity.one_ounce_button = (ToggleButton) rootView.findViewById(R.id.one_ounce_shot);
        MainActivity.toggle_pause_button = (ToggleButton) rootView.findViewById(R.id.pause_enabled);
        MainActivity.challenges_button = (ToggleButton) rootView.findViewById(R.id.challenges_enabled);
        MainActivity.change_sound_button = (ToggleButton) rootView.findViewById(R.id.song_change_sound_enabled);
        MainActivity.beer_sound_button = (ToggleButton) rootView.findViewById(R.id.beer_empty_sound_enabled);
        MainActivity.duration_spinner = (Spinner) rootView.findViewById(R.id.game_duration);
        MainActivity.challenge_spinner = (Spinner) rootView.findViewById(R.id.challenge_frequency);

        MainActivity.challenge_spinner.setEnabled(false);

        MainActivity.beer_sound_button.setChecked(MainActivity.settings.isBeerGone());
        MainActivity.change_sound_button.setChecked(MainActivity.settings.isSongChange());
        MainActivity.challenges_button.setChecked(MainActivity.settings.isChallengesEnabled());
        MainActivity.challenge_spinner.setEnabled(MainActivity.settings.isChallengesEnabled());
        // TODO: change the spinner position to the correct spot
        MainActivity.toggle_pause_button.setChecked(MainActivity.settings.isPauseEnabled());

        if(MainActivity.settings.getShotSize() == 1) {
            MainActivity.one_ounce_button.setChecked(true);
        } else {
            MainActivity.one_ounce_button.setChecked(false);
        }

        MainActivity.duration_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        MainActivity.settings.setDuration(30);
                        break;
                    case 1:
                        MainActivity.settings.setDuration(60);
                        break;
                    case 2:
                        MainActivity.settings.setDuration(90);
                        break;
                    case 3:
                        MainActivity.settings.setDuration(100);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                MainActivity.settings.setDuration(60);
            }
        });

        MainActivity.challenge_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        MainActivity.settings.setChallengeFrequency(5);
                        break;
                    case 1:
                        MainActivity.settings.setChallengeFrequency(10);
                        break;
                    case 2:
                        MainActivity.settings.setChallengeFrequency(15);
                        break;
                    case 3:
                        MainActivity.settings.setChallengeFrequency(20);
                        break;
                    case 4:
                        // TODO: add random function to challenge frequency code
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                MainActivity.settings.setChallengeFrequency(5);
            }
        });

        return rootView;
    }


}
