package nbt.value.collection;

import java.io.DataInput;
import java.io.IOException;
import nbt.exception.NBTException;
import nbt.value.NBTValue;
import nbt.value.ValueType;
import nbt.value.number.NBTLong;
import util.string.Joiner;

/**
 * An {@linkplain NBTPrimitiveArray} which holds {@linkplain NBTLong} values.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class NBTLongArray extends NBTPrimitiveArray {
    public static final ValueType TYPE = ValueType.LONG_ARRAY;
    @Override public ValueType type() {return TYPE;}
    public static final PrimitiveArrayToken TOKEN = PrimitiveArrayToken.LONG;
    
    @Override protected Joiner getJoiner() {return TOKEN.getJoiner();}
    
    /**
     * Creates an empty long array with default minimalism.
     * 
     * @see NBTValue#NBTValue()
     * 
     * @throws NBTException Primitive arrays are not enabled in this version.
     */
    public NBTLongArray() throws NBTException {super(TOKEN.subtype);}
    /**
     * Creates an empty long array.
     * 
     * @see NBTValue#NBTValue(boolean)
     * 
     * @throws NBTException Primitive arrays are not enabled in this version.
     */
    public NBTLongArray(final boolean minimal) throws NBTException {super(TOKEN.subtype,minimal);}
    /**
     * Reads an long array.
     * 
     * @throws IOException The array could not be read.
     * @throws NBTException Primitive arrays are not enabled in this version.
     */
    public NBTLongArray(final DataInput in) throws IOException,NBTException {
        this();
        final int l = in.readInt();
        for(int i = 0;i < l;++i)
            add(new NBTLong(in.readLong()));
    }
    
    /**
     * Adds all the values to the array.
     * 
     * @return <code>this</code>
     */
    public NBTLongArray addAll(final long...elements) {
        if(elements != null && elements.length != 0)
            for(final long e : elements) values.add(new NBTLong(e));
        return this;
    }
}