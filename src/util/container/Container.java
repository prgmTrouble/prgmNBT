package util.container;

import java.lang.reflect.Array;

/**
 * A lightweight singly-linked list implementation.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public abstract class Container<V> implements Iterable<V> {
    protected Node<V> h = null,t = null;
    protected int size = 0;
    
    /**@return The number of elements in the container.*/
    public int size() {return size;}
    /**@return <code>true</code> iff the container has no elements.*/
    public boolean empty() {return h == null;}
    
    private void pushBase(final V v) {
        if(++size == 1) h = t = new Node<>(v);
        else internalPush(v);
    }
    /**
     * Adds an element to the container.
     * 
     * @param v Element to add.
     * 
     * @return <code>this</code>.
     */
    @SuppressWarnings("unchecked")
    public <C extends Container<V>> C push(final V v) {
        pushBase(v);
        return (C)this;
    }
    /**Internally adds an item.*/
    protected abstract void internalPush(final V v);
    /**
     * Pushes all elements to the container.
     * 
     * @param v Elements to add.
     * 
     * @return <code>this</code>.
     */
    @SuppressWarnings("unchecked")
    public <C extends Container<V>> C push(final V...v) {
        if(v != null && v.length != 0) {
            pushBase(v[0]);
            for(int i = 0;++i < v.length;internalPush(v[i]));
        }
        return (C)this;
    }
    
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
    @Override
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
    
    @SuppressWarnings("unchecked")
    private V[] createArr(final Class<?> cls) {return (V[])Array.newInstance(cls,size);}
    private V[] infer() {
        if(empty())
            throw new NullPointerException(
                "Failed to create a generic array: No template array provided " +
                "and this container is empty."
            );
        return createArr(h.v.getClass());
    }
    private V[] ensure(final V[] arr) {
        return arr != null? arr.length == size? arr
                                              : createArr(arr.getClass().componentType())
                          : infer();
    }
    /**
     * @return An array containing the contents of this container.
     * 
     * @throws NullPointerException This container is empty.
     * 
     * @see #toArray(V[])
     */
    public V[] toArray() throws NullPointerException {
        final V[] arr = infer();
        unsafeToArray(arr);
        return arr;
    }
    /**
     * @param arr A template array.
     * 
     * @return An array containing the contents of this container.
     * 
     * @throws NullPointerException This container is empty and the provided
     *                              template array is <code>null</code>.
     *                              
     * @see #toArray()
     */
    public V[] toArray(V[] arr) throws NullPointerException {
        arr = ensure(arr);
        unsafeToArray(arr);
        return arr;
    }
    private void unsafeToArray(final V[] arr) {
        int i = 0;
        for(Node<V> n = h;n != null;++i,n = n.n) arr[i] = n.v;
    }
}