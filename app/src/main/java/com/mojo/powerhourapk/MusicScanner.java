package com.mojo.powerhourapk;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Mojsiejenko on 3/18/15.
 * <p/>
 * Contains a method to return an array list containing all songs found on the SD card
 */
public class MusicScanner {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String[] mediaProjection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION
    };
    private static final String[] genresProjection = {
            MediaStore.Audio.Genres.NAME,
            MediaStore.Audio.Genres._ID
    };
    private static Cursor genresCursor;
    private static int count;

    public static ArrayList getMusicFromStorage(Context context) {
        Log.d(LOG_TAG, "Getting music from storage...");
        ArrayList<Song> songs = new ArrayList<>();
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor mediaCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mediaProjection, selection, null, null);

        int artist_column_index = mediaCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        int title_column_index = mediaCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        int id_column_index = mediaCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
        int duration_column_index = mediaCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        int data_column_index = mediaCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

        if (mediaCursor.moveToFirst()) {
            do {
                int musicId = Integer.parseInt(mediaCursor.getString(id_column_index));
                String genre = null;

                Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId);
                genresCursor = context.getContentResolver().query(uri,
                        genresProjection, null, null, null);
                int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

                if (genresCursor.moveToFirst()) {
                    // do {
                    genre = genresCursor.getString(genre_column_index);
                    // } while (genresCursor.moveToNext());
                }

                if (Integer.parseInt(mediaCursor.getString(duration_column_index)) > 60000 && Integer.parseInt(mediaCursor.getString(duration_column_index)) < 600000) {
                    songs.add(new Song(
                            Integer.parseInt(mediaCursor.getString(id_column_index)),
                            mediaCursor.getString(title_column_index),
                            mediaCursor.getString(artist_column_index),
                            mediaCursor.getString(data_column_index),
                            Integer.parseInt(mediaCursor.getString(duration_column_index)),
                            genre));

                    count++;
                }
            } while (mediaCursor.moveToNext());

            mediaCursor.close();
            genresCursor.close();
        }

        Log.e("MusicScanner", "Success. Songs: " + count);

        return songs;
    }
}


