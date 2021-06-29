package nbt.value.collection;

import java.io.DataInput;
import java.io.IOException;
import nbt.exception.NBTException;
import nbt.value.NBTValue;
import nbt.value.ValueType;
import nbt.value.number.NBTByte;
import util.string.Joiner;

/**
 * An {@linkplain NBTPrimitiveArray} which holds {@linkplain NBTByte} values.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class NBTByteArray extends NBTPrimitiveArray {
    public static final ValueType TYPE = ValueType.BYTE_ARRAY;
    @Override public ValueType type() {return TYPE;}
    public static final PrimitiveArrayToken TOKEN = PrimitiveArrayToken.BYTE;
    
    @Override protected Joiner getJoiner() {return TOKEN.getJoiner();}
    
    /**
     * Creates an empty byte array with default minimalism.
     * 
     * @see NBTValue#NBTValue()
     * 
     * @throws NBTException Primitive arrays are not enabled in this version.
     */
    public NBTByteArray() throws NBTException {super(TOKEN.subtype);}
    /**
     * Creates an empty byte array.
     * 
     * @see NBTValue#NBTValue(boolean)
     * 
     * @throws NBTException Primitive arrays are not enabled in this version.
     */
    public NBTByteArray(final boolean minimal) throws NBTException {super(TOKEN.subtype,minimal);}
    /**
     * Reads a byte array.
     * 
     * @throws IOException The array could not be read.
     * @throws NBTException Primitive arrays are not enabled in this version.
     */
    public NBTByteArray(final DataInput in) throws IOException,NBTException {
        this();
        final byte[] elements = new byte[in.readInt()];
        in.readFully(elements);
        addAll(elements);
    }
    
    /**
     * Adds all the values to the array.
     * 
     * @return <code>this</code>
     */
    public NBTByteArray addAll(final byte...elements) {
        if(elements != null && elements.length != 0)
            for(final byte e : elements) values.add(new NBTByte(e));
        return this;
    }
    /**
     * Adds all the values to the array.
     * 
     * @return <code>this</code>
     */
    public NBTByteArray addAll(final int...elements) {
        if(elements != null && elements.length != 0)
            for(final int e : elements) values.add(new NBTByte(e));
        return this;
    }
}