package json;

import util.string.Sequence;
import util.string.Stringifiable;

public abstract class JSON implements Stringifiable {
    // ECMA-404 Standard
    
    public JSON() {}
    
    @Override public String toString() {return toSequence().toString();}
    
    public static JSON parse(final Sequence s) {
        //TODO
        return null;
    }
}