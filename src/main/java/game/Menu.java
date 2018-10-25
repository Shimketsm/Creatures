package game;

import engine.Globals;
import interfaces.InputListener;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import util.MouseInput;
import util.Resources;

import static game.Menu.Result.*;

/**
 * This code is adapted from a previous project, from quite a while ago. It is mostly just for decoration, however.
 *
 * @author Saejin
 */
public class Menu extends JFrame implements InputListener {



    private static final int W_WIDTH = Globals.WIDTH;
    private static final int W_HEIGHT = Globals.HEIGHT;
    private static final long serialVersionUID = 1L;
    private Result result = EXIT;
    private MenuArea menuArea = new MenuArea();
    private int mouseX;
    private int mouseY;
    private boolean buttonHovered = false;
    private Rectangle singleplayer_bounds = new Rectangle(W_WIDTH / 2 - (getStringPixelLength("Play") / 2) - 2, (W_HEIGHT / 2 - 16) - (getStringPixelHeight("Play") / 2) - 2, getStringPixelLength("Play") + 4, getStringPixelHeight("Play") + 4);
    private Rectangle exit_bounds = new Rectangle(W_WIDTH / 2 - (getStringPixelLength("Exit") / 2) - 2, (W_HEIGHT / 2 + 16) - (getStringPixelHeight("Exit") / 2) - 2, getStringPixelLength("Exit") + 4, getStringPixelHeight("Exit") + 4);

    public enum Result {
        PLAY, EXIT, RUNNING
    }


    public Menu() {
        super("Creatures");
        setSize(W_WIDTH, W_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.GRAY);
        setCursor(getToolkit().createCustomCursor(Resources.getImage("cursor"), new Point(14, 14), "Target Cursor"));


        add(menuArea);

    }

    public Result getResult() {
        visible(true);

        addMouseListener(Globals.MOUSE_INPUT);
        addMouseMotionListener(Globals.MOUSE_INPUT);
        addKeyListener(Globals.INPUT);
        MouseInput.registerLeftHitListener(this);
        result = RUNNING;
        while (result == RUNNING) {
            menuArea.update();
        }
        visible(false);

        removeMouseListener(Globals.MOUSE_INPUT);
        removeMouseMotionListener(Globals.MOUSE_INPUT);
        removeKeyListener(Globals.INPUT);
        MouseInput.unregisterLeftHitListener(this);

        return result;
    }

    private void visible(final boolean visible) {
        java.awt.EventQueue.invokeLater(() -> {
            setVisible(visible);
        });
    }

    public void end() {
        java.awt.EventQueue.invokeLater(() -> {
            setVisible(false);

            removeMouseListener(Globals.MOUSE_INPUT);
            removeMouseMotionListener(Globals.MOUSE_INPUT);
            removeKeyListener(Globals.INPUT);
            MouseInput.unregisterLeftHitListener(this);

            dispose();
        });
    }

    @Override
    public void trigger() {
        mouseX = MouseInput.getX();
        mouseY = MouseInput.getY();

        if (singleplayer_bounds.contains(mouseX, mouseY)) {
            System.out.println("Play clicked!");
            result = PLAY;
        } else if (exit_bounds.contains(mouseX, mouseY)) {
            System.out.println("Exit clicked!");
            result = EXIT;
        }
    }

    private class MenuArea extends JComponent {

        private static final long serialVersionUID = 1L;
        CopyOnWriteArrayList<Star> stars = new CopyOnWriteArrayList<>();
        boolean hoverPlayed = false;
        private long last_frame_moved = System.currentTimeMillis();
        Font font;

        public MenuArea() {
            setFont(new Font("Ariel", Font.BOLD, 18));

            for (int i = 0; i <= 490; i++) {
                stars.add(new Star((int) Math.round(Math.random() * W_WIDTH), (int) Math.round(Math.random() * W_HEIGHT), 1));
                stars.add(new Star((int) Math.round(Math.random() * W_WIDTH), (int) Math.round(Math.random() * W_HEIGHT), 2));
                stars.add(new Star((int) Math.round(Math.random() * W_WIDTH), (int) Math.round(Math.random() * W_HEIGHT), 3));
                stars.add(new Star((int) Math.round(Math.random() * W_WIDTH), (int) Math.round(Math.random() * W_HEIGHT), 5));
                stars.add(new Star((int) Math.round(Math.random() * W_WIDTH), (int) Math.round(Math.random() * W_HEIGHT), 8));
            }


        }

        public void update() {

            if (System.currentTimeMillis() - last_frame_moved > 25) {
                for (Star s : stars) {
                    if (s.dead()) {
                        stars.remove(s);
                    }
                    s.move();
                }
                stars.add(new Star(1));
                stars.add(new Star(2));
                stars.add(new Star(3));
                stars.add(new Star(4));
                stars.add(new Star(5));
                stars.add(new Star(6));
                stars.add(new Star(7));
                stars.add(new Star(8));
                stars.add(new Star(9));
                stars.add(new Star(10));
                last_frame_moved = System.currentTimeMillis();

            }
            repaint();
        }

