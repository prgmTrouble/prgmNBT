package util.string.outline;

import util.container.Queue;
import util.string.outline.segment.Segment;

public class Block {
    private final Queue<Segment> segments = new Queue<>();
    
    public Block() {}
    
    /**
     * Removes the specified number of segments from the front of this block.
     * 
     * @return A new block containing the removed segments.
     */
    public Block eject(int count) {
        if(count < 0 || segments.size() <= count) return this;
        final Block out = new Block();
        if(count != 0) {
            do out.segments.push(segments.pop());
            while(--count > 0);
        }
        return out;
    }
    
    
}
