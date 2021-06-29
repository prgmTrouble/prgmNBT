package json.value.collection;

import json.JSON;
import json.value.JSONNull;
import json.value.JSONString;
import json.value.JSONValue;
import util.string.Joiner;
import util.string.Sequence;
import util.string.outline.JoiningSegment;
import util.string.outline.Segment;

/**
 * An {@linkplain JSON} representing a key-value pair.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class JSONTag extends JSON {
    public static final char SEPARATOR = ':';
    
    /**This tag's key. This should not be modifiable.*/
    private final JSONString key;
    /**The {@linkplain JSONValue} held by this object.*/
    private JSONValue value = null;
    
    /**Creates a tag with a <code>null</code> value.*/
    public JSONTag(final JSONString key) {this.key = key;}
    /**Creates a tag.*/
    public JSONTag(final JSONString key,final JSONValue value) {this(key); value(value);}
    
    /**@return This tag's value.*/
    public JSONValue value() {return value;}
    /**
     * @param value This tag's value.
     * 
     * @return <code>this</code>
     */
    public JSONTag value(final JSONValue value) {
        this.value = value == null? JSONNull.INSTANCE : value;
        return this;
    }
    
    @Override public Sequence toSequence() {return value.appendTo(key.appendTo(new Joiner(':'))).concat();}
    @Override
    public Segment toSegment() {//TODO customization?
        return new JoiningSegment(new Sequence(SEPARATOR))
                            .push(key.toSegment())
                            .push(value.toSegment());
    }
    
    //TODO parsing
}