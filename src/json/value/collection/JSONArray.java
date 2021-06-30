package json.value.collection;

import java.util.ArrayList;
import java.util.Iterator;

import json.exception.JSONException;
import json.exception.JSONParsingException;
import json.value.JSONNull;
import json.value.JSONValue;
import json.value.ValueType;
import util.string.Joiner;
import util.string.Sequence;
import util.string.Sequence.SequenceIterator;
import util.string.outline.WrappingSegment;

/**
 * A {@linkplain JSONCollection} which functions as an indexed array.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class JSONArray extends JSONCollection<Integer,JSONValue> {
    public static final ValueType TYPE = ValueType.ARRAY;
    @Override public ValueType type() {return TYPE;}
    
    public static final char OPEN = '[',CLOSE = ']';
    @Override protected Joiner getJoiner() {return new Joiner(OPEN,CLOSE);}
    @Override
    protected WrappingSegment getWrapper() {
        final Sequence[] wrapper = Sequence.shared(new char[] {OPEN,CLOSE},1);
        return new WrappingSegment(wrapper[0],wrapper[1]); //TODO customization?
    }
    
    private final ArrayList<JSONValue> values = new ArrayList<>();
    
    /**Creates an empty array.*/
    public JSONArray() {super();}
    
    @Override public Iterator<JSONValue> iterator() {return values.iterator();}
    @Override protected JSONArray values() {return this;}
    
    private boolean validKey(final Integer key) {return key != null && 0 <= key && key < values.size();}
    
    /**@throws JSONException The key is <code>null</code> or out of bounds.*/
    @Override
    public JSONArray set(final Integer key,final JSONValue value) throws JSONException {
        if(!validKey(key))
            if(key != Integer.valueOf(values.size())) // Avoid nullptr
                throw new JSONException("Invalid index: %d.".formatted(key));
        values.add(key,value == null? JSONNull.INSTANCE : value);
        return this;
    }
    /**
     * Adds the value to the end of the array.
     * 
     * @return <code>this</code>
     */
    public JSONArray add(final JSONValue value) {
        values.add(value == null? JSONNull.INSTANCE : value);
        return this;
    }
    
    /**@throws JSONException The key is <code>null</code> or out of bounds.*/
    @Override
    public JSONValue get(final Integer key) throws JSONException {
        if(!validKey(key))
            throw new JSONException("Invalid index: %d.".formatted(key));
        return values.get(key);
    }
    
    /**@throws JSONException The key is <code>null</code> or out of bounds.*/
    @Override
    public JSONValue remove(final Integer key) throws JSONException {
        if(!validKey(key))
            throw new JSONException("Invalid index: %d.".formatted(key));
        return values.remove(key.intValue());
    }
    
    /**
     * @param i A {@linkplain SequenceIterator} which points to the position at the
     *          opening bracket.
     * 
     * @return An {@linkplain JSONArray}.
     * 
     * @throws JSONParsingException The iterator cannot find a valid array.
     */
    public static JSONArray parse(final SequenceIterator i,
                                  final Character terminator,
                                  final boolean commas)
                                  throws JSONParsingException {
        final JSONArray arr = new JSONArray();
        i.nextNonWS();
        while(i.hasNext()) {
            arr.add(JSONValue.parse(i,CLOSE,true));
            if(i.peek() == CLOSE) break;
            i.nextNonWS();
        }
        if(i.peek() != CLOSE)
            throw new JSONParsingException(
                "Missing closing character '%c'"
                .formatted(CLOSE),i
            );
        final Character c = i.nextNonWS();
        if(!(commas && c == ',') && c != terminator)
            throw new JSONParsingException("array",i,terminator,commas,c);
        return arr;
    }
}