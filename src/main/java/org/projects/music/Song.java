package org.projects.music;

public class Song {
    private String filePath;
    private String songName;

    public Song(String filePath, String songName) {
        this.filePath = filePath;
        this.songName = songName;
    }

    public String getFilePath() {
        return filePath;
    }
}
