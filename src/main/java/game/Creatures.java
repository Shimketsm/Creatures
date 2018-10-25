package game;

import engine.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import util.*;

public class Creatures extends Game {

    public static final int END_GAME_SCREEN_FRAMES = 500;
    public static final int STARTING_CARDS = Integer.parseInt(Resources.getText("startingCards"));

    Turn currentTurn;
    Thread turnThread;
    Human human;
    AI ai;
    UI ui;

    Card selectedCard;

    @Override
    public void start() {
        DrawArea.setBG(Resources.getImage("bg"));
        Card.init();

        human = new Human();
        ai = new AI();

//        MouseInput.registerRightHitListener(() -> {
//            human.hand().add(Card.random(true));
//            ai.hand().add(Card.random(false));
//        });

        ui = new UI(human, ai);

        addUI(human.hand());
        addUI(ai.hand());
        addUI(ui);


        for (int i = 0; i < STARTING_CARDS; i++) {
            human.hand().add(Card.random(human));
            ai.hand().add(Card.random(ai));
        }

        Button close = new Button("", Resources.getImage("close"), Resources.getImage("closeHover"), Globals.WIDTH - Resources.getImage("close").getWidth(), 0);
        close.setListener(() -> changeStatus(Status.QUIT));
        addUI(close);


        //lambdas are FREAKIN' AWESOME
        //Quit listener
        Input.registerKeyListener(KeyEvent.VK_ESCAPE, () -> changeStatus(Status.QUIT));

        //Player update listener
        MouseInput.registerLeftHitListener(() -> {
            human.update();
            ai.update();
        });

        //PopUp p = new PopUp("This is a note!", Color.red, Globals.WIDTH / 2, Globals.HEIGHT / 2, 500);

        newTurn();
    }

    public void doEncounter(Card defender, Card attacker) {
        if (attacker.canAttack()) {
            attacker.doDamage(defender.getAttack());
            defender.doDamage(attacker.getAttack());
            attacker.didAttack();
        }
    }

    public void select(Card c) {
        if (selectedCard != null)
            deselect(selectedCard);
        if (!c.graphic().isSelected())
            c.graphic().select();
        selectedCard = c;
    }

    public Card selected() {
        return selectedCard;
    }

    public void deselect(Card... cards) {
        if (cards.length == 0) {
            if (selectedCard != null && selectedCard.graphic().isSelected()) {
                selectedCard.graphic().deselect();
            }
            selectedCard = null;
            return;
        }
        boolean n = false;
        for (Card c : cards) {
            if (c == selectedCard)
                n = true;
            if (selectedCard != null) selectedCard.graphic().deselect();
        }
        if (n)
            selectedCard = null;
    }

    public void popup(String text, int frames) {
        new PopUp(text, new Color(255, 190, 75), Globals.WIDTH / 2, Globals.HEIGHT / 2, frames);
    }

    public void endGame(Player whoDied) {
        PopUp text;
        //changeStatus(Status.PAUSED);

        if (whoDied == human) {
            //lost
            System.out.println("Player died!");
            text = new PopUp(Resources.getImage("lost"), Globals.WIDTH / 2, Globals.HEIGHT / 2, END_GAME_SCREEN_FRAMES);
        } else {
            //won
            System.out.println("AI died!");
            text = new PopUp(Resources.getImage("won"), Globals.WIDTH / 2, Globals.HEIGHT / 2, END_GAME_SCREEN_FRAMES);
        }

        new Thread(() -> {
            while (!text.finished()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
            changeStatus(Status.QUIT);

        }).start();

    }

    //overloads of super methods

    public void add(Card c) {
        super.add(c.graphic());
    }

    public void remove(Card c) {
        super.remove(c.graphic());
    }

    public void newTurn() {
        currentTurn = new Turn(human, ai);
        turnThread = new Thread(currentTurn);
        turnThread.start();
    }

    @Override
    public void tick() {
        //called every frame
        if (currentTurn.done) {
            newTurn();
        }
    }

    class Turn implements Runnable {

        Player hu, ai;
        boolean done;

        public Turn(Human hu, AI ai) {
            this.hu = hu;
            this.ai = ai;
            done = false;
        }

        @Override
        public void run() {
            Globals.game.deselect();

            hu.increaseMana();
            hu.resetMana();
            hu.hand().add(Card.random(hu));
            hu.turn();


            ai.increaseMana();
            ai.resetMana();
            ai.hand().add(Card.random(ai));
            ai.turn();

            done = true;
        }

    }

}
