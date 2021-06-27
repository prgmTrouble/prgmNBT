package json.exception;

import static util.ExceptionUtils.fmtJSONConversion;

import json.value.ValueType;

public class JSONConversionException extends JSONException {
    private static final long serialVersionUID = 1L;
    
    public JSONConversionException() {super();}
    public JSONConversionException(final String message) {super(message);}
    public JSONConversionException(final String message,final Throwable t) {super(message,t);}
    public JSONConversionException(final ValueType from,final ValueType to) {super(fmtJSONConversion(from,to));}
    public JSONConversionException(final ValueType from,final ValueType to,final Throwable t) {super(fmtJSONConversion(from,to),t);}
    public JSONConversionException(final String from,final ValueType to) {super(fmtJSONConversion(from,to));}
}