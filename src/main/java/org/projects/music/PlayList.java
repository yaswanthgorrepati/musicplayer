package org.projects.music;

import java.util.List;

public class PlayList {

    List<Song> songList;
    String playListName;

    public PlayList(List<Song> songList, String playListName) {
        this.songList = songList;
        this.playListName = playListName;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public String getPlayListName() {
        return playListName;
    }
}
