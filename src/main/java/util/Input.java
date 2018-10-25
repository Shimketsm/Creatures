package util;

import interfaces.InputListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class Input extends KeyAdapter {
    private static final HashSet<Integer> KEYS_DOWN = new HashSet<>();
    private static final HashMap<Integer, List<InputListener>> KEY_LISTENERS = new HashMap<>();

    @Override
    public void keyReleased(KeyEvent e) {
        KEYS_DOWN.remove(e.getKeyCode());
        e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        KEYS_DOWN.add(e.getKeyCode());
        List<InputListener> i = KEY_LISTENERS.get(e.getKeyCode());
        if (i != null) {
            for (InputListener l : i) {
                l.trigger();
            }
        }
        e.consume();
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Integer> getKeysDown() {
        return new ArrayList<>(KEYS_DOWN);
    }

    public static void registerKeyListener(int key, InputListener listener) {
        if (KEY_LISTENERS.get(key) == null) {
            List<InputListener> l = new LinkedList<>();
            l.add(listener);
            KEY_LISTENERS.put(key, l);

        } else {
            KEY_LISTENERS.get(key).add(listener);
        }
    }

    public static void unregisterKeyListener(int key, InputListener listener) {
        if (KEY_LISTENERS.get(key) != null) {
            KEY_LISTENERS.get(key).remove(listener);
        }
    }

    /**
     *
     * @param keyCode - code to check against for downness.
     * @return True or false depending on the result of the check.
     */
    public static boolean isKeyDown(int keyCode) {
        return KEYS_DOWN.contains(keyCode);
    }
}
