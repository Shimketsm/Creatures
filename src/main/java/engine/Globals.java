package engine;

import game.Creatures;
import game.Menu;
import game.Menu.Result;
import java.awt.Color;
import java.awt.FontMetrics;
import util.*;


public class Globals {

    public static final String JARPATH = Globals.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    public static final int WIDTH = 1080;
    public static final int HEIGHT = 720;
    public static final Color BG_COLOR = Color.BLACK;
    public static boolean UNDECORATED = true;

    public static Input INPUT = new Input();
    public static MouseInput MOUSE_INPUT = new MouseInput();

    public static FontMetrics FONT_METRICS;

    //the game object
    public static Creatures game;

    public static void toMenu() {
        Menu menu;
        boolean running = true;
        menu = new Menu();
        Result r = menu.getResult();
        menu.end();
        //stick this in a for loop to go back to menu, but buggy with other things not resetting
        if (r == Result.PLAY) {
            game = new Creatures();

//                Thread gameThread = new Thread(game);
//                gameThread.start();

            game.run();

        } else {
            running = false;
            System.exit(0);
        }
    }

    public static void main(String[] args) {

        final Music music = new Music();
        music.start();

        toMenu();

    }

}
