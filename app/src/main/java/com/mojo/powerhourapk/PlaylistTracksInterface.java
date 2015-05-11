package com.mojo.powerhourapk;

import com.wrapper.spotify.models.PlaylistTrack;

import java.util.List;

/**
 * Created by Mojsiejenko on 5/10/15.
 */
public interface PlaylistTracksInterface {
    void processDone(List<PlaylistTrack> output);
}
