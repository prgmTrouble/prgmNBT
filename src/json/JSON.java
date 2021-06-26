package json;

import util.string.Stringifiable;

public abstract class JSON implements Stringifiable {
    
    public JSON() {}
    
    @Override public String toString() {return toSequence().toString();}
    
    
}