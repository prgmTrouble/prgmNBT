package util.string.outline;

import settings.Settings;
import util.string.Sequence;

public class WrappingSegment extends Segment {
    private final Sequence openFolded,openExpanded,
                           closeFolded,closeExpanded;
    private Sequence indent = Settings.defaultIndent();
    private final int wrapperSize;
    private Segment child = null;
    
    public WrappingSegment(final Sequence open,final Sequence close) {
        if(open == null || close == null || open.isEmpty() || close.isEmpty())
            throw new IllegalArgumentException("Empty wrapper.");
        final Sequence[] shared = Sequence.shared(open,close);
        openFolded = openExpanded = shared[0];
        closeFolded = closeExpanded = shared[1];
        wrapperSize = openFolded.length() + closeFolded.length();
    }
    public WrappingSegment(final Sequence openFolded,final Sequence openExpanded,
                           final Sequence closeFolded,final Sequence closeExpanded) {
        if(
            openFolded == null || openExpanded == null ||
            closeFolded == null || closeExpanded == null ||
            openFolded.isEmpty() || openExpanded.isEmpty() ||
            closeFolded.isEmpty() || closeExpanded.isEmpty()
        ) throw new IllegalArgumentException("Cannot have empty wrappers.");
        final Sequence[] shared = Sequence.shared(
            openFolded,openExpanded,
            closeFolded,closeExpanded
        );
        this.openFolded = shared[0];
        this.openExpanded = shared[1];
        this.closeFolded = shared[2];
        this.closeExpanded = shared[3];
        wrapperSize = openFolded.length() + closeFolded.length();
    }
    
    public Sequence indent() {return indent;}
    public WrappingSegment indent(final Sequence indent) {
        this.indent = indent == null? Settings.defaultIndent() : indent;
        return this;
    }
    
    public Segment child() {return child;}
    public WrappingSegment child(final Segment child) {this.child = child; return this;}
    
    @Override public int size() {return wrapperSize + (child == null? 0 : child.size());}
    
    private boolean expand = false;
    @Override
    protected boolean firstPass() {
        return expand = child != null && child.firstPass() || shouldExpand();
    }
    
    @Override
    protected Sequence[] secondPass() {
        if(child == null) // null child implies empty.
            return expand? new Sequence[] {openExpanded,closeExpanded}
                         : new Sequence[] {Sequence.merge(openFolded,closeFolded)};
        if(expand) {
            final Sequence[] out;
            {
                // Copy second pass data from the child. 
                final Sequence[] data = child.secondPass();
                out = new Sequence[data.length + 2];
                if(indent.isEmpty())
                    System.arraycopy(data,0,out,1,data.length);
                else for(int i = 0;i < data.length;++i)
                    out[i + 1] = Sequence.merge(indent,data[i]);
            }
            // Add the wrappers.
            out[0] = openExpanded;
            out[out.length - 1] = closeExpanded;
            // Condense and return.
            return Sequence.shared(out);
        }
        return new Sequence[] {Sequence.merge(
            openFolded,
            // Since expanded children force the parent to be expanded, there must
            // be exactly one element in the second pass array.
            child.secondPass()[0],
            closeFolded
        )};
    }
}