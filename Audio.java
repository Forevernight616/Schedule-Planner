/**
 * @author Braa Oudeh
 * @author Duc Tan Tran
 * @author Khanh Nguyen
 * @author Paloma Ortiz

 * Purpose: This program handles the audio aspect of Planner. It loads and plays .wav files,
 *      handles stop, resume and volumn control. Also scan src/adioSource for all audios.
 */

import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;

public class Audio {
    // Clip handles loading the audio data into memory for playback
    private Clip clip;
    private String fileName;
    private ArrayList<String> allTracks;
    static final String SOURCE_DIRECTORY = "src/audioSource/";
    private final double MIN_AUDIO = -80.f;
    private final double MAX_AUDIO = 0.0f;
    private FloatControl volumeControl;

    /**
     * Audio() -- constructor for this class
     * @param fileName - the track that will be played
     */
    public Audio(String fileName){
        this.fileName = fileName;
        this.allTracks = fillMusic();
        playMusic();
    }

    /**
     * playMusic() -- using the fileName to play that track. It closes all running tracks before playing this one
     */
    public void playMusic() {
        // user switches music track
        if (clip != null){
            clip.close();

            // 0.5 sec deplay
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Restore interrupted state
                Thread.currentThread().interrupt();
            }
        }

        try {
            // decode the audio file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(SOURCE_DIRECTORY + fileName));

            // get a channel to play sound (clip obj), load the entire data into mem, and starts playback
            // also set up audio control
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-5.0f); // normal audio is 0.0f, i want to make the background music quieter

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (Exception e) {
            if (!fileName.endsWith(".wav")){
                System.out.println("Please select a .wav file");
            }
            System.out.println("Error in reading file");
        }
    }

    // read the source directory, add all .wav into a arraylist that will be used

    /**
     * fillMusic() -- read the source directory, add all .wav into a arraylist that will be used
     *      in plannerview to display music options
     * @return array of files ending in .wav
     */
    public ArrayList<String> fillMusic(){
        ArrayList<String> tracks = new ArrayList<>();
        File folder = new File(SOURCE_DIRECTORY);

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".wav")) {
                    tracks.add(file.getName());
                }
            }
        }

        return tracks;
    }

    /**
     * continueMusic() -- continue the music
     */
    public void continueMusic(){
        if (clip!=null && !clip.isRunning()){
            clip.start();
        }
    }

    /**
     * stopMusic() -- stops the music but doesn't reset
     */
    public void stopMusic() {
        if (clip!= null) {
            clip.stop();
        }
    }

    /**
     * changeMusic() - change the playing song
     * @param newFileName name of the song
     */
    public void changeMusic(String newFileName){
        this.fileName = newFileName;
    }

    /**
     * getAllTracks() -- return an array of all tracks
     * @return
     */
    public ArrayList<String> getAllTracks(){
        return allTracks;
    }

    /**
     * increaseVolumn() -- increase the volumn by increments of 5
     */
    public void increaseVolumn(){
        if (volumeControl.getValue() <= MAX_AUDIO -5){
            volumeControl.setValue(volumeControl.getValue() + 5.0f);
        }
    }

    /**
     * decreaseVolumn() -- decrease the volumn by decrements of 5
     */
    public void decreaseVolumn(){
        if (volumeControl.getValue() >= MIN_AUDIO + 5){
            volumeControl.setValue(volumeControl.getValue() - 5.0f);
        }
    }

}