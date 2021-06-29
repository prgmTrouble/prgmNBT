package util.container;

/**
 * A FIFO implementation of {@linkplain Container}.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class Queue<V> extends Container<V> {
    /**Adds the item to the back of the container.*/
    @Override protected void internalPush(final V v) {t = t.n = new Node<>(v);}
    @Override public Queue<V> clone() {return cloneWithInstance(new Queue<>());}
}