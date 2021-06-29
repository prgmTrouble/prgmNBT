package json.value;

import util.string.Sequence;
import util.string.outline.Segment;
import util.string.outline.ValueSegment;

/**
 * A {@linkplain JSONValue} representing <code>null</code>.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public final class JSONNull extends JSONValue {
    public static final ValueType TYPE = ValueType.NULL;
    @Override public ValueType type() {return TYPE;}
    
    /**A shared instance of the <code>null</code> type.*/
    public static final JSONNull INSTANCE = new JSONNull();
    public static final Sequence NULL_SEQUENCE = new Sequence(String.valueOf((Object)null));
    private static final Segment SEGMENT = new ValueSegment(NULL_SEQUENCE);
    
    private JSONNull() {super();}
    
    @Override public Sequence toSequence() {return NULL_SEQUENCE;}
    @Override public Segment toSegment() {return SEGMENT;}
    
    //TODO parsing
}