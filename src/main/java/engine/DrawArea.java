package engine;

import interfaces.Renderable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JComponent;

public class DrawArea extends JComponent {

    public static BufferedImage BACKGROUND = null;

    private static final long serialVersionUID = 1L;


    private final CopyOnWriteArrayList<Renderable> renderables = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<Renderable> uiRenderables = new CopyOnWriteArrayList<>();

    public DrawArea() {
        super.setSize(Globals.WIDTH, Globals.HEIGHT);
    }

    public static void setBG(BufferedImage bg) {
        BACKGROUND = bg;
    }

    public void animateAll() {

        for (Renderable r : renderables) {
            r.animate();
        }

        for (Renderable r : uiRenderables) {
            r.animate();
        }
    }

    @Override
    public void paint(Graphics g2) {
        Graphics2D g = (Graphics2D) g2;
        if (Globals.FONT_METRICS == null) {
            Font f = g.getFont();
            g.setFont(f.deriveFont(Font.BOLD, 16));
            g.setFont(f);
            Globals.FONT_METRICS = g.getFontMetrics();
        }
        if (BACKGROUND == null) {
            g.setColor(Globals.BG_COLOR);
            g.fillRect(0, 0, Globals.WIDTH, Globals.HEIGHT);
        } else {
            g.drawImage(BACKGROUND, 0, 0, Globals.WIDTH, Globals.HEIGHT, null);
        }

        //using lambdas because a) they are cool, and b) supposedly more performant once in bytecode

        for (Renderable r : renderables) {
            BufferedImage i = r.render();
            g.drawImage(i, r.getX() - i.getWidth() / 2, r.getY() - i.getHeight() / 2, null);
        }

        for (Renderable r : uiRenderables) {
            BufferedImage i = r.render();
            g.drawImage(i, r.getX() - i.getWidth() / 2, r.getY() - i.getHeight() / 2, null);
        }

    }

    public void add(Renderable t) {
        if (!renderables.contains(t)) {
            renderables.add(t);
        }
    }

    public void remove(Renderable t) {
        renderables.remove(t);
    }

    public void addUI(Renderable t) {
        if (!uiRenderables.contains(t)) {
            uiRenderables.add(t);
        }
    }

    public void removeUI(Renderable t) {
        uiRenderables.remove(t);
    }


}
