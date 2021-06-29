package nbt.value.number;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import nbt.value.NBTValue;
import nbt.value.ValueType;
import settings.Version;

/**
 * A 32-bit floating-point {@linkplain NBTNumber}.
 * 
 * @author prgmTrouble
 * @author AzureTriple
 */
public class NBTFloat extends NBTFP {
    public static final ValueType TYPE = ValueType.FLOAT;
    @Override public ValueType type() {return TYPE;}
    
    public static final char SUFFIX_0 = 'f',SUFFIX_1 = 'F';
    /**
     * Before "The Flattening," the suffixes are required to ensure that lists are
     * correct. However, no instances of 32-bit floats appear in SNBT outside of
     * lists (as far as I can tell). For simplicity, this constant assumes that
     * floats will always have suffixes in versions before the flattening.
     */
    public static final boolean SUFFIX_POLICY = Version.atMost(Version.The_Flattening);
    public static final float GLOBAL_DEFAULT = 0f;
    
    /**Used in {@linkplain #isDefault()}.*/
    public float localDefault = GLOBAL_DEFAULT;
    
    /**
     * Creates a float value of {@linkplain #GLOBAL_DEFAULT} with default
     * minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTFloat() {super(GLOBAL_DEFAULT);}
    /**
     * Creates a float value of {@linkplain #GLOBAL_DEFAULT}.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTFloat(final boolean minimal) {super(GLOBAL_DEFAULT,minimal);}
    /**
     * Creates a float value with default minimalism.
     * 
     * @see NBTValue#NBTValue()
     */
    public NBTFloat(final float value) {super(value);}
    /**
     * Creates a float value.
     * 
     * @see NBTValue#NBTValue(boolean)
     */
    public NBTFloat(final float value,final boolean minimal) {super(value,minimal);}
    /**Reads a float value.*/
    public NBTFloat(final DataInput in) throws IOException {super(in.readFloat());}
    /**Writes this float value.*/
    @Override
    public void write(final DataOutput out) throws IOException {out.writeFloat(value.floatValue());}
    
    public NBTFloat setValue(final float value) {this.value = value; return this;}
    
    @Override public boolean isDefault() {return value.floatValue() == localDefault;}
    
    @Override protected char suffix() {return SUFFIX_0;}
    @Override protected boolean forceSuffix() {return SUFFIX_POLICY;}
    
    /**@return <code>true</code> iff the argument matches a float suffix.*/
    public static boolean isFloatSuffix(final char suffix) {
        return suffix == SUFFIX_0 || suffix == SUFFIX_1;
    }
}