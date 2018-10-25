package engine;

import interfaces.Renderable;
import java.awt.image.BufferedImage;
import util.Vector2;

public abstract class GameObject implements Renderable {

    Vector2 pos;
    protected BufferedImage img;

    public GameObject(float x, float y, BufferedImage img) {
        pos = new Vector2(x, y);
        this.img = img;
    }

    public final void setLocation(float x, float y) {
        setX(x);
        setY(y);
    }

    public final void move(float x, float y) {
        setX(getExactX() + x);
        setY(getExactY() + y);
    }

    @Override
    public final int getX() {
        return Math.round(pos.getX());
    }

    public final float getExactX() {
        return pos.getX();
    }

    public final void setX(float x) {
        pos.setX(x);
    }

    @Override
    public final int getY() {
        return Math.round(pos.getY());
    }

    public float getExactY() {
        return pos.getY();
    }

    public final void setY(float y) {
        pos.setY(y);
    }

    public final void setImage(BufferedImage im) {
        img = im;
    }

    //this method is final so that it will ALWAYS just return the image. Use animate() to do anything else per-frame.
    @Override
    public final BufferedImage render() {
        return img;
    }

    @Override
    public void animate() {
        //just a stub so that a GameObject implementation does not have to have an animate().

    }
}
