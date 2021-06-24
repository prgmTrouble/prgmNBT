package util.string.structure;

import util.string.Sequence;

public interface Rule {
    /**Applies this rule to the builder.*/
    public Sequence apply(StructureBuilder b);
}