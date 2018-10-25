package util;

import interfaces.InputListener;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.CopyOnWriteArraySet;



public class MouseInput extends MouseAdapter {

    //ensure init to avoid null pointer
    private static MouseEvent currentMouseEvent;
    private static int currentX, currentY;
    private static boolean rightMouseDown;
    private static boolean leftMouseDown;
    private static final CopyOnWriteArraySet<InputListener> LEFT_LISTENERS = new CopyOnWriteArraySet<>();
    private static final CopyOnWriteArraySet<InputListener> RIGHT_LISTENERS = new CopyOnWriteArraySet<>();
    private static final CopyOnWriteArraySet<AreaListener> AREA_LISTENERS = new CopyOnWriteArraySet<>();



    static class AreaListener {
        Rectangle area;
        InputListener listener;

        public AreaListener(InputListener l, Rectangle r) {
            area = r;
            listener = l;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (currentMouseEvent != null) currentMouseEvent.consume();
        currentMouseEvent = e;
        currentX = e.getX();
        currentY = e.getY();


        if (e.getButton() == MouseEvent.BUTTON1) {
            leftMouseDown = true;
            for (InputListener t : LEFT_LISTENERS) {
                t.trigger();
            }

            for (AreaListener l : AREA_LISTENERS) {
                if (l.area.contains(e.getX(), e.getY())) {
                    l.listener.trigger();
                }
            }
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            rightMouseDown = true;
            for (InputListener t : RIGHT_LISTENERS) {
                t.trigger();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (currentMouseEvent != null) currentMouseEvent.consume();
        currentMouseEvent = e;
        currentX = e.getX();
        currentY = e.getY();

        if (e.getButton() == MouseEvent.BUTTON1) leftMouseDown = false;
        if (e.getButton() == MouseEvent.BUTTON3) rightMouseDown = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (currentMouseEvent != null) currentMouseEvent.consume();
        currentMouseEvent = e;
        currentX = e.getX();
        currentY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (currentMouseEvent != null) currentMouseEvent.consume();
        currentMouseEvent = e;
        currentX = e.getX();
        currentY = e.getY();
    }

    public static boolean isMouseDown() {
        return rightMouseDown || leftMouseDown;
    }

    public static boolean isRightMouseDown() {
        return rightMouseDown;
    }

    public static boolean isLeftMouseDown() {
        return leftMouseDown;
    }

    public static boolean isBothMouseDown() {
        return rightMouseDown && leftMouseDown;
    }

    public static MouseEvent getMouseEvent() {
        return currentMouseEvent;
    }

    public static int getX() {
        return currentX;
    }

    public static int getY() {
        return currentY;
    }

    public static void registerLeftHitListener(InputListener trigger) {
        LEFT_LISTENERS.add(trigger);
    }

    public static void registerRightHitListener(InputListener trigger) {
        RIGHT_LISTENERS.add(trigger);
    }

    public static void registerAreaListener(Rectangle area, InputListener trigger) {
        AREA_LISTENERS.add(new AreaListener(trigger, area));
    }

    public static void unregisterLeftHitListener(InputListener trigger) {
        LEFT_LISTENERS.remove(trigger);
    }

    public static void unregisterRightHitListener(InputListener trigger) {
        RIGHT_LISTENERS.remove(trigger);
    }

    public static void unregisterAreaListener(InputListener trigger) {
        throw new UnsupportedOperationException("Unregistering AreaListeners is not supported");
    }
}
