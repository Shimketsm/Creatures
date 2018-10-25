package game;

import engine.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import util.*;

/**
 * Cards are immutable once created
 */
public class Card {

    static final Color HUMAN_OUTLINE_COLOR = new Color(20, 30, 255, 175);
    static final Color AI_OUTLINE_COLOR = new Color(255, 30, 15, 175);

    static final float WIDTH_TO_HEIGHT_RATIO = 14 / 9f;
    static final float CARD_SIZE_AS_PERCENT_OF_SCREEN = 0.1f;

    static final int CARD_WIDTH = (int) (Globals.WIDTH * CARD_SIZE_AS_PERCENT_OF_SCREEN);
    static final int CARD_HEIGHT = Math.round(CARD_WIDTH * WIDTH_TO_HEIGHT_RATIO);

    static final Color CARD_SELECTED = new Color(180, 45, 90, 100);
    static final Color CARD_HOVERED = new Color(135, 40, 105, 45);
    static final Color CARD_CANT_ATTACK = new Color(25, 25, 25, 100);

    static final Color CARD_BG = new Color(240, 250, 245);
    static final Color CARD_TEXT = new Color(10, 25, 20);
    static final Color CARD_ATTACK = new Color(15, 10, 15);
    static final Color CARD_MANA = new Color(10, 20, 185);
    static final Color CARD_HEALTH = new Color(185, 20, 10);



    private static final HashMap<String, Card> CARDS = new HashMap<>();
    //private static final HashMap<String, BufferedImage> cachedCardImages = new HashMap<>();

    private final int cost;

    //name that appears on the card
    private final String name;

    //if true, opponent must attack this card before face
    private final boolean taunt;

    //if true, opponent can attack immediatly
    private final boolean charge;

    //the attack of the card - to change this (buff, etc), must create NEW Card.
    private final int attack;

    //the health of the card
    private int health;


    private boolean dead;
    private boolean onField;

    private boolean canAttack;

    private Player owner;

    //cache the CardGraphic here - only one graphic per card
    private CardGraphic graphic = null;

    private Card(Card proto, Player owner) {
        this(proto.name, proto.attack, proto.health, proto.cost, proto.taunt, proto.charge, owner);
    }

    private Card(String name, int attack, int health, int cost, boolean taunt, boolean charge, Player owner) {
        this.name = name;
        this.attack = attack;
        this.health = health;
        this.cost = cost;
        this.taunt = taunt;
        this.charge = charge;
        this.owner = owner;
        this.onField = false;
        this.canAttack = false;
    }

    public void embark() {
        onField = true;
        setCanAttack(charge);

        Globals.game.add(this);
        graphic().deselect();
        graphic().dehover();
        graphic().update();
    }

    public boolean doDamage(int damage) {
        health -= damage;
        new PopUp(Resources.getImage("hit"), graphic().getX(), graphic().getY(), 75);
        if (health <= 0) {
            //do anything card related for death of card
            die();
            graphic.update();
            return true;
        } else {
            graphic.update();
            return false;
        }
    }

    public void die() {
        onField = false;
        dead = true;
        Globals.game.deselect(this);
        Globals.game.remove(this);
        //new PopUp(Resources.getImage("killed"), graphic.getX(), graphic.getY(), 60);

    }

    public boolean isDead() {
        return dead;
    }

    public Player owner() {
        return owner;
    }

    public CardGraphic graphic() {
        if (graphic == null) {
            graphic = new CardGraphic(0, 0, this);
        }
        return graphic;
    }

    public void setCanAttack(boolean atk) {
        canAttack = atk;
        Globals.game.deselect(this);
    }

    public boolean canAttack() {
        return canAttack;
    }

    public void didAttack() {
        canAttack = false;
        Globals.game.deselect(this);
        graphic().update();
    }

    public boolean getCharge() {
        return charge;
    }

    public int getAttack() {
        return attack;
    }

    public int getCost() {
        return cost;
    }

    public boolean getTaunt() {
        return taunt;
    }

    public int getHealth() {
        return health;
    }

    public boolean equalsCard(Card c) {
        return name.equals(c.name);
    }

    public boolean equalsCard(String name) {
        return name.equals(name);
    }

    @Override
    public String toString() {
        return name;
    }

