package util.string.outline;

import util.string.Sequence;
import util.string.SequenceStack;
import util.string.outline.position.Adjacency;
import util.string.outline.position.FoldingStatus;

public class OutlineToken extends OutlineElement {
    /*
    private final Rule[][] rules;
    {
        rules = new Rule[FoldingStatus.values().length]
                        [Adjacency.values().length];
    }
    */
    private Rule[] rules;
    {
        rules = new Rule[Adjacency.values().length];
    }
    
    private final Outline outline;
    
    public OutlineToken(final Sequence token,final Outline outline) {
        super(token);
        this.outline = outline;
    }
    
    public OutlineToken setRule(//final FoldingStatus foldingStatus,
                                final Adjacency adjacency,
                                final Rule rule) {
        //rules[foldingStatus.ordinal()][adjacency.ordinal()] = rule;
        rules[adjacency.ordinal()] = rule;
        return this;
    }
    public Rule getRule(//final FoldingStatus foldingStatus,
                        final Adjacency adjacency) {
        //return rules[foldingStatus.ordinal()][adjacency.ordinal()];
        return rules[adjacency.ordinal()];
    }
    
    public SequenceStack apply(final FoldingStatus foldingStatus,
                               final SequenceStack builder) {
        /*
        final Rule[] fs = rules[foldingStatus.ordinal()];
        {
            final Rule before = fs[Adjacency.before.ordinal()];
            if(before != null) builder.push(before.apply(foldingStatus));
        }
        super.apply(builder);
        {
            final Rule after = fs[Adjacency.after.ordinal()];
            if(after != null) builder.push(after.apply(foldingStatus));
        }
        */
        {
            final Rule before = getRule(Adjacency.before);
            if(before != null) builder.push(before.apply(foldingStatus));
        }
        super.apply(builder);
        {
            final Rule after = getRule(Adjacency.after);
            if(after != null) builder.push(after.apply(foldingStatus));
        }
        return builder;
    }
    
    @Override
    public int sizeIfInlined() {
        //TODO
        
        return super.sizeIfInlined();
    }
}