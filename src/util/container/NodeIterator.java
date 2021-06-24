package util.container;

/**
 * An iterator which traverses nodes in a {@linkplain Container}.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public class NodeIterator<V> {
    protected Node<V> itr;
    protected NodeIterator(final Node<V> start) {itr = start;}
    
    /**@return <code>true</code> iff there are more subsequent elements.*/
    public boolean hasNext() {return itr != null;}
    /**
     * Advances the iterator's position.
     * 
     * @return The value at the current position before advancing.
     */
    public V next() {final V v = itr.v; itr = itr.n; return v;}
}