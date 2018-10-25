package game;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class CardStack implements Iterable<Card> {

    //use as a stack, so 0 is top.
    CopyOnWriteArrayList<Card> cards;

    public CardStack() {
        cards = new CopyOnWriteArrayList<>();
    }

    public CardStack(Collection<Card> cards) {
        this();
        this.cards.addAll(cards);
    }

    /**
     *
     * @return the top card
     */
    public Card top() {
        return cards.get(0);
    }

    /**
     *
     * @param index
     *
     * @return the card at index
     */
    public Card get(int index) {

        return cards.get(index);
    }

    /**
     *
     * @param c the card to push onto the top
     */
    public void addToTop(Card c) {
        insert(c, 0);
    }

    /**
     *
     * @param c the card to add to the end (bottom)
     */
    public void add(Card c) {
        //add to end
        cards.add(c);
    }

    /**
     *
     * @param c     the card to insert
     * @param index the index at which to insert the card
     */
    public void insert(Card c, int index) {
        //insert at index
        cards.add(index, c);
    }

    /**
     *
     * @param c the card to remove
     *
     * @return if the removal was successful (IE the card was in the list)
     */
    public boolean remove(Card c) {
        return cards.remove(c);
    }

    /**
     *
     * @param index the index of the card to remove
     *
     * @return if the removal was successful (IE the card was in the list)
     */
    public boolean remove(int index) {

        return cards.remove(index) != null;
    }

    public boolean contains(Card c) {
        return cards.contains(c);
    }

    public int size() {
        return cards.size();
    }

    @Override
    public Iterator<Card> iterator() {
        return cards.iterator();
    }


}
