package engine;
import interfaces.InputListener;
import interfaces.Renderable;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import util.MouseInput;


public class Button implements Renderable, InputListener {

    private BufferedImage hover;
    private BufferedImage up;

    private Rectangle bounds;

    private String text;
    private boolean hovered;

    private InputListener listener;

    private boolean visible = true;


    public Button(String text, BufferedImage up, BufferedImage hover, Rectangle bounds) {
        this.text = text;
        this.up = up;
        this.hover = hover;
        this.bounds = bounds;

        Graphics2D g = up.createGraphics();
        g.setFont(g.getFont().deriveFont(Font.BOLD, 16));
        Rectangle2D b = g.getFontMetrics().getStringBounds(text, g);
        g.drawString(text, Math.round(up.getWidth() / 2f - b.getWidth() / 2f), Math.round(up.getHeight() / 2f + b.getHeight() / 2f));


        MouseInput.registerLeftHitListener(this);
    }

    public Button(String text, BufferedImage up, BufferedImage hover, int x, int y) {
        this(text, up, hover, new Rectangle(x, y, up.getWidth(), up.getHeight()));
    }

    public Button(String text, BufferedImage img, int x, int y) {
        this(text, img, img, x, y);
    }

    public void setListener(InputListener l) {
        listener = l;
    }

    public void setVisible(boolean vis) {
        visible = vis;
    }

    @Override
    public void trigger() {
        if (hovered && visible) listener.trigger();
    }

    @Override
    public int getX() {
        return (int) Math.round(bounds.getCenterX());
    }

    @Override
    public int getY() {
        return (int) Math.round(bounds.getCenterY());
    }

    @Override
    public BufferedImage render() {
        if (!visible) return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return hovered ? hover : up;
    }

    @Override
    public void animate() {
        if (!visible) return;
        hovered = bounds.contains(MouseInput.getX(), MouseInput.getY());
    }
}