        @Override
        public final void setFont(Font f) {
            font = f;
        }

        @Override
        public Font getFont() {
            return font;
        }

        @Override
        public void paintComponent(Graphics gr) {
            Graphics2D g = (Graphics2D) gr;
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(font);

            mouseX = MouseInput.getX();
            mouseY = MouseInput.getY();

            buttonHovered = exit_bounds.contains(mouseX, mouseY) || singleplayer_bounds.contains(mouseX, mouseY);

            g.setColor(Color.BLACK);

            g.fillRect(0, 0, W_WIDTH, W_HEIGHT);

            g.setColor(Color.WHITE);
            for (Star s : stars) {
                g.drawRect(s.x, s.y, 0, 0);
            }
            try {
                BufferedImage logo = ImageIO.read(new File("art/logo.png"));

                g.drawImage(logo, W_WIDTH / 2 - logo.getWidth() / 2, W_HEIGHT / 2 - 125, null);
            } catch (IOException ex) {
                g.setColor(Color.CYAN.darker().darker().darker().darker());
                g.setFont(g.getFont().deriveFont(Font.BOLD, 24));
                g.drawString("Creatures", (W_WIDTH / 2) - (getStringPixelLength("Creatures", g.getFont()) / 2) - 2, (W_HEIGHT / 2 - 85) + (getStringPixelHeight("Creatures", g.getFont()) / 2));
                g.setFont(font);
            }


            if (singleplayer_bounds.contains(mouseX, mouseY)) {
                g.setColor(Color.WHITE);
                g.drawString("Play", singleplayer_bounds.x + 2, singleplayer_bounds.y + getStringPixelHeight("Play") - 2);
                g.drawString("Play", singleplayer_bounds.x + 3, singleplayer_bounds.y + getStringPixelHeight("Play") - 3);
            } else {
                g.setColor(Color.WHITE.darker().darker());
                g.drawString("Play", singleplayer_bounds.x + 2, singleplayer_bounds.y + getStringPixelHeight("Play") - 2);
//                    g.draw(singleplayer_bounds);
            }

            if (exit_bounds.contains(mouseX, mouseY)) {
                g.setColor(Color.WHITE);
                g.drawString("Exit", exit_bounds.x + 2, exit_bounds.y + getStringPixelHeight("Exit") - 2);
                g.drawString("Exit", exit_bounds.x + 3, exit_bounds.y + getStringPixelHeight("Exit") - 3);
            } else {
                g.setColor(Color.WHITE.darker().darker());
                g.drawString("Exit", exit_bounds.x + 2, exit_bounds.y + getStringPixelHeight("Exit") - 3);
//                    g.draw(exit_bounds);
            }

            Font org = g.getFont();
            g.setFont(new Font("Ariel", Font.BOLD, 10));
            g.setColor(Color.WHITE.darker().darker());
            g.drawString("Hit [M] to toggle mute, and [N] to jump to the next music track!", Globals.WIDTH - getStringPixelLength("Hit [M] to toggle mute, and [N] to jump to the next music track!", g.getFont()) - 5, Globals.HEIGHT - 15);
            g.drawString("Music courtesy of Kevin Macleod  -  www.incompetech.com", Globals.WIDTH - getStringPixelLength("Music courtesy of Kevin Macleod  -  www.incompetech.com", g.getFont()) - 5, Globals.HEIGHT - 5);
            g.setFont(org);
        }
    }

    private class Star {

        int x = 0;
        int y;
        int speed; //pix per frame
        boolean dead = false;

        public Star(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }

        public Star(int y, int speed) {
            this.y = y;
            this.speed = speed;

        }

        public Star(int speed) {
            x = 0;
            y = (int) Math.round(Math.random() * W_HEIGHT);
            this.speed = speed;
        }

        public void move() {
            x += speed;
            if (x >= W_WIDTH) {
                dead = true;
            }
        }

        public void move(int dir) {
            if (dir != -1 && dir != 1) {
                return;
            }
            x += dir * speed;
        }

        public boolean dead() {
            return dead;
        }
    }

    public int getStringPixelLength(String s) {
        Font f = menuArea.getFont();
        Rectangle2D r = f.getStringBounds(s, new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT));
        return (int) Math.round(r.getWidth());

    }

    public int getStringPixelHeight(String s) {
        Font f = menuArea.getFont();
        Rectangle2D r = f.getStringBounds(s, new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT));
        return (int) Math.round(r.getHeight());

    }

    public Rectangle2D getStringBounds(String s) {
        Font f = menuArea.getFont();
        return f.getStringBounds(s, new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT));
    }

    public static int getStringPixelLength(String s, Font f) {
        Rectangle2D r = f.getStringBounds(s, new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT));
        return (int) Math.round(r.getWidth());

    }

    public static int getStringPixelHeight(String s, Font f) {
        Rectangle2D r = f.getStringBounds(s, new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT));
        return (int) Math.round(r.getHeight());

    }

    public static Rectangle2D getStringBounds(String s, Font f) {
        return f.getStringBounds(s, new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT));
    }
}
