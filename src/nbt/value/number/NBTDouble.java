package nbt.value.number;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import nbt.value.NBTValue;
import nbt.value.ValueType;

/**
 * A 64-bit floating-point {@linkplain NBTNumber}.
 * 
 * @author prgmTrouble 
 * @author AzureTriple
 */
public class NBTDouble extends NBTFP {
    public static final ValueType TYPE = ValueType.DOUBLE;
    @Override public ValueType type() {return TYPE;}
    
    public static final char SUFFIX_0 = 'd',SUFFIX_1 = 'D';
    public static final boolean SUFFIX_POLICY = false;
    public static final double GLOBAL_DEFAULT = 0d;
    
    /**Used in {@linkplain #isDefault()}.*/
    public double localDefault = GLOBAL_DEFAULT;
    
    /**
     * Creates a double value of {@linkplain #GLOBAL_DEFAULT} with default
     * minimalism.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    public NBTDouble() {super(GLOBAL_DEFAULT);}
    /**
     * Creates a double value of {@linkplain #GLOBAL_DEFAULT}.
     * 
     * @see {@linkplain NBTValue#NBTValue(boolean)}
     */
    public NBTDouble(final boolean minimal) {super(GLOBAL_DEFAULT,minimal);}
    /**
     * Creates a double value with default minimalism.
     * 
     * @see {@linkplain NBTValue#NBTValue()}
     */
    public NBTDouble(final double value) {super(value);}
    /**
     * Creates a double value.
     * 
     * @see {@linkplain NBTValue#NBTValue(boolean)}
     */
    public NBTDouble(final double value,final boolean minimal) {super(value,minimal);}
    /**Reads a double value.*/
    public NBTDouble(final DataInput in) throws IOException {super(in.readDouble());}
    /**Writes this double value.*/
    @Override
    public void write(final DataOutput out) throws IOException {out.writeDouble(value.doubleValue());}
    
    public NBTDouble setValue(final double value) {this.value = value; return this;}
    
    @Override public boolean isDefault() {return value.doubleValue() == localDefault;}
    
    @Override protected char suffix() {return SUFFIX_0;}
    @Override protected boolean forceSuffix() {return SUFFIX_POLICY;}
    
    /**@return <code>true</code> iff the argument matches a double suffix.*/
    public static boolean isDoubleSuffix(final char suffix) {
        return suffix == SUFFIX_0 || suffix == SUFFIX_1;
    }
}