package util.string;

import util.container.Container;
import util.container.NodeIterator;
import util.container.Stack;

public class SequenceStack extends Stack<Sequence> implements Builder {
private int totalSize = 0;
    
    @SuppressWarnings("unchecked") @Override
    public SequenceStack push(final Sequence v) {
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
    public SequenceStack clear() {
        totalSize = 0;
        return super.clear();
    }
    
    @Override
    public <T extends Container<Sequence>> SequenceStack merge(final T o) {
        if(o instanceof SequenceStack)
            totalSize += ((SequenceStack)o).totalSize;
        else
            for(
                final NodeIterator<Sequence> i = o.iterator();
                i.hasNext();
                totalSize += i.next().length()
            );
        return (SequenceStack)super.merge(o);
    }
    
    @Override
    public SequenceStack clone() {
        final SequenceStack cloned = (SequenceStack)super.clone();
        cloned.totalSize = totalSize;
        return cloned;
    }
    
    @Override
    public Sequence concat() {
        if(empty()) return Sequence.EMPTY;
        final char[] buf = new char[totalSize];
        final NodeIterator<Sequence> i = iterator();
        int cursor = totalSize;
        do {
            final Sequence s = i.next();
            s.copyInto(buf,cursor -= s.length());
        } while(i.hasNext());
        return new Sequence(buf);
    }
}