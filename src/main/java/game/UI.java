package game;

import engine.GameObject;
import engine.Globals;
import interfaces.Renderable;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import util.MouseInput;
import util.Resources;

public class UI implements Renderable {

    public static final int ICON_SIZE = 32;
    public static final int UI_PADDING = 2;


    BufferedImage screen;

    Human human;
    AI ai;

    Face aiFace, huFace;

    int drawnManaH;
    int drawnHealthH;

    int drawnManaA;
    int drawnHealthA;

    public UI(Human human, AI ai) {
        screen = new BufferedImage(Globals.WIDTH, Globals.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        this.human = human;
        this.ai = ai;
        huFace = new Face(human);
        aiFace = new Face(ai);
        Globals.game.addUI(huFace);
        Globals.game.addUI(aiFace);
        update();
    }

    @Override
    public int getX() {
        return Globals.WIDTH / 2;
    }

    @Override
    public int getY() {
        return Globals.HEIGHT / 2;
    }

    public final void update() {
        BufferedImage scr = new BufferedImage(Globals.WIDTH, Globals.HEIGHT, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = scr.createGraphics();

        drawHuman(g);
        drawAI(g);

//        for (Card c : human.hand().cards()) {
//            Rectangle b = new Rectangle(c.graphic().getX() - Card.CARD_WIDTH / 2, human.hand().getY() - Card.CARD_HEIGHT / 2, Card.CARD_WIDTH, Card.CARD_HEIGHT);
//            g.setColor(Color.GREEN);
//            System.out.println(b);
//            g.fillRect(c.graphic().getX() - Card.CARD_WIDTH / 2, human.hand().getY() - Card.CARD_HEIGHT / 2, Card.CARD_WIDTH, Card.CARD_HEIGHT);
//        }

//        g.setColor(Color.RED);
//        g.draw(Face.aiBounds);

        g.dispose();
        screen = scr;
    }

    private void drawAI(Graphics2D g) {
        int drawnSoFar = 0;
        for (int y = UI_PADDING * 2; true; y += ICON_SIZE + UI_PADDING) {
            if (drawnSoFar >= ai.getMana())
                break;

//            if (y < Globals.HEIGHT / 2) {
//                x += ICON_SIZE + UI_PADDING;
//                y = ICON_SIZE - UI_PADDING;
//            }

            g.drawImage(Resources.getImage("mana"), ICON_SIZE + UI_PADDING * 2, y, ICON_SIZE, ICON_SIZE, null);
            drawnSoFar++;
        }

        drawnSoFar = 0;
        for (int y = UI_PADDING * 2; true; y += ICON_SIZE + UI_PADDING) {
            if (drawnSoFar >= ai.getHealth())
                break;

//            if (y < Globals.HEIGHT / 2 + UI_PADDING) {
//                x += ICON_SIZE + UI_PADDING;
//                y = ICON_SIZE - UI_PADDING;
//            }


            g.drawImage(Resources.getImage("health"), UI_PADDING, y, ICON_SIZE, ICON_SIZE, null);
            drawnSoFar++;
        }

        drawnHealthA = ai.getHealth();
        drawnManaA = ai.getMana();
    }

    private void drawHuman(Graphics2D g) {
        int drawnSoFar = 0;
        for (int y = Globals.HEIGHT - ICON_SIZE - UI_PADDING; true; y -= ICON_SIZE + UI_PADDING) {
            if (drawnSoFar >= human.getMana())
                break;

//            if (y < Globals.HEIGHT / 2) {
//                x += ICON_SIZE + UI_PADDING;
//                y = Globals.HEIGHT - ICON_SIZE - UI_PADDING;
//            }

            g.drawImage(Resources.getImage("mana"), Globals.WIDTH - ICON_SIZE * 2 - UI_PADDING * 2, y, ICON_SIZE, ICON_SIZE, null);
            drawnSoFar++;
        }

        drawnSoFar = 0;
        for (int y = Globals.HEIGHT - ICON_SIZE - UI_PADDING; true; y -= ICON_SIZE + UI_PADDING) {
            if (drawnSoFar >= human.getHealth())
                break;

//            if (y < Globals.HEIGHT / 2 + UI_PADDING) {
//                x += ICON_SIZE + UI_PADDING;
//                y = Globals.HEIGHT - ICON_SIZE - UI_PADDING;
//            }


            g.drawImage(Resources.getImage("health"), Globals.WIDTH - ICON_SIZE - UI_PADDING, y, ICON_SIZE, ICON_SIZE, null);
            drawnSoFar++;
        }

        drawnHealthH = human.getHealth();
        drawnManaH = human.getMana();



    }

    @Override
    public BufferedImage render() {
        return screen;
    }

    @Override
    public void animate() {
        update();
        if (drawnManaH != human.getMana() || drawnHealthH != human.getHealth() || drawnManaA != ai.getMana() || drawnHealthA != ai.getHealth()) {
            update();
        }
    }



    public static class Face extends GameObject {

        public static final int HIT_KNOCK_BACK = 64;
        public static final int HIT_KNOCK_BACK_HIT_DELT = 12;
        public static final int HIT_KNOCK_BACK_REVERSE_DELT = 1;



        static BufferedImage human = Resources.getImage("doge");
        static BufferedImage ai = Resources.getImage("wenskovitch");

        static final int WIDTH = 96;
        static final int HEIGHT = 128;

        static final int HX = WIDTH / 2 + UI.ICON_SIZE, HY = Globals.HEIGHT - HEIGHT / 2;
        static final int AX = Globals.WIDTH - WIDTH / 2 - UI.ICON_SIZE, AY = HEIGHT / 2;

        static final Rectangle aiBounds = new Rectangle(AX - WIDTH / 2, AY - HEIGHT / 2, WIDTH, HEIGHT);

        private Player player;


        public Face(Player player) {
            super((player instanceof Human ? HX : AX), (player instanceof Human ? HY : AY), (player instanceof Human ? human : ai));
            this.player = player;
            if (player instanceof AI)
                MouseInput.registerLeftHitListener(
                        () -> {
                    if (aiBounds.contains(MouseInput.getX(), MouseInput.getY()))
                        hit();
                });

        }

        public boolean hit() {
            if (Globals.game.selected() != null && player.hasTaunt() == false && Globals.game.selected().owner() != player) {
                if (Globals.game.selected().canAttack()) {
                    if (player.doDamage()) {
                        doHit();
                        Globals.game.selected().didAttack();
                        player.update();
                        return true;
                    }
                }
            }
            return false;
        }

        int offset = 0;
        int delt = 0;
        boolean doingHit;

        public void doHit() {
            //System.out.println("HIT");
            doingHit = true;
            delt = HIT_KNOCK_BACK_HIT_DELT;
        }

        @Override
        public void animate() {
            if (doingHit) {
                if (offset >= HIT_KNOCK_BACK)
                    delt = -HIT_KNOCK_BACK_REVERSE_DELT;
                offset += delt;
                setY((player instanceof Human ? HY + offset : AY - offset));
                if (player instanceof Human ? getY() <= HY : getY() >= AY) {
                    doingHit = false;
                    setY((player instanceof Human ? HY : AY));
                }
            }
        }

    }

}
