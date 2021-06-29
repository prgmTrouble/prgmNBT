package util.string;

import util.container.Container;
import util.container.Stack;

/**
 * A {@linkplain Stack} of {@linkplain Sequence}s which functions as a builder.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class SequenceStack extends Stack<Sequence> implements Builder {
    private int totalSize = 0;
    
    @SuppressWarnings("unchecked") @Override
    public SequenceStack push(final Sequence v) {
        if(v != null) totalSize += v.length();
        return super.push(v);
    }
    @SuppressWarnings("unchecked") @Override
    public SequenceStack push(final Sequence...v) {
        if(v != null) for(final Sequence s : v) if(s != null) totalSize += s.length();
        return super.push(v);
    }
    @Override
    public Sequence pop() {
        final Sequence out = super.pop();
        if(out != null) totalSize -= out.length();
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
        else if(o != null) for(final Sequence s : o) if(s != null) totalSize += s.length();
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