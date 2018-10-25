package util;

import engine.Globals;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class Sound {

    public static void play(String tag) {
        new Thread(new SoundPlayer(tag)).start();
    }
}

class SoundPlayer implements Runnable {

    private String tag;

    public SoundPlayer(String t) {
        tag = t;
    }

    @Override
    public void run() {
        try {
            final Clip clip = AudioSystem.getClip();

            clip.addLineListener((LineEvent event) -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.open(AudioSystem.getAudioInputStream(new File(Globals.JARPATH + File.separator + "resources" + File.separator + "sound" + File.separator + tag + ".wav")));
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException exc) {
            exc.printStackTrace(System.out);
        }
    }
}
