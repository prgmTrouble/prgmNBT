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
     * @see {@linkplain NBTValue#NBTValue()}
     */
    public NBTIntArray() throws NBTException {super(TOKEN.subtype);}
    /**
     * Creates an empty int array.
     * 
     * @see {@linkplain NBTValue#NBTValue(boolean)}
     */
    public NBTIntArray(final boolean minimal) throws NBTException {super(TOKEN.subtype,minimal);}
    /**Reads an int array.*/
    public NBTIntArray(final DataInput in) throws IOException,NBTException {
        this();
        final int l = in.readInt();
        for(int i = 0;i < l;++i)
            add(new NBTInt(in.readInt()));
    }
    
    public NBTIntArray addAll(final int...elements) {
        if(elements != null && elements.length != 0)
            for(final int e : elements) values.add(new NBTInt(e));
        return this;
    }
}