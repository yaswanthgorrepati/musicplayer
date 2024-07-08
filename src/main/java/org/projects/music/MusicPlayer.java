package org.projects.music;

import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicPlayer {
    private String filePath;
    private List<String> filePaths;
    private Player player;
    private FileInputStream fileInputStream;
    private long totalLength;
    private long pauseLength;
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    ;
    private boolean isPaused = false;

    private static MusicPlayer musicPlayer;

    public static MusicPlayer getInstance() {
        if (musicPlayer == null) {
            return new MusicPlayer();
        }
        return musicPlayer;
    }

    private MusicPlayer() {
    }

    private String MP3PlayerButtonAction(String s) {
        if (s.equalsIgnoreCase("play")) {
            if (filePaths != null) {
                if (player != null) {
                    closePlayer(player);
                }
                isPaused = false;
                executor.execute(play());
                return "Song Started Playing!";
            }
        }
        if (s.equalsIgnoreCase("pause")) {
            if (player != null && filePath != null && !isPaused) {
                try {
                    pauseLength = fileInputStream.available();
                    isPaused = true;
                    closePlayer(player);
                    return "Paused Song!";
                } catch (Exception e) {
                    System.out.println("MP3PlayerButtonAction:Pause ::Exception" + e);
                }
            }
        }

        if (s.equalsIgnoreCase("resume")) {
            if (filePath != null && isPaused) {
                executor.execute(resume());
                isPaused = false;
                return "Resumed Song!";
            }
        }
        if (s.equalsIgnoreCase("stop")) {
            closePlayer(player);
            filePath = null;
            return "Stopped Song!";
        }
        return "Exception";
    }

    private Runnable play() {
        Runnable playRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Play::Started playing");
                    for (String filePath1 : filePaths) {
                        System.out.println("file path is" + filePath1);
                        filePath = filePath1;
                        fileInputStream = new FileInputStream(filePath);
                        player = new Player(fileInputStream);
                        totalLength = fileInputStream.available();
                        player.play();
                        if(player.isComplete()){
                            System.out.println("song is completed");
                            continue;
                        }else{
                            System.out.println("Song is not completed");
                            break;
                        }
                    }
                    System.out.println("Play::End playing");
                } catch (Exception e) {
                    System.out.println("Play::Exception " + e);
                }
            }
        };
        return playRunnable;
    }

    public void playSongs(List<String> filePaths) {
        this.filePaths = filePaths;
        MP3PlayerButtonAction("play");
    }

    public void playSong(String filePath) {
        this.filePaths = new ArrayList<String>() {{
            add(filePath);
        }};
        MP3PlayerButtonAction("play");
    }

    public void resumeSong() {
        MP3PlayerButtonAction("resume");
    }

    public void pauseSong() {
        MP3PlayerButtonAction("pause");
    }

    public void stopSong() {
        MP3PlayerButtonAction("stop");
    }

    private Runnable resume() {
        Runnable resumeRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Resume::Started playing");
                    fileInputStream = new FileInputStream(filePath);
                    player = new Player(fileInputStream);
                    fileInputStream.skip(totalLength - pauseLength);
                    player.play();
                    System.out.println("Resume::End playing");
                } catch (Exception e) {
                    System.out.println("Resume::Exception " + e);
                }
            }
        };
        return resumeRunnable;
    }

    private void closePlayer(Player player) {
        if (player != null) {
            player.close();
            System.out.println("Player closed");
        } else {
            System.out.println("No player found");
        }
    }
}
