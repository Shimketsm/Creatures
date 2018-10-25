package engine;
import interfaces.Renderable;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PopUp implements Renderable {


    int x, y;
    int totalFrames;
    int count;
    float opacity;
    float opacityStep;

    boolean finished = false;

    BufferedImage img;
    BufferedImage org;

    public PopUp(BufferedImage img, int x, int y, int frames) {
        this.x = x;
        this.y = y;
        this.org = img;
        pop(frames);
    }

    public PopUp(String text, Color bg, int x, int y, int frames) {
        this.x = x;
        this.y = y;

        org = new BufferedImage(Math.round(Globals.FONT_METRICS.stringWidth(text) * 2f), Math.round(Globals.FONT_METRICS.getHeight() * 2f), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = org.createGraphics();
        g.setColor(bg);
        //g.drawRect(0, 0, org.getWidth() - 1, org.getHeight() - 1);
        g.fillOval(0, 0, org.getWidth() - 1, org.getHeight() - 1);
        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 16));
        g.drawString(text, Math.round(org.getWidth() / 2 - g.getFontMetrics().stringWidth(text) / 2), Math.round(org.getHeight() / 2f + g.getFontMetrics().getAscent() / 2.8f));
        g.dispose();

        pop(frames);
    }

    public final void pop(int frames) {
        totalFrames = frames;

        opacityStep = 1 / (totalFrames / 8f);

        img = new BufferedImage(org.getWidth(), org.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Globals.game.addUI(this);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public BufferedImage render() {
        return img;
    }

    public boolean finished() {
        return finished;
    }

    @Override
    public void animate() {
        count++;

        if (count > totalFrames) {
            Globals.game.removeUI(this);
            finished = true;
            return;
        }


        boolean doWork = false;
        if (count < totalFrames / 4) {
            opacity += opacityStep;
            doWork = true;
        } else if (count > totalFrames - (totalFrames / 4)) {
            opacity -= opacityStep;
            doWork = true;
        }

        if (opacity < 0) opacity = 0;
        else if (opacity > 1) opacity = 1;

        if (doWork) {
            Graphics2D g = img.createGraphics();
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, img.getWidth(), img.getHeight());
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g.drawImage(org, 0, 0, null);
        }
    }
}
