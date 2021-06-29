package json.value.collection;

import java.util.Iterator;
import java.util.TreeMap;

import json.exception.JSONException;
import json.value.JSONString;
import json.value.JSONValue;
import json.value.ValueType;
import nbt.value.NBTValue;
import util.string.Joiner;
import util.string.outline.WrappingSegment;

public class JSONObject extends JSONCollection<JSONString,JSONTag> {
    private static final ValueType TYPE = ValueType.OBJECT;
    @Override public ValueType type() {return TYPE;}
    
    private final TreeMap<JSONString,JSONValue> values = new TreeMap<>();
    
    
    @Override
    public Iterator<JSONTag> iterator() {
        return null;
    }
    
    private static <V> V checkNN(final V v) throws NullPointerException {
        if(v == null) throw new NullPointerException("Cannot set a null value.");
        return v;
    }
    @Override
    public JSONObject set(final JSONString key,final JSONValue value) {
        //values.put(key,);
        return this;
    }
    public JSONObject set(final String key,final JSONValue value) throws JSONException {
        return set(new JSONString(key),value);
    }
    
    @Override
    public JSONValue get(final JSONString key) {
        
        return null;
    }
    
    @Override
    public JSONValue remove(JSONString key) {
        return null;
    }
    
    @Override
    protected Joiner getJoiner() {
        return null;
    }
    
    @Override
    protected WrappingSegment getWrapper() {
        return null;
    }
    
    @Override
    protected Iterable<NBTValue> values() {
        return null;
    }
    
    
}
