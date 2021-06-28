package gui.theme;

import javafx.scene.paint.Color;

public class Palette {
    public static class PaletteColor {
        private static final double BRIGHTNESS_SCALAR = 18.0,
                                    Xn = 0.95047,
                                    Yn = 1.0,
                                    Zn = 1.08883,
                                    t0 = 0.137931034,
                                    t1 = 0.206896552,
                                    t2 = 0.12841855,
                                    t3 = 0.008856452,
                                    K_DIV_PHI = Math.pow(132./1477.,2.4);
        private static final double[][] TRANSFORM_MATRIX = {{+ 12831./  3959.,-    329./   214.,- 1974./  3959.},
                                                            {-851781./878810.,+1648619./878810.,+36519./878810.},
                                                            {+   705./ 12673.,-   2585./ 12673.,+  705./   667.}};
        private static int R(final int rgba) {return rgba >>> 24;}
        private static int G(final int rgba) {return rgba >>> 16 & 0xFF;}
        private static int B(final int rgba) {return rgba >>>  8 & 0xFF;}
        private static int A(final int rgba) {return rgba & 0xFF;}
        private static byte transform(final double[] cie,final int row) {
            double out = 0.0;
            final double[] r = TRANSFORM_MATRIX[row];
            for(int i = 0;i < 3;++i) out += cie[i] * r[i];
            return (byte)Math.max(
                Math.min(
                    (int)Math.round(
                        255.0 * (K_DIV_PHI < out? (1.055 * Math.pow(out,1.0 / 2.4) - 0.055) : (12.92 * out))
                    ),
                    255
                ),
                0
            );
        }
        
        private static double norm(final double v) {return v > t1? Math.pow(v,3.0) : t2 * (v - t0);}
        
        private static byte[] CIE_XYZ_to_sRGB(double[] cie,final int alpha) {
            {
                final double x = (cie[0] + 16.0) / 116.0,
                             u = Yn * norm(x),
                             s = Xn * norm(Double.isNaN(cie[1])? x : (x + cie[1] / 500.0)),
                             c = Zn * norm(Double.isNaN(cie[2])? x : (x - cie[2] / 200.0));
                cie = new double[] {s,u,c};
            }
            return new byte[] {transform(cie,0),
                               transform(cie,1),
                               transform(cie,2),
                               (byte)Math.min(Math.max(alpha,0),0xFF)};
        }
        
        private static double[] lab(final int rgba) {
            final double[] cie = sRGB_to_CIE_XYZ(rgba);
            return new double[] {116.0 * cie[1] - 16.0,
                                 500.0 * (cie[0] - cie[1]),
                                 200.0 * (cie[1] - cie[2])};
        }
        
        private static double gammaCorrect(final int v) {
            final double out = (double)v / 255.0;
            return out <= 0.04045? out / 12.92 : Math.pow((out + 0.055) / 1.055,2.4);
        }
        private static final double[][] REV_TRANSFORM_MATRIX = {{506752./1228815., 87881./245763.,  12673./  70218.},
                                                                { 87098./ 409605.,175762./245763.,  12673./ 175545.},
                                                                {  7918./ 409605., 87881./737289.,1001167./1053270.}};
        
        private static double reverseTransform(final double[] gammaCorrected,
                                               final int row,
                                               final double scale) {
            double out = 0.0;
            final double[] r = REV_TRANSFORM_MATRIX[row];
            for(int i = 0;i < 3;++i) out += gammaCorrected[i] * r[i];
            out /= scale;
            return out > t3? Math.pow(out,1.0 / 3.0) : (out / t2 + t0);
        }
        
        private static double[] sRGB_to_CIE_XYZ(final int rgba) {
            final double[] out = {gammaCorrect(R(rgba)),
                                  gammaCorrect(G(rgba)),
                                  gammaCorrect(B(rgba))};
            return new double[] {reverseTransform(out,0,Xn),
                                 reverseTransform(out,1,Yn),
                                 reverseTransform(out,2,Zn)};
        }
        
        private static byte[] variant(final int rgba,final boolean lighter) {
            final double[] lab = lab(rgba);
            lab[0] -= lighter? -BRIGHTNESS_SCALAR : BRIGHTNESS_SCALAR;
            return CIE_XYZ_to_sRGB(lab,A(rgba));
            // omitted alpha calls
        }
        
        public final Color color,light,dark;
        public PaletteColor(final Color color,
                            final Color light,
                            final Color dark) {
            if((this.color = color) == null)
                throw new IllegalArgumentException(
                    "Cannot instantiate a null color."
                );
            this.light = light;
            this.dark = dark;
        }
        public PaletteColor(final Color color) {
            if((this.color = color) == null)
                throw new IllegalArgumentException(
                    "Cannot instantiate a null color."
                );
            final int rgba = color.hashCode();
            {
                final byte[] l = variant(rgba,true);
                light = Color.rgb(
                    Byte.toUnsignedInt(l[0]),
                    Byte.toUnsignedInt(l[1]),
                    Byte.toUnsignedInt(l[2]),
                    (double)l[3]/255.0
                );
            }
            {
                final byte[] l = variant(rgba,false);
                dark = Color.rgb(
                    Byte.toUnsignedInt(l[0]),
                    Byte.toUnsignedInt(l[1]),
                    Byte.toUnsignedInt(l[2]),
                    (double)l[3]/255.0
                );
            }
        }
        public PaletteColor(int color,final boolean alpha) {
            if(!alpha) color = color << 8 | 0xFF;
            this.color = Color.rgb(R(color),G(color),B(color),(double)A(color)/255.0);
            {
                final byte[] l = variant(color,true);
                light = Color.rgb(
                    Byte.toUnsignedInt(l[0]),
                    Byte.toUnsignedInt(l[1]),
                    Byte.toUnsignedInt(l[2]),
                    (double)Byte.toUnsignedInt(l[3])/255.0
                );
            }
            {
                final byte[] l = variant(color,false);
                dark = Color.rgb(
                    Byte.toUnsignedInt(l[0]),
                    Byte.toUnsignedInt(l[1]),
                    Byte.toUnsignedInt(l[2]),
                    (double)Byte.toUnsignedInt(l[3])/255.0
                );
            }
        }
    }
    
    public final PaletteColor primary,secondary;
    public Palette(final PaletteColor primary,
                   final PaletteColor secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }
    public Palette(final Color primary,
                   final Color secondary) {
        this.primary = new PaletteColor(primary);
        this.secondary = new PaletteColor(secondary);
    }
    /**Constructs a palette from colors in RGBA integer format.*/
    public Palette(final int primary,final int secondary) {
        this.primary = new PaletteColor(primary,true);
        this.secondary = new PaletteColor(secondary,true);
    }
}



