    public static void init() {
        //read and add all cards

        String[] cards = Resources.getText("cardList").split(System.lineSeparator());

        for (String name : cards) {
            //System.out.println("processing " + "\'" + name + "\'");
            if (name.startsWith("#")) {
                continue;
            }
            String[] a = name.split(":");
            CARDS.put(a[0].toLowerCase().trim(), new Card(a[0].trim(), Integer.parseInt(a[3].trim()), Integer.parseInt(a[4].trim()), Integer.parseInt(a[5].trim()), "T".equalsIgnoreCase(a[1].trim()), "C".equalsIgnoreCase(a[2].trim()), null));
        }
    }

    public static Card random(Player owner) {
        return new Card((Card) CARDS.values().toArray()[(int) (Math.random() * CARDS.size())], owner);
    }

    public static Card getCard(String name, Player owner) {


        Card c = CARDS.get(name.toLowerCase());
        if (c == null) {
            throw new Error("No card named " + name);
        } else {
            //create a new card, do NOT return c, would allow access and modification to proto cards.


            return new Card(c, owner);
        }
    }

    public final class CardGraphic extends GameObject {



        public final Card card;

        private boolean showCost;
        private int drawnHealth;
        boolean drawnCanAttack;
        private boolean drawnSelected;
        private boolean selected;
        boolean drawnHovered;
        private boolean hovered;
        private Color outlineColor = AI_OUTLINE_COLOR;

        private final Rectangle bounds;

        public CardGraphic(float x, float y, Card c) {
            super(x, y, null);
            this.card = c;
            bounds = new Rectangle(getX() - CARD_WIDTH / 2, getY() - CARD_HEIGHT / 2, CARD_WIDTH, CARD_HEIGHT);
            //System.out.println("CardGraphic created! " + "It is owned by human: " + humanOwned);
            if (c.owner instanceof Human) {
                MouseInput.registerLeftHitListener(() -> {
                    if (onField && hovered && !selected && canAttack) {
                        //System.out.println("selected " + this);
                        Globals.game.select(card);
                        return;
                    } else if (onField && hovered && selected) {
                        Globals.game.deselect(card);
                    }

                    if (onField && selected) {
                        //no manual movement
                        //setLocation(MouseInput.getX(), MouseInput.getY());
                    }



                });
                MouseInput.registerRightHitListener(
                        () -> {
                    deselect();
                });
                Input.registerKeyListener(
                        KeyEvent.VK_SPACE,
                        () -> {
                    deselect();
                });
            } else {
                MouseInput.registerLeftHitListener(() -> {
                    //System.out.println(Globals.game.selected());
                    if (onField && hovered && Globals.game.selected() != null && owner != Globals.game.selected().owner) {
                        //do attack/defence

                        Globals.game.doEncounter(card, Globals.game.selected());
                    }
                });
            }



            if (c.owner instanceof Human)
                outlineColor = HUMAN_OUTLINE_COLOR;
            else
                outlineColor = AI_OUTLINE_COLOR;

            //may create bugs by getting cached image that has changed health
            //fixed possibility by using keys of strings with card name and health
            //could run into memory issues!!
            //setImage(cachedCardImages.getOrDefault(card, createImg()));
            //setImage(createImg());
            //valid only in JDK 1.8
            //setImage(cachedCardImages.getOrDefault(card.name + card.health, createImg()));
//            BufferedImage i = cachedCardImages.get(currDesc());
//            if (i == null) {
//                i = createImg();
//            }
            // all the above was premature optimization, totally unneeded.

            setImage(createImg());
        }

        public void setOutlineColor(Color c) {
            outlineColor = c;
        }

        //MUST CALL UPDATE WHEN HEALTH CHANGES
        public void update() {
            if (dead)
                Globals.game.remove(card);
            setImage(createImg());
        }

        public void showManaCost(boolean show) {
            showCost = show;
            update();
        }

        /**
         * DO NOT USE THIS UNLESS CARD IS IN HAND
         */
        public void select() {
            selected = true;
            update();
        }

        public boolean isSelected() {
            return selected;
        }

        /**
         * DO NOT USE THIS UNLESS CARD IS IN HAND
         */
        public void deselect() {
            selected = false;
            update();
        }

        public void hover() {
            hovered = true;
            update();

        }

