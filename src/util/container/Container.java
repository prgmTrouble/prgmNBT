package util.container;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    public V[] toArray() {
        final V[] arr = infer();
        unsafeToArray(arr);
        return arr;
    }
    public V[] toArray(V[] arr) {
        arr = ensure(arr);
        unsafeToArray(arr);
        return arr;
    }
    private void unsafeToArray(final V[] arr) {
        int i = 0;
        for(Node<V> n = h;n != null;++i,n = n.n) arr[i] = n.v;
    }
    private void unsafeToArray(final V[] arr,Node<V> cursor,int start,final int end) {
        final int _s = start;
        try {
        do {
            arr[start] = cursor.v;
            cursor = cursor.n;
        } while(++start != end);
        } catch(NullPointerException e) {
            throw new ArrayIndexOutOfBoundsException("idx: "+start+",start: "+_s+",end: "+end);
        }
    }
    private void chkChunk(final int chunkSize) {
        if(chunkSize < 1)
            throw new IllegalArgumentException(
                "Chunk size '%d%' is less than one."
                .formatted(chunkSize)
            );
    }
    public V[] parallelToArray(final int chunkSize) {
        chkChunk(chunkSize);
        final V[] arr = infer();
        unsafePtA(chunkSize,arr);
        return arr;
    }
    public V[] parallelToArray(final int chunkSize,V[] out) {
        chkChunk(chunkSize);
        out = ensure(out);
        unsafePtA(chunkSize,out);
        return out;
    }
    private void unsafePtA(final int chunk,final V[] out) {
        final ExecutorService taskPool;
        {
            final ExecutorService jumpThread;
            {
                Future<Node<V>> jump = new Future<>() {
                    @Override public boolean cancel(final boolean b) {return false;}
                    @Override public boolean isCancelled() {return false;}
                    @Override public boolean isDone() {return true;}
                    @Override
                    public Node<V> get() throws InterruptedException,ExecutionException {return h;}
                    @Override
                    public Node<V> get(final long l,final TimeUnit t) throws InterruptedException,
                    ExecutionException,
                    TimeoutException {return h;}
                };
                jumpThread = Executors.newSingleThreadExecutor();
                final int _size = size; // Prevent concurrency issues.
                final int fullChunks = _size / chunk;
                taskPool = Executors.newFixedThreadPool(
                    Math.min(
                        Runtime.getRuntime().availableProcessors(),
                        Math.max(
                            1,
                            fullChunks
                        )
                    )
                );
                try {
                    for(int i = 0;i < fullChunks;++i) {
                        final Node<V> current = jump.get();
                        jump = jumpThread.submit(() -> {
                            Node<V> cursor = current;
                            for(int j = 0;j < chunk;++j) cursor = cursor.n;
                            return cursor;
                        });
                        final int offset = i * chunk;
                        taskPool.execute(() -> unsafeToArray(out,current,offset,offset + chunk));
                    }
                    jumpThread.shutdown();
                    final int mod = _size % chunk;
                    if(mod != 0) {
                        final Node<V> current = jump.get();
                        taskPool.execute(() -> unsafeToArray(out,current,_size - (_size % chunk),_size));
                    }
                } catch(ExecutionException|InterruptedException e) {throw new RuntimeException(e);}
                taskPool.shutdown();
            }
            try {jumpThread.awaitTermination(Long.MAX_VALUE,TimeUnit.DAYS);}
            catch(final InterruptedException e) {throw new RuntimeException(e);}
        }
        try {taskPool.awaitTermination(Long.MAX_VALUE,TimeUnit.DAYS);}
        catch(final InterruptedException e) {throw new RuntimeException(e);}
    }
}

























