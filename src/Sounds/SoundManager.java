
package Sounds;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {

    private static Clip clickSound;
    private static Clip moveSound;
    private static Clip victorySound;
    private static Clip backgroundMusic;

    private static long savedMusicPosition = 0;

    private static boolean resumeAfterVictory = false;
    private static boolean musicEnabled = true;
    private static boolean victoryPlaying = false;

    static {

        clickSound = loadSound("/Sounds/Click.wav");
        moveSound = loadSound("/Sounds/Move.wav");
        victorySound = loadSound("/Sounds/Victory.wav");
        backgroundMusic = loadSound("/Sounds/Music.wav");

        if (victorySound != null) {

            victorySound.addLineListener(event -> {

                if (event.getType() == LineEvent.Type.STOP) {

                    if (victorySound.getFramePosition()
                            >= victorySound.getFrameLength()) {

                        victoryPlaying = false;

                        if (resumeAfterVictory && musicEnabled) {

                            resumeAfterVictory = false;
                            resumeMusic();

                        } else {

                            resumeAfterVictory = false;
                        }
                    }
                }
            });
        }
    }

    // ==========================================
    // LOAD SOUND
    // ==========================================

    private static Clip loadSound(String path) {

        try {

            URL url = SoundManager.class.getResource(path);

            if (url == null) {

                System.out.println("Sound not found: " + path);
                return null;
            }

            AudioInputStream audio =
                AudioSystem.getAudioInputStream(url);

            Clip clip = AudioSystem.getClip();

            clip.open(audio);
            audio.close();

            return clip;

        } catch (Exception e) {

            System.out.println("Error loading sound: " + path);
            e.printStackTrace();

            return null;
        }
    }

    // ==========================================
    // CLICK SOUND
    // ==========================================

    public static synchronized void playClick() {

        playEffect(clickSound);
    }

    // ==========================================
    // MOVEMENT SOUND
    // ==========================================

    public static synchronized void playMove() {

        playEffect(moveSound);
    }

    // ==========================================
    // PLAY SOUND EFFECT
    // ==========================================

    private static void playEffect(Clip clip) {

        if (clip == null) return;

        if (clip.isRunning()) {
            clip.stop();
        }

        clip.setFramePosition(0);
        clip.start();
    }

    // ==========================================
    // VICTORY SOUND
    // ==========================================

    public static synchronized void playVictory() {

        if (victorySound == null) return;

        resumeAfterVictory =
            musicEnabled
            && backgroundMusic != null
            && backgroundMusic.isRunning();

        if (backgroundMusic != null
                && backgroundMusic.isRunning()) {

            savedMusicPosition =
                backgroundMusic.getLongFramePosition();

            backgroundMusic.stop();
        }

        victoryPlaying = true;

        if (victorySound.isRunning()) {
            victorySound.stop();
        }

        victorySound.setFramePosition(0);
        victorySound.start();
    }

    // ==========================================
    // BACKGROUND MUSIC
    // ==========================================

    public static synchronized void playBackgroundMusic() {

        if (backgroundMusic == null) return;
        if (!musicEnabled) return;
        if (victoryPlaying) return;

        if (!backgroundMusic.isRunning()) {

            backgroundMusic.setFramePosition(0);

            backgroundMusic.loop(
                Clip.LOOP_CONTINUOUSLY
            );
        }
    }

    // ==========================================
    // MUSIC TOGGLE
    // ==========================================

    public static synchronized void toggleMusic() {

        musicEnabled = !musicEnabled;

        if (musicEnabled) {

            if (!victoryPlaying) {
                playBackgroundMusic();
            }

        } else {

            resumeAfterVictory = false;
            stopBackgroundMusic();
        }
    }

    // ==========================================
    // CHECK MUSIC STATUS
    // ==========================================

    public static boolean isMusicEnabled() {

        return musicEnabled;
    }

    // ==========================================
    // RESUME MUSIC
    // ==========================================

    private static synchronized void resumeMusic() {

        if (backgroundMusic == null) return;
        if (!musicEnabled) return;
        if (victoryPlaying) return;

        int frameLength = backgroundMusic.getFrameLength();

        if (frameLength > 0) {

            backgroundMusic.setFramePosition(
                (int) (savedMusicPosition % frameLength)
            );
        }

        backgroundMusic.loop(
            Clip.LOOP_CONTINUOUSLY
        );
    }

    // ==========================================
    // STOP MUSIC
    // ==========================================

    public static synchronized void stopBackgroundMusic() {

        resumeAfterVictory = false;

        if (backgroundMusic != null) {

            backgroundMusic.stop();
            backgroundMusic.setFramePosition(0);
        }
    }

    // ==========================================
    // STOP ALL
    // ==========================================

    public static synchronized void stopAll() {

        resumeAfterVictory = false;
        victoryPlaying = false;
        musicEnabled = false;

        stopBackgroundMusic();

        if (clickSound != null) {
            clickSound.stop();
        }

        if (moveSound != null) {
            moveSound.stop();
        }

        if (victorySound != null) {
            victorySound.stop();
        }
    }
}
