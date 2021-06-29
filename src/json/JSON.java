package json;

import util.string.Sequence;
import util.string.Stringifiable;

/**
 * A {@linkplain Stringifiable} data format using the ECMA-404 standard.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public abstract class JSON implements Stringifiable {
    public JSON() {}
    
    @Override public String toString() {return toSequence().toString();}
    
    public static JSON parse(final Sequence s) {
        //TODO
        return null;
    }
}