package json.value;

import util.string.Sequence;
import util.string.outline.Segment;
import util.string.outline.ValueSegment;

/**
 * A {@linkplain JSONValue} representing a boolean.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class JSONBool extends JSONValue {
    public static final ValueType TYPE = ValueType.BOOL;
    @Override public ValueType type() {return TYPE;}
    
    public static final Sequence TRUE,FALSE;
    static {
        final Sequence[] share = Sequence.shared(
            Boolean.toString(true).toCharArray(),
            Boolean.toString(false).toCharArray()
        );
        TRUE = share[0];
        FALSE = share[1];
    }
    private static final Segment TRUE_SEG = new ValueSegment(TRUE),
                                 FALSE_SEG = new ValueSegment(FALSE);
    
    private boolean value = false;
    
    /**Creates a boolean value of <code>false</code>.*/
    public JSONBool() {super();}
    /**Creates a boolean value.*/
    public JSONBool(final boolean value) {super(); value(value);}
    
    /**@return This boolean's value.*/
    public boolean value() {return value;}
    /**
     * @param value This boolean's value.
     * 
     * @return <code>this</code>
     */
    public JSONBool value(final boolean value) {this.value = value; return this;}
    
    @Override public Sequence toSequence() {return value()? TRUE : FALSE;}
    @Override public Segment toSegment() {return value()? TRUE_SEG : FALSE_SEG;}
    
    //TODO parsing
}