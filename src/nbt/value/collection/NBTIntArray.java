package nbt.value.collection;

import java.io.DataInput;
import java.io.IOException;
import nbt.exception.NBTException;
import nbt.value.NBTValue;
import nbt.value.ValueType;
import nbt.value.number.NBTInt;
import util.string.Joiner;

/**
 * An {@linkplain NBTPrimitiveArray} which holds {@linkplain NBTInt} values.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class NBTIntArray extends NBTPrimitiveArray {
    public static final ValueType TYPE = ValueType.INT_ARRAY;
    @Override public ValueType type() {return TYPE;}
    public static final PrimitiveArrayToken TOKEN = PrimitiveArrayToken.INT;
    
    @Override protected Joiner getJoiner() {return TOKEN.getJoiner();}
    
    /**
     * Creates an empty int array with default minimalism.
     * 
     * @see NBTValue#NBTValue()
     * 
     * @throws NBTException Primitive arrays are not enabled in this version.
     */
    public NBTIntArray() throws NBTException {super(TOKEN.subtype);}
    /**
     * Creates an empty int array.
     * 
     * @see NBTValue#NBTValue(boolean)
     * 
     * @throws NBTException Primitive arrays are not enabled in this version.
     */
    public NBTIntArray(final boolean minimal) throws NBTException {super(TOKEN.subtype,minimal);}
    /**
     * Reads an int array.
     * 
     * @throws IOException The array could not be read.
     * @throws NBTException Primitive arrays are not enabled in this version.
     */
    public NBTIntArray(final DataInput in) throws IOException,NBTException {
        this();
        final int l = in.readInt();
        for(int i = 0;i < l;++i)
            add(new NBTInt(in.readInt()));
    }
    
    /**
     * Adds all the values to the array.
     * 
     * @return <code>this</code>
     */
    public NBTIntArray addAll(final int...elements) {
        if(elements != null && elements.length != 0)
            for(final int e : elements) values.add(new NBTInt(e));
        return this;
    }
}