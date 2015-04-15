package com.mojo.powerhourapk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mojo.powerhourapk.Objects.Genre;

import java.util.ArrayList;

/**
 * Created by Mojsiejenko on 3/18/15.
 */
public class GenreAdapter extends BaseAdapter {

    private final LayoutInflater genreInf;
    private ArrayList<Genre> genres = new ArrayList<>();

    public GenreAdapter(Context c, ArrayList<Song> songs) {
        genreInf = LayoutInflater.from(c);

        for (int i = 0; i < songs.size(); i++) {
            if (!Genre.genres.contains(songs.get(i).getGenre())) {
                if (songs.get(i).getGenre() != null) {
                    genres.add(new Genre(songs.get(i).getGenre()));
                }
            }
        }
    }

    @Override
    public int getCount() {
        return genres.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout genreLay = (LinearLayout) genreInf.inflate
                (R.layout.genre, parent, false);
        //get title and artist views
        TextView genreView = (TextView) genreLay.findViewById(R.id.genre);
        CheckBox checkbox = (CheckBox) genreLay.findViewById(R.id.check_box);
        //get song using position
        Genre currGenre = genres.get(position);

        //get title and artist strings
        checkbox.setChecked(currGenre.isSelected());
        genreView.setText(currGenre.getGenre());
        //set position as tag
        genreLay.setTag(position);
        return genreLay;
    }
}

