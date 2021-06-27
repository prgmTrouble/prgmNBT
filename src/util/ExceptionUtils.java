package util;

import settings.Settings;
import util.string.Sequence;

public final class ExceptionUtils {
    private ExceptionUtils() {}
    
    public static String fmtParsing(final String message,final int index,final Sequence data) {
        final int maxShown = Settings.exceptionCharacterLimit();
        return "%s at position %d: %s <-".formatted(
            message,index,
            (index > maxShown? "..." : "") +
            data.subSequence(
                Math.min(Math.max(index - maxShown,0),data.length()),
                Math.min(Math.max(index + 1,0),data.length())
            )
        );
    }
    public static String fmtParsingTerminator(final String type,
                                              final int index,
                                              final Sequence data,
                                              final Character terminator,
                                              final boolean commas,
                                              final Character c) {
        return fmtParsing(
            "Missing %s%s in %s (was '%s')".formatted(
                commas? "comma or "
                      : "",
                terminator == null? "end of sequence"
                                  : "closing character '%c'".formatted(terminator),
                type,c == null? "null":c
            ),
            index,data
        ); 
    }
    
    
    public static String fmtJSONConversion(final json.value.ValueType from,final json.value.ValueType to) {
        return "Cannot convert type %s to type %s.".formatted(from.name,to.name);
    }
    public static String fmtJSONConversion(final String from,final json.value.ValueType to) {
        return "Cannot convert type %s to type %s.".formatted(from,to.name);
    }
    public static String fmtNBTConversion(final nbt.value.ValueType from,final nbt.value.ValueType to) {
        return "Cannot convert type %s to type %s.".formatted(from.name,to.name);
    }
    public static String fmtNBTConversion(final String from,final nbt.value.ValueType to) {
        return "Cannot convert type %s to type %s.".formatted(from,to.name);
    }
}