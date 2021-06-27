package nbt.exception;

import static util.ExceptionUtils.fmtNBTConversion;

import nbt.value.ValueType;

public class NBTConversionException extends NBTException {
    private static final long serialVersionUID = 1L;
    
    public NBTConversionException() {super();}
    public NBTConversionException(final String message) {super(message);}
    public NBTConversionException(final String message,final Throwable t) {super(message,t);}
    public NBTConversionException(final ValueType from,final ValueType to) {super(fmtNBTConversion(from,to));}
    public NBTConversionException(final ValueType from,final ValueType to,final Throwable t) {super(fmtNBTConversion(from,to),t);}
    public NBTConversionException(final String from,final ValueType to) {super(fmtNBTConversion(from,to));}
}