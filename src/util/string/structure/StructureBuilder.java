package util.string.structure;

import settings.Settings;
import util.container.Queue;
import util.string.Sequence;
import util.string.SequenceQueue;
import util.string.structure.position.Adjacency;
import util.string.structure.position.FoldingStatus;
import util.string.structure.position.RuleSet;
import util.string.structure.position.Token;

public class StructureBuilder implements Structure {
    private final Queue<Structure> structures = new Queue<>();
    /**The number of characters in the structure, excluding formatting characters.*/
    private int size = 0;
    private int nStruct = 0;
    private final int maxSize,maxStruct;
    private boolean expanded = false;
    private final Sequence open,close,sep,indent;
    public final RuleSet rules = new RuleSet();
    
    private void expand() {
        if(!expanded)
            expanded = (maxSize != INFINITE_LIMIT && size > maxSize) ||
                       (maxStruct != INFINITE_LIMIT && nStruct > maxStruct);
    }
    
    /**Specifies an infinite charLimit.*/
    public static final int INFINITE_LIMIT = -2;
    /**Disables folding.*/
    public static final int FOLD_NONE = -1;
    
    /**
     * Creates a structure builder with limited folding.
     * 
     * @param structureLimit The maximum number of structures to append before the
     *                       builder disables folding.
     * @param sizeLimit      The maximum number of characters to append before the
     *                       builder disables folding.
     * @param open           Sequence to put before the structure.
     * @param close          Sequence to put after the structure.
     * @param sep            Sequence to put between sub-elements.
     * @param indent         Sequence to use as indentation.
     *                       
     * @see #INFINITE_LIMIT
     * @see #StructureBuilder(FoldingPolicy,Sequence,Sequence,Sequence)
     */
    public StructureBuilder(final int structureLimit,
                            final int sizeLimit,
                            final Sequence open,
                            final Sequence close,
                            final Sequence sep,
                            final Sequence indent) {
        maxStruct = structureLimit;
        maxSize = sizeLimit;
        expanded = maxSize == FOLD_NONE || maxStruct == FOLD_NONE;
        expand();
        this.open = open;
        this.close = close;
        this.sep = sep;
        this.indent = indent;
    }
    /**
     * @see #ALL
     * @see #NONE
     */
    public static enum FoldingPolicy {
        /**Specifies that the entire structure should be folded.*/
        ALL,
        /**Specefies that none of the structure should be folded.*/
        NONE;
        private int toLimit() {return ordinal() - 2;}
    }
    /**
     * Creates a structure builder using the specified folding policy.
     * 
     * @see FoldingPolicy
     * @see #StructureBuilder(Sequence,Sequence,Sequence)
     * @see #StructureBuilder(int,int,Sequence,Sequence,Sequence)
     */
    public StructureBuilder(final FoldingPolicy foldingPolicy,
                            final Sequence open,
                            final Sequence close,
                            final Sequence sep,
                            final Sequence indent) {
        maxStruct = maxSize = (
            foldingPolicy == null? Settings.defaultFoldingPolicy()
                                 : foldingPolicy
        ).toLimit();
        this.open = open;
        this.close = close;
        this.sep = sep;
        this.indent = indent;
    }
    /**
     * Creates a structure builder using the default folding policy and indentation.
     * 
     * @see Settings#defaultFoldingPolicy()
     * @see #StructureBuilder(FoldingPolicy,Sequence,Sequence,Sequence)
     */
    public StructureBuilder(final Sequence open,
                            final Sequence close,
                            final Sequence sep) {
        this(Settings.defaultFoldingPolicy(),open,close,sep,Settings.defaultIndent());
    }
    
    private final SequenceQueue inline = new SequenceQueue(),
                                expand = new SequenceQueue();
    
    @Override public Sequence inline() {return inline.concat();}
    @Override
    public Sequence preview() {
        col = indents = 0;
        
        final SequenceQueue q = pushWrapped(new SequenceQueue(),open,fs,Token.open);
        pushAware(q);
        while(!structures.empty()) pushAware(pushWrapped(q,sep,fs,Token.separator));
        final Sequence out = pushWrapped(q,close,fs,Token.close).concat();
        col = indents = 0;
        return out;
    }
    
    /**Pushes the structure to the builder.*/
    @SuppressWarnings("unchecked")
    public <B extends StructureBuilder> B push(final Structure s) {
        structures.push(s);
        inline.push(s.inline());
        
        size += s.size();
        ++nStruct;
        if(!(expanded || (expanded = s.isExpanded()))) expand();
        return (B)this;
    }
    
    transient int col,indents;
    private transient Sequence cachedInline;
    private transient boolean cacheDirty = true;
    
    private Sequence popBase(final int indentCount,
                             final FoldingStatus fs) {
        col = (indents = indentCount) * indent.length();
        final SequenceQueue q = pushWrapped(new SequenceQueue(),open,fs,Token.open);
        pushAware(q);
        while(!structures.empty()) pushAware(pushWrapped(q,sep,fs,Token.separator));
        final Sequence out = pushWrapped(q,close,fs,Token.close).concat();
        col = indents = 0;
        return out;
    }
    
    @Override public Sequence pop() {return pop(0);}
    private Sequence getAndApply(final Adjacency adjacency,
                                 final FoldingStatus foldingStatus,
                                 final Token token) {
        return rules.getRule(adjacency,foldingStatus,token).apply(this);
    }
    private void pushAware(final SequenceQueue q) {
        final Sequence push;
        {
            final Structure s = structures.pop();
            push = s instanceof StructureBuilder? ((StructureBuilder)s).pop(indents)
                                                : s.pop();
        }
        col += push.length();
        q.push(push);
    }
    private SequenceQueue pushWrapped(final SequenceQueue q,
                                      final Sequence value,
                                      final FoldingStatus fs,
                                      final Token t) {
        return q.push(getAndApply(Adjacency.before,fs,t))
                .push(value)
                .push(getAndApply(Adjacency.after,fs,t));
    }
    private Sequence pop(final int indentCount) {
        col = (indents = indentCount) * indent.length();
        final FoldingStatus fs = nStruct != 0? isExpanded()? FoldingStatus.expanded
                                                           : FoldingStatus.inline
                                             : FoldingStatus.empty;
        
        final SequenceQueue q = pushWrapped(new SequenceQueue(),open,fs,Token.open);
        pushAware(q);
        while(!structures.empty()) pushAware(pushWrapped(q,sep,fs,Token.separator));
        final Sequence out = pushWrapped(q,close,fs,Token.close).concat();
        col = indents = 0;
        return out;
    }
    
    @Override
    public int size() {
        
        return size;
    }
    @Override
    public boolean isExpanded() {
        if(expanded) return true;
        
        return expanded || size > Settings.maxFoldedLength();
    }
    
    public int maxSize() {return maxSize;}
    public int maxStruct() {return maxStruct;}
    public int nStruct() {return nStruct;}
}




















