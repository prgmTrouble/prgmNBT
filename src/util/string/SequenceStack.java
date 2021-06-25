package util.string;

import util.container.Container;
import util.container.Stack;

public class SequenceStack extends Stack<Sequence> implements Builder {
private int totalSize = 0;
    
    @SuppressWarnings("unchecked") @Override
    public SequenceStack push(final Sequence v) {
        totalSize += v.length();
        return super.push(v);
    }
    @SuppressWarnings("unchecked") @Override
    public SequenceStack push(final Sequence...v) {
        if(v != null) for(final Sequence s : v) totalSize += s.length();
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
        if(o instanceof SequenceStack) totalSize += ((SequenceStack)o).totalSize;
        else for(final Sequence s : o) totalSize += s.length();
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
        int cursor = totalSize;
        for(final Sequence s : this) s.copyInto(buf,cursor -= s.length());
        return new Sequence(buf);
    }
}