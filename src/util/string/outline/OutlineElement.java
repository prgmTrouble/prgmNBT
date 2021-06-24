package util.string.outline;

import util.string.Sequence;
import util.string.SequenceStack;

public class OutlineElement {
    protected final Sequence element;
    
    public OutlineElement(final Sequence element) {this.element = element;}
    
    public SequenceStack apply(final SequenceStack builder) {return builder.push(element);}
    
    public int sizeIfInlined() {return element.length();}
}