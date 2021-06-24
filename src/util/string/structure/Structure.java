package util.string.structure;

import util.string.Sequence;

/**
 * An interface which describes how to stringify data.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public interface Structure {
    public int size();
    public boolean isExpanded();
    public Sequence pop();
    public Sequence inline();
    public Sequence preview();
}