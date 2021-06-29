package util.string;

import util.container.Container;
import util.container.Queue;

/**
 * A {@linkplain Queue} of {@linkplain Sequence}s which functions as a builder.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class SequenceQueue extends Queue<Sequence> implements Builder {
    private int totalSize = 0;
    
    @SuppressWarnings("unchecked") @Override
    public SequenceQueue push(final Sequence v) {
        if(v != null) totalSize += v.length();
        return super.push(v);
    }
    @SuppressWarnings("unchecked") @Override
    public SequenceQueue push(final Sequence...v) {
        if(v != null)
            for(final Sequence s : v)
                if(s != null)
                    totalSize += s.length();
        return super.push(v);
    }
    @Override
    public Sequence pop() {
        final Sequence out = super.pop();
        if(out != null) totalSize -= out.length();
        return out;
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
        if(o instanceof SequenceQueue) totalSize += ((SequenceQueue)o).totalSize;
        else if(o != null) for(final Sequence s : this) if(s != null) totalSize += s.length();
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
        int cursor = 0;
        for(final Sequence s : this) if(s != null) cursor = s.copyInto(buf,cursor);
        return new Sequence(buf);
    }
}