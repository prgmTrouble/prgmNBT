package util.string.outline.segment;

import util.container.Queue;
import util.string.Sequence;

public class JoiningSegment extends Segment {
    private Queue<Segment> children = new Queue<>();
    private final Sequence separator;
    private int totalSize = 0;
    private int childLimit = MAX_LIMIT; //TODO put default in settings
    
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
    
    private boolean expand = false;
    @Override
    protected boolean firstPass() {
        for(final Segment s : children) expand |= s.firstPass();
        if(!expand) expand = shouldExpand();
        return expand;
    }
    @Override
    protected Sequence[] secondPass() {
        if(expand) {
            // A sequence array holding the data for each block.
            final Sequence[][] data = new Sequence[children.size()][];
            {
                // Initially set each segment to its own block.
                int child = -1;
                for(final Segment s : children)
                    data[++child] = s.secondPass();
            }
            //TODO
            // break on:
            //     expanded child
            //     child limit reached
            //     character limit reached
            
            // Don't bother inlining if the child limit allows 1 element per block.
            if(childLimit != 1) {
                // Variables to keep track of the current line.
                int charCount = 0,childCount = 0;
                
            }
        }
        
        return null;
    }
}

































