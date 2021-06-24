package util.string.outline;

import util.container.Stack;
import util.string.Sequence;
import util.string.SequenceStack;
import util.string.outline.position.FoldingStatus;

public class Outline {
    public static enum FoldingPolicy {
        FOLD,EXPAND,MIXED;
        
        public static final FoldingPolicy DEFAULT = MIXED;
    }
    
    private final OutlineToken open,close,sep;
    private final Stack<OutlineElement> elements = new Stack<>();
    private final FoldingPolicy foldingPolicy;
    
    public Outline(final FoldingPolicy foldingPolicy,
                   final Sequence open,
                   final Sequence close,
                   final Sequence sep) {
        this.foldingPolicy = foldingPolicy == null? FoldingPolicy.DEFAULT
                                                  : foldingPolicy;
        this.open = new OutlineToken(open,this);
        this.close = new OutlineToken(close,this);
        this.sep = new OutlineToken(sep,this);
    }
    public Outline(final Sequence open,
                   final Sequence close,
                   final Sequence sep) {
        this(FoldingPolicy.DEFAULT,open,close,sep);
    }
    
    public Outline close() {
        final SequenceStack stk = new SequenceStack();
        final FoldingStatus foldingStatus = switch(foldingPolicy) {
            case FOLD -> FoldingStatus.inline;
            case EXPAND -> FoldingStatus.expanded;
            default -> {
                if(elements.empty()) yield FoldingStatus.empty;
                
                //TODO
                yield null;
            }
        };
        return this;
    }
}































