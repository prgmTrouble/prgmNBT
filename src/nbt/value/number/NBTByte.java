package nbt.value.number;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import nbt.value.NBTValue;
import nbt.value.ValueType;

/**
 * An 8-bit integral value.
 * 
 * @see NBTi32
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class NBTByte extends NBTi32 {
    public static final ValueType TYPE = ValueType.BYTE;
    @Override public ValueType type() {return TYPE;}
    
    public static final char SUFFIX_0 = 'b',SUFFIX_1 = 'B';
    public static final char ARRAY_TOKEN = 'B';
    
    /**
     * Creates a byte value of {@linkplain NBTi32#GLOBAL_DEFAULT} with default
     * minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTByte() {super();}
    /**
     * Creates a byte value of {@linkplain NBTi32#GLOBAL_DEFAULT}.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTByte(final boolean minimal) {super(minimal);}
    /**
     * Creates a byte value default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTByte(final byte value) {super(value);}
    /**
     * Creates a byte value default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTByte(final int value) {super(value);}
    /**
     * Creates a byte value.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTByte(final byte value,final boolean minimal) {super(value,minimal);}
    /**
     * Creates a byte value.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTByte(final int value,final boolean minimal) {super(value,minimal);}
    /**
     * Reads a byte value.
     * 
     * @throws IOException The value could not be read.
     */
    public NBTByte(final DataInput in) throws IOException {super(in.readByte());}
    /**
     * Writes this byte value.
     * 
     * @throws IOException The value could not be written.
     */
    @Override
    public void write(final DataOutput out) throws IOException {out.writeByte(value.intValue());}
    
    @Override public NBTByte setValue(final int value) {return (NBTByte)super.setValue(value);}
    public NBTByte setValue(final byte value) {return (NBTByte)super.setValue((int)value);}
    
    @Override public NBTByte setDefault(int localDefault) {return (NBTByte)super.setDefault(localDefault);}
    public NBTByte setDefault(final byte localDefault) {return (NBTByte)super.setValue((int)localDefault);}
    
    @Override protected char suffix() {return SUFFIX_0;}
    
    /**@return <code>true</code> iff the argument matches a byte suffix.*/
    public static boolean isByteSuffix(final char suffix) {
        return suffix == SUFFIX_0 || suffix == SUFFIX_1;
    }
}