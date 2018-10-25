package engine;

import interfaces.Renderable;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import util.Resources;

public abstract class Game implements Runnable {

    public JFrame frame;

    public DrawArea drawArea;

    public enum Status {
        NORMAL, PAUSED, QUIT
    }

    private Status status = Status.NORMAL;

    public Game() {

        // Set up visuals
        frame = new JFrame("Game");
        frame.setResizable(false);

        //if on windows, this needs to be true
        //on linux, this can be false
        //in our final build, it should be false,
        //but should be mentioned in our final report stuff
        frame.setUndecorated(Globals.UNDECORATED);
        frame.setSize(Globals.WIDTH, Globals.HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setCursor(frame.getToolkit().createCustomCursor(Resources.getImage("cursor"), new Point(14, 14), "Target Cursor"));

        drawArea = new DrawArea();

        frame.add(drawArea);

        //set up input
        frame.addKeyListener(Globals.INPUT);
        frame.addMouseListener(Globals.MOUSE_INPUT);
        frame.addMouseMotionListener(Globals.MOUSE_INPUT);

        //set up everything else
        //gameObjects = new CopyOnWriteArrayList<>();
        //not using this currently
    }

    public void add(Object o) {
        if (o instanceof Renderable) {
            drawArea.add((Renderable) o);
        }
    }

    public void remove(Object o) {
        if (o instanceof Renderable) {
            drawArea.remove((Renderable) o);
        }
    }

    public void addUI(Object o) {
        if (o instanceof Renderable) {
            drawArea.addUI((Renderable) o);
        }
    }

    public void removeUI(Object o) {
        if (o instanceof Renderable) {
            drawArea.removeUI((Renderable) o);
        }
    }

    protected final void changeStatus(Status toGoTo) {
        System.out.println("Changing status to " + toGoTo + " from " + status);
        switch (toGoTo) {
            case NORMAL:
                if (status == Status.PAUSED) {
                    //resume from pause
                }

                status = Status.NORMAL;

                break;
            case PAUSED:
                //go to pause state

                status = Status.PAUSED;

                break;
            case QUIT:
                status = Status.QUIT;
                //do quit cleanup
                break;
        }
    }

    public boolean isRunning() {
        return status == Status.NORMAL;
    }

    public boolean isRendering() {
        return status != Status.QUIT;
    }

    public abstract void tick();

    public abstract void start();

    @Override
    public final void run() {

        frame.setVisible(true);

        int sleepTime = 1_000_000_000 / 60;

        start();

        try {
            Thread.sleep(sleepTime / 1_000_000, sleepTime % 1_000_000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

        long time;
        while (isRendering()) {
            time = System.nanoTime();

//            System.out.println("repainting!");
            drawArea.repaint();
            if (isRunning()) {
                tick();
                drawArea.animateAll();
            }

            time = System.nanoTime() - time;

            try {
                //System.out.println("sleeping " + (sleepTime - time));
                Thread.sleep(Math.max(sleepTime - time, 0) / 1_000_000, (int) (Math.max(sleepTime - time, 0) % 1_000_000));
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //doesn't auto-terminate here because some other thread is also running
        //so must manually exit
        System.exit(0);
    }

}
