package util.string.outline;

import settings.Settings;
import util.string.Sequence;

/**
 * An {@linkplain Segment} which includes prefix and suffix sequences to put
 * before and after (respectively) its child.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class WrappingSegment extends Segment {
    private final Sequence openFolded,openExpanded,
                           closeFolded,closeExpanded;
    private Sequence indent = Settings.defaultIndent();
    private final int wrapperSize;
    private Segment child = null;
    
    /**
     * Creates a wrapping segment. <code>null</code> values yield empty sequences.
     * 
     * @param open  Sequence to append before the child.
     * @param close Sequence to append after the child.
     */
    public WrappingSegment(Sequence open,Sequence close) {
        if(open == null) open = Sequence.EMPTY;
        if(close == null) close = Sequence.EMPTY;
        final Sequence[] shared = Sequence.shared(open,close);
        openFolded = openExpanded = shared[0];
        closeFolded = closeExpanded = shared[1];
        wrapperSize = openFolded.length() + closeFolded.length();
    }
    /**
     * Creates a wrapping segment. <code>null</code> values yield empty sequences.
     * 
     * @param openFolded    Sequence to append before the child iff this segment is
     *                      folded.
     * @param openExpanded  Sequence to append before the child iff this segment is
     *                      expanded.
     * @param closeFolded   Sequence to append after the child iff this segment is
     *                      folded.
     * @param closeExpanded Sequence to append after the child iff this segment is
     *                      expanded.
     */
    public WrappingSegment(Sequence openFolded,Sequence openExpanded,
                           Sequence closeFolded,Sequence closeExpanded) {
        if(openFolded == null) openFolded = Sequence.EMPTY;
        if(openExpanded == null) openExpanded = Sequence.EMPTY;
        if(closeFolded == null) closeFolded = Sequence.EMPTY;
        if(closeExpanded == null) closeExpanded = Sequence.EMPTY;
        
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
    
    /**@return The indentation sequence currently being used by this segment.*/
    public Sequence indent() {return indent;}
    /**
     * @param indent The indentation sequence to use. <code>null</code> values yield
     *               the default indent.
     *               
     * @return <code>this</code>
     *               
     * @see Settings#defaultIndent()
     */
    public WrappingSegment indent(final Sequence indent) {
        this.indent = indent == null? Settings.defaultIndent() : indent;
        return this;
    }
    
    /**@return The child segment.*/
    public Segment child() {return child;}
    /**
     * @param child The child segment. <code>null</code> values yield an empty
     *              segment.
     * 
     * @return <code>this</code>
     */
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