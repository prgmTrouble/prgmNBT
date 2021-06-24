package nbt.exception;

import settings.Settings;

public class NBTException extends Exception {
    private static final long serialVersionUID = 1L;
    protected static String version(final String message) {
        final String current = Settings.version().toString();
        if(message == null) return current;
        return new StringBuilder(current).append(": ").append(message).toString();
    }
    
    public NBTException() {super(version(null));}
    public NBTException(final String message) {super(version(message));}
    public NBTException(final String message,final Throwable t) {super(version(message),t);}
}