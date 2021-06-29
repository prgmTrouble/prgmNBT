package json.value.collection;

import json.JSON;
import json.value.JSONValue;
import nbt.value.NBTValue;
import util.string.Joiner;
import util.string.Sequence;
import util.string.outline.JoiningSegment;
import util.string.outline.Segment;
import util.string.outline.WrappingSegment;

/**
 * A {@linkplain JSONValue} which holds other {@linkplain JSON}s.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public abstract class JSONCollection<K,V extends JSON> extends JSONValue implements Iterable<V> {
    
    public JSONCollection() {super();}
    
    /**Maps the value to the key.*/
    public abstract JSONCollection<K,V> set(final K key,final JSONValue value);
    /**Gets an value mapped to the key.*/
    public abstract JSONValue get(final K key);
    /**Removes and returns the value mapped to the key.*/
    public abstract JSONValue remove(final K key);
    
    /**Gets a {@linkplain Joiner} instance to use when converting to a character sequence.*/
    protected abstract Joiner getJoiner();
    /**Gets a {@linkplain WrappingSegment} instance to use when converting to an outline.*/
    protected abstract WrappingSegment getWrapper();
    
    protected abstract Iterable<NBTValue> values();
    
    @Override
    public Sequence toSequence() {
        final Joiner j = getJoiner();
        for(final V nbt : this) nbt.appendTo(j);
        return j.concat();
    }
    
    /**@return A segment containing the children in a stringified form.*/
    protected Segment getChildren() {
        final JoiningSegment s = new JoiningSegment(new Sequence(',')); //TODO customization?
        for(final JSON n : this) s.push(n.toSegment());
        return s;
    }
    @Override public Segment toSegment() {return getWrapper().child(getChildren());}
}