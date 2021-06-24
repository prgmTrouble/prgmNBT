package gui.theme;

public enum Theme {
    LIGHT(0x6A6E73,0xD9D9D9),
    DARK(0x2F3136,0x222426);
    
    public final Palette palette;
    private Theme(final int p,final int s) {palette = new Palette(p,s);}
}