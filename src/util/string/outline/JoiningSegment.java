package util.string.outline;

import settings.Settings;
import util.container.Queue;
import util.string.Joiner;
import util.string.Sequence;
import util.string.SequenceQueue;

public class JoiningSegment extends Segment {
    private Queue<Segment> children = new Queue<>();
    private final Sequence separator;
    private int totalSize = 0;
    private int childLimit = Settings.defaultFoldedChildLimit();
    
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
    
    @Override public int size() {return totalSize;}
    
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
            // The data for each block.
            final Sequence[][] data = new Sequence[children.size()][];
            {
                int child = -1;
                for(final Segment s : children)
                    data[++child] = s.secondPass();
            }
            final Queue<Sequence> lines = new Queue<>();
            {
                SequenceQueue line = new SequenceQueue();
                {
                    boolean notFirst = false,previousExpanded = false;
                    final int sepSize = separator.length();
                    int childCount = 0;
                    for(final Sequence[] block : data) {
                        // Push separators between blocks.
                        if(notFirst) line.push(separator);
                        else notFirst = true;
                        // Force line breaks after expanded children.
                        if(previousExpanded) {
                            lines.push(line.concat());
                            line = new SequenceQueue();
                            previousExpanded = false;
                        }
                        // Detect line break position.
                        if(
                            block.length != 1 || // Expanded child.
                            childCount == childLimit || // Child limit reached.
                            (
                                line.currentSize() + sepSize + block[0].length()
                            ) >= charLimit // Character limit reached.
                        ) {
                            // Force a line break if the previousExpanded check
                            // failed.
                            if(!line.empty()) {
                                lines.push(line.concat());
                                line = new SequenceQueue();
                            }
                            // Push all lines excepting the last.
                            if(previousExpanded = block.length != 1) {
                                for(int i = 0;i < block.length - 1;++i)
                                    lines.push(block[i]);
                                // Reset child counter.
                                childCount = 0;
                            } else childCount = 1;
                        } else ++childCount;
                        // Push the remaining line.
                        line.push(block[block.length - 1]);
                    }
                }
                lines.push(line.concat());
            }
            return Sequence.shared(lines.toArray(new Sequence[lines.size()]));
        }
        final Joiner line = new Joiner(separator);
        for(final Segment s : children)
            // Since expanded children force the parent to be expanded, there must
            // be exactly one element in the second pass array.
            line.push(s.secondPass()[0]);
        return new Sequence[] {line.concat()};
    }
}

































