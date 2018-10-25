package interfaces;

import java.awt.image.BufferedImage;

/**
 * Any class that implements this interface can be added to Game, and then rendered to the screen based on getX() and getY()
 * @author Saejin
 */
public interface Renderable {
    
    /**
     *
     * @return the center x coordinate of this Renderable
     */
    public int getX();
    
    /**
     *
     * @return the center y coordinate of this Renderable
     */
    public int getY();
    
    /**
     * Called every visual frame, this should ONLY give the current image. Use animate() for any visual animations or logic for visuals.
     * @return the image to draw for this Renderable
     */
    public BufferedImage render();
    
    /**
     * Use this for any visual logic - it is called alongside render() in most cases, but only when the game is not paused
     */
    public void animate();
}
