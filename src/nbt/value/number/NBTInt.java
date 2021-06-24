package nbt.value.number;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import nbt.value.NBTValue;
import nbt.value.ValueType;

/**
 * @see NBTi32
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public class NBTInt extends NBTi32 {
    public static final ValueType TYPE = ValueType.INT;
    @Override public ValueType type() {return TYPE;}
    
    public static final char[] SUFFIXES = {0}; // Added for the sake of completeness.
    public static final char ARRAY_TOKEN = 'I';
    
    /**
     * Creates an integer value of {@linkplain NBTi32#GLOBAL_DEFAULT} with default
     * minimalism.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    public NBTInt() {super();}
    /**
     * Creates an integer value of {@linkplain NBTi32#GLOBAL_DEFAULT}.
     * 
     * @see {@linkplain NBTValue#NBTValue(boolean)}
     */
    public NBTInt(final boolean minimal) {super(minimal);}
    /**
     * Creates an integer value default minimalism.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    public NBTInt(final int value) {super(value);}
    /**
     * Creates an integer value.
     * 
     * @see {@linkplain NBTValue#NBTValue(boolean)}
     */
    public NBTInt(final int value,final boolean minimal) {super(value,minimal);}
    /**Reads an integer value.*/
    public NBTInt(final DataInput in) throws IOException {super(in.readInt());}
    /**Writes this integer value.*/
    @Override
    public void write(final DataOutput out) throws IOException {out.writeInt(value.intValue());}
    
    @Override public NBTInt setValue(final int value) {return (NBTInt)super.setValue(value);}
    @Override public NBTInt setDefault(final int value) {return (NBTInt)super.setDefault(value);}
    
    @Override protected char suffix() {return SUFFIXES[0];}
}