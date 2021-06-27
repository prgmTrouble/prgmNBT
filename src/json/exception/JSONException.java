package json.exception;

public class JSONException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public JSONException() {}
    public JSONException(final String message) {super(message);}
    public JSONException(final String message,final Throwable cause) {super(message,cause);}
}