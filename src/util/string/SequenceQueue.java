package util.string;

import util.container.Container;
import util.container.NodeIterator;
import util.container.Queue;

public class SequenceQueue extends Queue<Sequence> implements Builder {
    private int totalSize = 0;
    
    @SuppressWarnings("unchecked") @Override
    public SequenceQueue push(final Sequence v) {
        totalSize += v.length();
        return super.push(v);
    }
    @Override
    public Sequence pop() {
        final Sequence out = super.pop();
        totalSize -= out.length();
        return super.pop();
    }
    
    /**@return The size of the sequences stored in the queue.*/
    public int currentSize() {return totalSize;}
    
    @SuppressWarnings("unchecked") @Override
    public SequenceQueue clear() {
        totalSize = 0;
        return super.clear();
    }
    
    @Override
    public <T extends Container<Sequence>> SequenceQueue merge(final T o) {
        if(o instanceof SequenceQueue)
            totalSize += ((SequenceQueue)o).totalSize;
        else
            for(
                final NodeIterator<Sequence> i = o.iterator();
                i.hasNext();
                totalSize += i.next().length()
            );
        return (SequenceQueue)super.merge(o);
    }
    
    @Override
    public SequenceQueue clone() {
        final SequenceQueue cloned = (SequenceQueue)super.clone();
        cloned.totalSize = totalSize;
        return cloned;
    }
    
    @Override
    public Sequence concat() {
        if(empty()) return Sequence.EMPTY;
        final char[] buf = new char[totalSize];
        final NodeIterator<Sequence> i = iterator();
        int cursor = 0;
        do cursor = i.next().copyInto(buf,cursor);
        while(i.hasNext());
        return new Sequence(buf);
    }
}