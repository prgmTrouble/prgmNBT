package nbt.value.number;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import nbt.value.NBTValue;
import nbt.value.ValueType;

/**
 * A 16-bit integral type.
 * 
 * @see NBTi32
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class NBTShort extends NBTi32 {
    public static final ValueType TYPE = ValueType.SHORT;
    @Override public ValueType type() {return TYPE;}
    
    public static final char SUFFIX_0 = 's',SUFFIX_1 = 'S';
    
    /**
     * Creates a short value of {@linkplain NBTi32#GLOBAL_DEFAULT} with default
     * minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTShort() {super();}
    /**
     * Creates a short value of {@linkplain NBTi32#GLOBAL_DEFAULT}.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTShort(final boolean minimal) {super(minimal);}
    /**
     * Creates a short value default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTShort(final short value) {super(value);}
    /**
     * Creates a short value default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTShort(final int value) {super(value);}
    /**
     * Creates a short value.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTShort(final short value,final boolean minimal) {super(value,minimal);}
    /**
     * Creates a short value.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTShort(final int value,final boolean minimal) {super(value,minimal);}
    /**Reads a short value.*/
    public NBTShort(final DataInput in) throws IOException {super(in.readShort());}
    /**Writes this short value.*/
    @Override
    public void write(final DataOutput out) throws IOException {out.writeShort(value.intValue());}
    
    @Override public NBTShort setValue(final int value) {return (NBTShort)super.setValue(value);}
    public NBTShort setValue(final short value) {return (NBTShort)super.setValue((int)value);}
    
    @Override public NBTShort setDefault(final int value) {return (NBTShort)super.setDefault(value);}
    public NBTShort setDefault(final short value) {return (NBTShort)super.setDefault((int)value);}
    
    @Override protected char suffix() {return SUFFIX_0;}
    
    /**@return <code>true</code> iff the argument matches a short suffix.*/
    public static boolean isShortSuffix(final char suffix) {
        return suffix == SUFFIX_0 || suffix == SUFFIX_1;
    }
}