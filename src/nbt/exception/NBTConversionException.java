package nbt.exception;

import nbt.value.ValueType;

public class NBTConversionException extends NBTException {
    private static final long serialVersionUID = 1L;
    private static String fmtConversion(final ValueType from,final ValueType to) {
        return "Cannot convert type %s to type %s.".formatted(from.name,to.name);
    }
    private static String fmtConversion(final String from,final ValueType to) {
        return "Cannot convert type %s to type %s.".formatted(from,to.name);
    }
    
    public NBTConversionException() {super();}
    public NBTConversionException(final String message) {super(message);}
    public NBTConversionException(final String message,final Throwable t) {super(message,t);}
    public NBTConversionException(final ValueType from,final ValueType to) {super(fmtConversion(from,to));}
    public NBTConversionException(final ValueType from,final ValueType to,final Throwable t) {super(fmtConversion(from,to),t);}
    public NBTConversionException(final String from,final ValueType to) {super(fmtConversion(from,to));}
}