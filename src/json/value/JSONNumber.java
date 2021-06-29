package json.value;

import util.string.Sequence;

/**
 * A {@linkplain JSONValue} which holds a numeric value.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class JSONNumber extends JSONValue {
    public static final ValueType TYPE = ValueType.NUMBER;
    @Override public ValueType type() {return TYPE;}
    
    private Number value = 0;
    
    /**Creates a number with the value zero.*/
    public JSONNumber() {super();}
    /**
     * Creates a number with the specified value. <code>null</code> values yield
     * zero.
     */
    public JSONNumber(final Number value) {super(); value(value);}
    
    /**@return This number's value.*/
    public Number value() {return value;}
    /**
     * @param value This number's value. <code>null</code> values yield zero.
     * 
     * @return <code>this</code>
     */
    public JSONNumber value(final Number value) {
        this.value = value == null? 0 : value;
        return this;
    }
    
    @Override public Sequence toSequence() {return new Sequence(value().toString());}
    
    //TODO parsing
}