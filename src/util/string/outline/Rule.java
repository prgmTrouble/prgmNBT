package util.string.outline;

import util.string.Sequence;
import util.string.outline.position.FoldingStatus;

public interface Rule {
    /**Applies this rule to the builder.*/
    public Sequence apply(FoldingStatus fs);
}