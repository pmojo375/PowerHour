package com.mojo.powerhourapk;

import com.wrapper.spotify.models.SimplePlaylist;

import java.util.List;

/**
 * Created by Mojsiejenko on 5/10/15.
 */
public interface ASyncInterface {
    void processFinish(List<SimplePlaylist> output);
}
