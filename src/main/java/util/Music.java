package util;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import javax.sound.sampled.*;

public final class Music extends Thread {

    private ArrayList<Clip> music;
    private int index_of_playing = 0;
    private boolean playing = false;
    private boolean running = true;
    private boolean mute = true;

    public Music() {
        music = new ArrayList<>();

        File m = new File("resources" + File.separator + "music");
        if (!m.exists()) {
            m.mkdirs();
        }
        //load .wav's in folder 'music'
        File[] listFiles = m.listFiles(new WAVFilter());
        for (File file : listFiles) {
            try {
                Clip c = AudioSystem.getClip();
                c.open(AudioSystem.getAudioInputStream(file));
                c.addLineListener((LineEvent e) -> {
                    if (e.getFramePosition() >= ((Clip) e.getLine()).getFrameLength())
                        next();
                });
                music.add(c);
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                System.out.println("unable to load music");
                music.clear();
            }
        }

        System.out.println(music);

        //input listeners for skipping and mute
        Input.registerKeyListener(KeyEvent.VK_M, () -> toggleMute());
        Input.registerKeyListener(KeyEvent.VK_N, () -> next());

        setMute(mute);
    }

    @Override
    public void run() {
        playing = false;
        index_of_playing = (int) (Math.random() * music.size());
        next();
    }

    public boolean isPlaying() {
        return playing;
    }

    public Clip get(int index) {
        if (index >= 0 && index < music.size()) {
            return music.get(index);
        }
        return null;
    }

    public void next() {
        System.out.println("Next Song!");
        Clip c = get(index_of_playing);
        if (c != null)
            c.stop();
        int next;
        do {
            next = (int) (Math.random() * music.size());
        } while (index_of_playing == next);
        index_of_playing = next;
        music.get(index_of_playing).setFramePosition(0);
        music.get(index_of_playing).start();
        playing = true;
        setMute(mute);
    }

    public void toggleMute() {
        Clip c = get(index_of_playing);
        if (c != null) {
            BooleanControl vol = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
            vol.setValue(!vol.getValue());
            mute = !mute;
        }
    }

    public void setMute(boolean mute) {
        Clip c = get(index_of_playing);
        if (c != null) {
            BooleanControl vol = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
            vol.setValue(mute);
            this.mute = mute;
        }
    }

    public void end() {
        running = false;
        interrupt();
    }

}

class WAVFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        return pathname.isFile() && pathname.getName().endsWith(".wav");
    }

}