        public void dehover() {
            hovered = false;
            update();
        }

        @Override
        public void animate() {
            if (onField) {
                bounds.setLocation(getX() - CARD_WIDTH / 2, getY() - CARD_HEIGHT / 2);
                boolean h = bounds.contains(MouseInput.getX(), MouseInput.getY());
                if (h && !drawnHovered) {
                    hover();
                } else if (!h && drawnHovered) {
                    dehover();
                }

                if (selected) {
                    //attack things if selected and trigger the left mouse when hovered over another card
                    //perhaps a global "selected" card to check against?


                }
            }
        }

        public final BufferedImage createImg() {

            drawnHealth = card.health;

            //get image, construct rest
            BufferedImage inf;
            try {
                inf = Resources.getImage(card.name);
            } catch (Throwable e) {
                inf = null;
            }


            //Cards should be about an 8th the width of the screen, whatever it is.
            BufferedImage fin = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = fin.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            try {
                g.drawImage(Resources.getImage("cardFront"), 0, 0, CARD_WIDTH, CARD_HEIGHT, null);
            } catch (Throwable e) {
                //image doesn't exist
                //so fill bg
                g.setColor(CARD_BG);
                g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
            }

            if (selected) {
                g.setColor(CARD_SELECTED);
                g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
//                try {
//                    //ensure selected is transparent
//                    g.drawImage(Resources.getImage("selected"), 0, 0, width, height, null);
//                } catch (Throwable e) {
//                    //image doesn't exist
//                    //so color slightly red
//                    g.setColor(CARD_SELECTED);
//                    g.fillRect(0, 0, width, height);
//                }
            } else if (hovered) {
                g.setColor(CARD_HOVERED);
                g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
            }
            g.setColor(CARD_TEXT);
            g.drawString(card.name, (CARD_WIDTH / 2f) - (g.getFontMetrics().stringWidth(card.name) / 2f), 15);
            int size = Math.round(CARD_WIDTH * 0.9f);
            g.drawImage(inf, Math.round((CARD_WIDTH / 2f) - (size / 2f)), 20, size, size, null);


            if (!canAttack) {
                g.setColor(CARD_CANT_ATTACK);
                g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
            }


            g.setColor(outlineColor);
            g.drawRect(0, 0, CARD_WIDTH - 1, CARD_HEIGHT - 1);
            g.setColor(new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), 125));
            g.drawRect(1, 1, CARD_WIDTH - 3, CARD_HEIGHT - 3);
            g.setColor(new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), 75));
            g.drawRect(2, 2, CARD_WIDTH - 5, CARD_HEIGHT - 5);
            g.setColor(new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), 25));
            g.drawRect(3, 3, CARD_WIDTH - 7, CARD_HEIGHT - 7);



            g.setFont(g.getFont().deriveFont(Font.BOLD, 24));

            g.setColor(CARD_ATTACK);
            g.drawString("" + card.attack, 2, CARD_HEIGHT - 2);

            g.setColor(CARD_HEALTH);
            g.drawString("" + card.health, CARD_WIDTH - g.getFontMetrics().stringWidth("" + card.health), CARD_HEIGHT - 2);


            if (taunt) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
                g.drawImage(Resources.getImage("taunt"), CARD_WIDTH / 2 - UI.ICON_SIZE / 2, (int) (Card.CARD_HEIGHT - UI.ICON_SIZE * 2.25f), null);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            }
            if (charge) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
                //move over one pixel if taunt is also drawn, just cosmetic
                g.drawImage(Resources.getImage("charge"), CARD_WIDTH / 2 - UI.ICON_SIZE / 2 + (taunt ? -1 : 0), (int) (Card.CARD_HEIGHT - UI.ICON_SIZE * 2.25f), null);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            }

            if (showCost) {
                g.setColor(CARD_MANA);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
                g.drawString("" + card.cost, CARD_WIDTH / 2f - g.getFontMetrics().stringWidth("" + card.cost) / 2f, CARD_HEIGHT - g.getFontMetrics().getHeight() / 1.5f);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            }



            g.dispose();

            drawnHealth = card.health;
            drawnSelected = selected;
            drawnHovered = hovered;
            drawnCanAttack = card.canAttack;

            //cachedCardImages.put(currDesc(), fin);
            return fin;
        }

    }

}
