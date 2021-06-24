package util.container;

/**
 * A lightweight singly-linked list implementation.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public abstract class Container<V> {
    protected Node<V> h = null,t = null;
    protected int size = 0;
    
    /**@return The number of elements in the container.*/
    public int size() {return size;}
    /**@return <code>true</code> iff the container has no elements.*/
    public boolean empty() {return h == null;}
    
    /**
     * Adds an element to the container.
     * 
     * @param v Element to add.
     * 
     * @return <code>this</code>.
     */
    @SuppressWarnings("unchecked")
    public <T extends Container<V>> T push(final V v) {
        if(++size == 1) h = t = new Node<>(v);
        else internalPush(v);
        return (T)this;
    }
    /**Internally adds an item.*/
    protected abstract void internalPush(final V v);
    
    /**
     * Removes an element from the front of the container.
     * 
     * @return The removed element.
     */
    public V pop() {
        if(size == 0) return null;
        final V v = h.v;
        if(--size == 0) h = t = null;
        else {
            final Node<V> n = h.n;
            h.n = null;
            h = n;
        }
        return v;
    }
    /**@return The element at the front of the container, or <code>null</code> if empty.*/
    public V top() {return h != null? h.v : null;}
    
    /**
     * Moves values from the argument to the tail of this container.
     * 
     * @return <code>this</code>.
     */
    public <T extends Container<V>> Container<V> merge(final T o) {
        if(o != null && o.size > 0) {
            if(size == 0) h = o.h;
            else t.n = o.h;
            t = o.t;
            o.h = o.t = null;
            size += o.size;
            o.size = 0;
        }
        return this;
    }
    /**
     * Copies values from the argument to the tail of this container.
     * 
     * @return <code>this</code>.
     */
    @SuppressWarnings("unchecked")
    public <C extends Container<V>> C cloneMerge(final C o) {
        return (C)(o != null? merge(o.clone()) : this);
    }
    /**
     * Removes all elements from the container.
     * 
     * @return <code>this</code>.
     */
    @SuppressWarnings("unchecked")
    public <T extends Container<V>> T clear() {
        if(size > 0) {
            // Better to clear the nodes manually to make de-allocation
            // more explicit for the GC.
            for(Node<V> n = h.n;n != null;n = (h = n).n) h.n = null;
            h = t = null;
            size = 0;
        }
        return (T)this;
    }
    
    /**@return A {@linkplain NodeIterator}*/
    public NodeIterator<V> iterator() {return new NodeIterator<>(h);}
    
    @Override public abstract Container<V> clone();
    /**Copies the elements of this container into the argument.*/
    <C extends Container<V>> C cloneWithInstance(final C c) {
        if(!empty()) {
            c.h = c.t = new Node<>(h.v);
            for(Node<V> n = h.n;n != null;n = n.n)
                c.t = c.t.n = new Node<>(n.v);
            c.size = size;
        }
        return c;
    }
}