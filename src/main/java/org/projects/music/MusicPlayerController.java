package org.projects.music;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MusicPlayerController {

    private List<PlayList> allPlayLists;
    private String basePath;
    private MusicPlayer musicPlayer;

    public MusicPlayerController(String basePath) {
        this.basePath = basePath;
        this.allPlayLists = new ArrayList<>();
        this.musicPlayer = MusicPlayer.getInstance();
        loadDefaultPlayList();
    }

    private void loadDefaultPlayList() {
        Path startPath = Paths.get(basePath);
        List<Path> mp3Files = new ArrayList<>();

        // Use a try-with-resources statement to ensure the file visitor is closed
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    // Check if the file is an MP3
                    System.out.println(file.getFileName());
                    if (file.toString().toLowerCase().endsWith(".mp3")) {
                        mp3Files.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    // Handle any errors that occur while visiting files
                    System.err.println("Failed to access file: " + file + " (" + exc.getMessage() + ")");
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the found MP3 files and play the first one
        if (mp3Files.isEmpty()) {
            System.out.println("No MP3 files found.");
        } else {
            System.out.println("Found MP3 files:");
            for (Path mp3File : mp3Files) {
                System.out.println(mp3File);
            }
        }

        List<Song> songs = mp3Files.stream().map(filePath -> {
            return new Song(filePath.toString(),filePath.getFileName().toString());
        }).collect(Collectors.toList());
        PlayList playList = new PlayList(songs, "Default");
        this.allPlayLists.add(playList);
    }

    public void createPlayList(String playListName) {
        PlayList playList = new PlayList(new ArrayList<>(), playListName);
        this.allPlayLists.add(playList);
    }

    public void deletePlayList(PlayList playList) {
        this.allPlayLists.remove(playList);
    }

    public void updatePlayList(PlayList playList, List<Song> songs) {
        playList.getSongList().addAll(songs);
    }

    public void play(PlayList playList) {
        List<String> filePaths = playList.getSongList().stream().map(song -> {
            return song.getFilePath();
        }).collect(Collectors.toList());
        musicPlayer.playSongs(filePaths);
    }

    public void play(PlayList playList, int songNumber) {
        String filePath = playList.getSongList().get(songNumber).getFilePath();
        musicPlayer.playSong(filePath);
    }

    public void resume() {
        musicPlayer.resumeSong();
    }

    public void pause() {
        musicPlayer.pauseSong();
    }

    public void stop() {
        musicPlayer.stopSong();
    }

    public List<PlayList> getAllPlayLists() {
        return allPlayLists;
    }

    public static void main(String[] args) throws InterruptedException {
        MusicPlayerController musicPlayerController = new MusicPlayerController("/home/yaswanth/Music/Piano");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            Thread.currentThread().sleep(2000);
            System.out.print("\n|1:Play  |");
            System.out.print("2:Pause|");
            System.out.print("\n|3:Resume|");
            System.out.print("4:Close|");
            System.out.print("\nEnter your choice: ");
            int choice = scanner.nextInt();
            if (choice == 1) {
                musicPlayerController.play(musicPlayerController.getAllPlayLists().get(0));
            } else if (choice == 2) {
                musicPlayerController.pause();
            } else if (choice == 3) {
                musicPlayerController.resume();
            } else if(choice == 4){
                musicPlayerController.stop();
            }
        }
    }
}
