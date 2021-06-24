package util.container;

public final class ArrayUtils {
    // Prevent instances.
    private ArrayUtils() {}
    
    public static <T> void fill(final T[] arr,final T value,final int start,final int length) {
        arr[start] = value;
        int i,l;
        for(i = start + 1,l = 1;l <= length / 2;i += l,l <<= 1)
            System.arraycopy(arr,start,arr,i,l);
        if(l != length) System.arraycopy(arr,start,arr,i,length - l);
    }
    public static <T> void fill(final T[] arr,final T value) {
        arr[0] = value;
        int i;
        for(i = 1;i <= arr.length / 2;i <<= 1)
            System.arraycopy(arr,0,arr,i,i);
        if(i != arr.length) System.arraycopy(arr,0,arr,i,arr.length - i);
    }
    
    public static void fill(final char[] arr,final char value,final int start,final int length) {
        arr[start] = value;
        int i,l;
        for(i = start + 1,l = 1;l <= length / 2;i += l,l <<= 1)
            System.arraycopy(arr,start,arr,i,l);
        if(l < length) System.arraycopy(arr,start,arr,i,length - l);
    }
    public static void fill(final char[] arr,final char value) {
        arr[0] = value;
        int i;
        for(i = 1;i <= arr.length / 2;i <<= 1)
            System.arraycopy(arr,0,arr,i,i);
        if(i != arr.length) System.arraycopy(arr,0,arr,i,arr.length - i);
    }
}