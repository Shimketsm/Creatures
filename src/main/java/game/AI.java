package game;

import engine.Globals;
import interfaces.Renderable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import util.Resources;

import static game.Player.Hand.MAX_HAND_SIZE;

public class AI extends Player {

    public static final int MAX_ENEMY_HEALTH = Integer.parseInt(Resources.getText("enemyHealth"));
    public static final int ENEMY_STARTING_MANA = Integer.parseInt(Resources.getText("enemyStartingMana"));

    AIHand hand;

    private int currPlaceX = (int) (Card.CARD_WIDTH);
    private int currPlaceY = Card.CARD_HEIGHT;


    public AI() {
        super(ENEMY_STARTING_MANA, MAX_ENEMY_HEALTH);

        hand = new AIHand();
    }

    @Override
    public Hand hand() {
        return hand;
    }

    @Override
    public void turn() {
        super.turn();
        Globals.game.popup("AI's Turn!", 200);

        //do logic and movement, can use thread.sleep

        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
        }

        //PLAY CARDS
        for (Card c : hand().cards()) {
            if (canPlayCard(c)) {
                //put out the card
                play(c);

            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
            }
        }

        //ATTACK WITH CARDS

        boolean hitFace = !Globals.game.human.hasTaunt();
        List<Card> hitable = new ArrayList<>();
        for (Card c : cardsInPlay()) {
            Globals.game.human.update();
            if (c.canAttack()) {

                //update human cards on battlefield
                Globals.game.select(c);
                //pause so player can follow action
                try {
                    Thread.sleep(999);
                } catch (InterruptedException ex) {
                }

                if (hitFace) {
                    if (Globals.game.ui.huFace.hit() == false) hitFace = false;
                } else {
                    hitable.clear();
                    Card maxDmg = null;
                    int dmg = 0;

                    for (Card ec : Globals.game.human.cardsInPlay()) {
                        if (ec.getHealth() <= c.getAttack()) hitable.add(ec);
                        if (ec.getAttack() > dmg) {
                            dmg = ec.getAttack();
                            maxDmg = ec;
                        }
                    }

                    if (hitable.isEmpty() && maxDmg != null) {
                        Globals.game.doEncounter(maxDmg, c);
                    } else {

                        maxDmg = null;
                        dmg = 0;
                        for (Card ecc : hitable) {
                            if (ecc.getAttack() > dmg) {
                                dmg = ecc.getAttack();
                                maxDmg = ecc;
                            }
                        }

                        if (maxDmg != null) {
                            Globals.game.doEncounter(maxDmg, c);
                        } else {
                            //try to hit face just in case, this can only happen if no cards are on field

                            Globals.game.ui.huFace.hit();

                        }

                    }
                }



            }


        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {
        }
        super.endTurn();
    }

    private void play(Card c) {
        hand.remove(c);

        boolean dir = false;
        //guarenteed to not run out of space as this method will not be
        //called if already have max cards out

        int i = battlefield.length / 2, o = 0;
        while (battlefield[i + (dir ? o : -o)] != null) {
            if (dir)
                o++;
            dir = !dir;
        }

        c.graphic().setLocation(hand.getX() + (dir ? o : -o) * (Card.CARD_WIDTH + UI.UI_PADDING * 5), hand.getY() + Card.CARD_HEIGHT + UI.UI_PADDING * 10);
        battlefield[i + (dir ? o : -o)] = c;
        c.embark();
    }

    public final class AIHand extends Hand implements Renderable {

        CardStack cards;

        int x, y;

        Card selected = null;

        BufferedImage img;
        BufferedImage back;



        public AIHand() {
            cards = new CardStack();
            x = Globals.WIDTH / 2;
            y = Math.round((Globals.WIDTH * Card.CARD_SIZE_AS_PERCENT_OF_SCREEN * Card.WIDTH_TO_HEIGHT_RATIO + 5) / 8f);

            back = new BufferedImage((int) (Globals.WIDTH * Card.CARD_SIZE_AS_PERCENT_OF_SCREEN), Math.round((int) (Globals.WIDTH * Card.CARD_SIZE_AS_PERCENT_OF_SCREEN) * Card.WIDTH_TO_HEIGHT_RATIO) / 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = back.createGraphics();
            g2.drawImage(Resources.getImage("cardBack"), 0, -back.getHeight(), back.getWidth(), back.getHeight() * 2, null);


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

            //c.graphic().setOutlineColor(Card.AI_OUTLINE_COLOR);
            cards.add(c);
            update();
        }

        @Override
        public void remove(Card c) {
            cards.remove(c);
            update();
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
            if (cards.size() == 0) {
                img = new BufferedImage(10, (int) (Globals.WIDTH * Card.CARD_SIZE_AS_PERCENT_OF_SCREEN * Card.WIDTH_TO_HEIGHT_RATIO + 5), BufferedImage.TYPE_INT_ARGB);
                //img.createGraphics().fillRect(0, 0, 10, 10);

                return;
            }

            BufferedImage i = new BufferedImage((int) (Globals.WIDTH * Card.CARD_SIZE_AS_PERCENT_OF_SCREEN + 10) * cards.size(),
                                                (int) (Globals.WIDTH * Card.CARD_SIZE_AS_PERCENT_OF_SCREEN * Card.WIDTH_TO_HEIGHT_RATIO + 5) / 2,
                                                BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = i.createGraphics();
            //g.fillRect(0, 0, i.getWidth(), i.getHeight());

            int width = (int) (Globals.WIDTH * Card.CARD_SIZE_AS_PERCENT_OF_SCREEN);
            int height = Math.round(width * Card.WIDTH_TO_HEIGHT_RATIO);



            int cX = 0;
            for (Card c : cards) {
                cX += 5; // 5px padding before
                g.drawImage(back, cX, 0, null);
                g.setColor(Card.AI_OUTLINE_COLOR);
                g.drawRect(cX + 0, -1, width - 0, height / 2 - 0);
                g.setColor(new Color(Card.AI_OUTLINE_COLOR.getRed(), Card.AI_OUTLINE_COLOR.getGreen(), Card.AI_OUTLINE_COLOR.getBlue(), 125));
                g.drawRect(cX + 1, -1, width - 2, height / 2 - 1);
                g.setColor(new Color(Card.AI_OUTLINE_COLOR.getRed(), Card.AI_OUTLINE_COLOR.getGreen(), Card.AI_OUTLINE_COLOR.getBlue(), 75));
                g.drawRect(cX + 2, -1, width - 4, height / 2 - 2);
                g.setColor(new Color(Card.AI_OUTLINE_COLOR.getRed(), Card.AI_OUTLINE_COLOR.getGreen(), Card.AI_OUTLINE_COLOR.getBlue(), 25));
                g.drawRect(cX + 3, -1, width - 6, height / 2 - 3);
                cX += width;
                cX += 5; // 5px padding after
            }

            img = i;
        }

        @Override
        public void animate() {
            for (Card c : cards()) {
                c.graphic().animate();
            }
        }

        @Override
        public BufferedImage render() {
            return img;
        }

    }
}
