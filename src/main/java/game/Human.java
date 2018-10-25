package game;

import engine.Button;
import engine.Globals;
import game.Card.CardGraphic;
import interfaces.InputListener;
import interfaces.Renderable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import util.MouseInput;
import util.Resources;

public final class Human extends Player {

    public static final int MAX_PLAYER_HEALTH = Integer.parseInt(Resources.getText("playerHealth"));

    private final PlayerHand hand;
    private final Button endTurnButton;



    CountDownLatch turnCountDown;

    public Human() {
        super(0, MAX_PLAYER_HEALTH);
        hand = new PlayerHand();
        MouseInput.registerLeftHitListener(hand);

        endTurnButton = new Button("", Resources.getImage("endTurn"), Resources.getImage("endTurnHover"), Globals.WIDTH - 128, Globals.HEIGHT / 2 - 45);
        endTurnButton.setListener(() -> {
            endTurn();

        });
        Globals.game.add(endTurnButton);
    }



    @Override
    public void turn() {
        turnCountDown = new CountDownLatch(1);
        //enables selection and playment of cards
        super.turn();
        endTurnButton.setVisible(true);

        Globals.game.popup("Your Turn!", 200);

        try {
            turnCountDown.await();
        } catch (InterruptedException ex) {
            System.out.println("Turn Thread interrupted!");
        }
    }

    @Override
    public void endTurn() {
        //do end turn stuff
        hand.deselectAll();
        endTurnButton.setVisible(false);
        super.endTurn();
        turnCountDown.countDown();
    }


    @Override
    public PlayerHand hand() {
        return hand;
    }

    public final class PlayerHand extends Hand implements Renderable, InputListener {

        CardStack cards;

        ArrayList<CardGraphic> graphics;

        int x, y;

        Card selected = null;

        BufferedImage img;

        public PlayerHand() {
            cards = new CardStack();
            graphics = new ArrayList<>(7);
            x = Globals.WIDTH / 2;
            y = Globals.HEIGHT - Math.round((Card.CARD_HEIGHT + 5) / 2f);

            update();
        }

        @Override
        public List<Card> cards() {
            return cards.cards;
        }

        @Override
        public void add(Card c) {
            if (cards.size() >= MAX_HAND_SIZE) {
                return;
            }
            c.graphic().showManaCost(true);
            c.setCanAttack(true);
            //c.graphic().setOutlineColor(Card.HUMAN_OUTLINE_COLOR);
            cards.add(c);
            //ensure card is NOT on battlefield
            //probably unneeded
            //Globals.game.remove(c);
            graphics.add(c.graphic());
            c.graphic().update();
            update();
        }

        @Override
        public void remove(Card c) {
            c.graphic().showManaCost(false);
            c.graphic().deselect();
            cards.remove(c);
            graphics.remove(c.graphic());
            update();
        }

        public void deselectAll() {
            if (selected != null)
                selected.graphic().deselect();
            selected = null;
            Globals.game.deselect();
            update();
        }

        @Override
        public void trigger() {
            //select event called

            if (MouseInput.getY() > Globals.HEIGHT - img.getHeight() - 10) {
                //select a new card
                Card closest = null;
                int dis = Integer.MAX_VALUE;
                for (Card c : cards) {
                    int d = Math.abs(c.graphic().getX() - MouseInput.getX());
                    if (d < dis) {
                        closest = c;
                        dis = d;
                    }
                }

                //deselect previous card
                if (selected != null) {
                    selected.graphic().deselect();
                }
                //select card
                if (closest != selected) {
                    selected = closest;
                }
                //check for null
                if (selected != null) {
                    selected.graphic().select();
                    update();
                }
            } else //put a card out
                if (selected != null && playingNow) {
                    if (canPlayCard(selected)) {

                        play(selected);
                        selected = null;
                    } else if (selected != null) {
                        selected.graphic().deselect();
                        selected = null;
                        update();
                    }
                }
        }

        private void play(Card c) {
            remove(c);

            boolean dir = false;
            //guarenteed to not run out of space as this method will not be
            //called if already have max cards out

            int i = battlefield.length / 2, o = 0;
            while (battlefield[i + (dir ? o : -o)] != null) {
                if (dir)
                    o++;
                dir = !dir;
            }

            c.graphic().setLocation(x + (dir ? o : -o) * (Card.CARD_WIDTH + UI.UI_PADDING * 5), y - Card.CARD_HEIGHT - UI.UI_PADDING * 10);
            battlefield[i + (dir ? o : -o)] = c;
            c.embark();
        }

        @Override
        public int getX() {

            return x;
        }

        @Override
        public int getY() {

            return y;
        }

        public void update() {
            if (graphics.isEmpty()) {
                img = new BufferedImage(10, Card.CARD_HEIGHT + 5, BufferedImage.TYPE_INT_ARGB);
                //img.createGraphics().fillRect(0, 0, 10, 10);

                return;
            }
            BufferedImage i = new BufferedImage(
                    (Card.CARD_WIDTH + 10) * graphics.size(),
                    Card.CARD_HEIGHT,
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = i.createGraphics();
            //g.fillRect(0, 0, i.getWidth(), i.getHeight());

            int cX = 0;
            for (CardGraphic c : graphics) {
                cX += 5; // 5px padding before
                g.drawImage(c.render(), cX, 0, null);
                c.setX((x - i.getWidth() / 2f) + cX + c.render().getWidth() / 2f);
                cX += c.render().getWidth();
                cX += 5; // 5px padding after
                g.setColor(Color.RED);
            }

            img = i;
        }

        @Override
        public BufferedImage render() {
            return img;
        }

        @Override
        public void animate() {
            for (Card c : cards()) {
                c.graphic().animate();
            }

        }

    }

}
