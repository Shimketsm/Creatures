package game;

import engine.Globals;
import java.util.ArrayList;
import java.util.List;
import util.Resources;

public abstract class Player {

    public static final int MAX_MANA = Integer.parseInt(Resources.getText("maxMana"));
    public static final int MAX_CARDS_ON_FIELD = Integer.parseInt(Resources.getText("maxCardsOnField"));

    protected int totalMana;
    protected int currMana;
    protected int currHealth;


    protected Card[] battlefield;
    protected int cardsInPlay;

    protected boolean playingNow;
    protected boolean dead;

    public Player(int startingMana, int startingHealth) {
        totalMana = currMana = startingMana;
        currHealth = startingHealth;
        battlefield = new Card[MAX_CARDS_ON_FIELD];
    }

    public final int getHealth() {
        return currHealth;
    }

    public final String getHealthDesc() {
        return "" + currHealth;
    }

    public final String getManaDesc() {
        return currMana + "/" + totalMana;
    }

    public final int getMana() {
        return currMana;
    }

    public final boolean increaseMana() {
        if (totalMana < MAX_MANA) {
            totalMana++;
            return true;
        } else
            return false;
    }

    public final void resetMana() {
        currMana = totalMana;
    }

    /**
     * Tests if the card can be played, and if so, subtracts the required mana.
     *
     * @param c
     *
     * @return
     */
    public final boolean canPlayCard(Card c) {
        if (c.getCost() > currMana || cardsInPlay >= MAX_CARDS_ON_FIELD) {
            return false;
        } else {
            currMana -= c.getCost();
            cardsInPlay++;
            return true;
        }
    }

    public final boolean hasTaunt() {
        boolean r = false;
        for (Card c : cardsInPlay()) {
            if (c.getTaunt()) r = true;
        }
        return r;
    }

    public final boolean doDamage() {

        currHealth -= 1;
        if (currHealth <= 0) {
            //die and gameover
            die();
        }
        return true;
    }

    public final boolean isDead() {
        return dead;
    }

    public void die() {
        dead = true;
        Globals.game.endGame(this);
    }

    public void turn() {
        for (Card c : cardsInPlay()) {
            c.setCanAttack(true);
            c.graphic().update();
        }
        playingNow = true;
    }

    public void endTurn() {
        playingNow = false;
    }

    public void update() {
        cardsInPlay = 0;
        for (int i = 0; i < battlefield.length; i++) {
            if (battlefield[i] != null) {
                if (battlefield[i].isDead())
                    battlefield[i] = null;
                else
                    cardsInPlay++;
            }
        }
    }

    public List<Card> cardsInPlay() {
        List<Card> l = new ArrayList<>(MAX_CARDS_ON_FIELD);

        for (Card c : battlefield) {
            if (c != null) l.add(c);
        }

        return l;
    }

    public abstract Hand
            hand();

    public abstract class Hand {

        public static final int MAX_HAND_SIZE = 7;

        public abstract List<Card> cards();

        public abstract void add(Card c);

        public abstract void remove(Card c);

    }

}
