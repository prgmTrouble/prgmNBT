package util.container;

/**
 * A LIFO implementation of {@linkplain Container}.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public class Stack<V> extends Container<V> {
    /**Adds the item to the front of the list.*/
    @Override protected void internalPush(final V v) {h = new Node<>(v,h);}
    @Override public Stack<V> clone() {return cloneWithInstance(new Stack<>());}
}