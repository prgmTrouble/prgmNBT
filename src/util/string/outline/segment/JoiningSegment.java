package util.string.outline.segment;

import util.container.Queue;
import util.string.Sequence;

public class JoiningSegment extends Segment {
    private Queue<Segment> children = new Queue<>();
    private final Sequence separator;
    private int totalSize = 0;
    private int childLimit = Integer.MAX_VALUE;
    
    public JoiningSegment(final Sequence separator) {
        super();
        this.separator = separator;
    }
    
    public int childLimit() {return childLimit;}
    public JoiningSegment childLimit(final int childLimit) {
        this.childLimit = childLimit;
        return this;
    }
    
    public JoiningSegment push(final Segment child) {
        totalSize += child.size();
        if(children.push(child).size() > 1)
            totalSize += separator.length();
        return this;
    }
    
    @Override
    public int size() {
        return totalSize;
    }
    
    @Override
    protected boolean shouldExpand() {
        return super.shouldExpand() || children.size() > childLimit();
    }
    @Override
    public void onExpand() {
        
    }
    
}

































