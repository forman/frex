package z.core.color;

import java.text.ParseException;

public class RGBA {
    public static final RGBA TRANSPARENT = new RGBA(0, 0, 0, 0);

    public static final RGBA BLACK = new RGBA(0, 0, 0);

    public static final RGBA RED = new RGBA(255, 0, 0);

    public static final RGBA GREEN = new RGBA(0, 255, 0);

    public static final RGBA BLUE = new RGBA(0, 0, 255);

    public static final RGBA ORANGE = new RGBA(255, 200, 0);

    public static final RGBA YELLOW = new RGBA(255, 255, 0);

    public static final RGBA CYAN = new RGBA(0, 255, 255);

    public static final RGBA MAGENTA = new RGBA(255, 0, 255);

    public static final RGBA PINK = new RGBA(255, 175, 175);

    public static final RGBA DARK_GRAY = new RGBA(64, 64, 64);

    public static final RGBA GRAY = new RGBA(128, 128, 128);

    public static final RGBA LIGHT_GRAY = new RGBA(192, 192, 192);

    public static final RGBA WHITE = new RGBA(255, 255, 255);

    private static final int A_POS = 24;

    private static final int R_POS = 16;

    private static final int G_POS = 8;

    private static final int B_POS = 0;

    private static final int UBYTE_MASK = 0xFF;

    private final int value;

    public RGBA(int r, int g, int b) {
        this(compose(r, g, b));
    }

    public RGBA(int r, int g, int b, int a) {
        this(compose(r, g, b, a));
    }

    public RGBA(float r, float g, float b) {
        this(compose(r, g, b));
    }

    public RGBA(float r, float g, float b, float a) {
        this(compose(r, g, b, a));
    }

    public RGBA(float[] components) {
        this(components[0], components[1], components[2], components[3]);
    }

    public RGBA(int value) {
        this.value = value;
    }

    public static int compose(int r, int g, int b) {
        return compose(r, g, b, 255);
    }

    public static int compose(int r, int g, int b, int a) {
        checkComponent(r);
        checkComponent(g);
        checkComponent(b);
        checkComponent(a);
        return ((a & UBYTE_MASK) << A_POS) | ((r & UBYTE_MASK) << R_POS)
                | ((g & UBYTE_MASK) << G_POS) | (b & UBYTE_MASK);
    }

    public static int compose(float r, float g, float b) {
        return compose(r, g, b, 1.0f);
    }

    public static int compose(float r, float g, float b, float a) {
        return compose(toUByte(r), toUByte(g), toUByte(b), toUByte(a));
    }

    public int getValue() {
        return value;
    }

    public int getA() {
        return (value >> A_POS) & UBYTE_MASK;
    }

    public int getR() {
        return (value >> R_POS) & UBYTE_MASK;
    }

    public int getG() {
        return (value >> G_POS) & UBYTE_MASK;
    }

    public int getB() {
        return (value) & UBYTE_MASK;
    }

    public void getComponents(float[] components) {
        components[0] = (float) getR() / 255.0f;
        components[1] = (float) getG() / 255.0f;
        components[2] = (float) getB() / 255.0f;
        components[3] = (float) getA() / 255.0f;
    }

    private static int toUByte(float component) {
        checkComponent(component);
        return (int) ((255.0f * component) + 0.5f);
    }

    public int getMin() {
        return Math.min(getR(), Math.min(getG(), getB()));
    }

    public int getMax() {
        return Math.max(getR(), Math.max(getG(), getB()));
    }

    public RGBA brighter(float factor) {
        return interpolate(WHITE, factor);
    }

    public RGBA darker(float factor) {
        return interpolate(BLACK, factor);
    }

    public RGBA interpolate(RGBA other, float factor) {
        if (other == null) {
            throw new IllegalArgumentException("other == null");
        }
        if (factor < 0.0f || factor > 1.0f) {
            throw new IllegalArgumentException("factor < 0.0f || factor > 1.0f");
        }
        if (factor == 0.0f) {
            return this;
        } else if (factor == 1.0f) {
            return other;
        }
        int r = Math.min(getR() + Math.round(factor * (float) (other.getR() - getR())), 255);
        int g = Math.min(getG() + Math.round(factor * (float) (other.getG() - getG())), 255);
        int b = Math.min(getB() + Math.round(factor * (float) (other.getB() - getB())), 255);
        int a = Math.min(getA() + Math.round(factor * (float) (other.getA() - getA())), 255);
        int value = compose(r, g, b, a);
        if (value == getValue()) {
            return this;
        }
        return new RGBA(r, g, b, a);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RGBA) {
            RGBA other = (RGBA) obj;
            return other.value == value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(16);
        sb.append(getR());
        sb.append(',');
        sb.append(getG());
        sb.append(',');
        sb.append(getB());
        if (getA() != 255) {
            sb.append(',');
            sb.append(getA());
        }
        return sb.toString();
    }

    public static RGBA parseRGBA(String s) throws ParseException {
        RGBA color;
        if (s.equalsIgnoreCase("black")) {
            color = RGBA.BLACK;
        } else if (s.equalsIgnoreCase("white")) {
            color = RGBA.WHITE;
        } else if (s.equalsIgnoreCase("yellow")) {
            color = RGBA.YELLOW;
        } else if (s.equalsIgnoreCase("orange")) {
            color = RGBA.ORANGE;
        } else if (s.equalsIgnoreCase("red")) {
            color = RGBA.RED;
        } else if (s.equalsIgnoreCase("green")) {
            color = RGBA.GREEN;
        } else if (s.equalsIgnoreCase("blue")) {
            color = RGBA.BLUE;
        } else if (s.equalsIgnoreCase("cyan")) {
            color = RGBA.CYAN;
        } else if (s.equalsIgnoreCase("magenta")) {
            color = RGBA.MAGENTA;
        } else if (s.equalsIgnoreCase("lightGray")) {
            color = RGBA.LIGHT_GRAY;
        } else if (s.equalsIgnoreCase("darkGray")) {
            color = RGBA.DARK_GRAY;
        } else if (s.equalsIgnoreCase("pink")) {
            color = RGBA.PINK;
        } else if (s.equalsIgnoreCase("transparent")) {
            color = RGBA.TRANSPARENT;
        } else {
            boolean ok = false;
            int r = 0, g = 0, b = 0, a = 255;
            int i1 = -1;
            int i2 = -1;
            try {
                i1 = 0;
                i2 = s.indexOf(',');
                if (i2 > 0) {
                    r = Integer.parseInt(s.substring(i1, i2).trim());
                    i1 = i2 + 1;
                    i2 = s.indexOf(',', i1);
                    if (i2 > 0) {
                        g = Integer.parseInt(s.substring(i1, i2).trim());
                        i1 = i2 + 1;
                        i2 = s.indexOf(',', i1);
                        if (i2 > 0) {
                            b = Integer.parseInt(s.substring(i1, i2).trim());
                            a = Integer.parseInt(s.substring(i2 + 1).trim());
                            ok = true;
                        } else {
                            b = Integer.parseInt(s.substring(i1).trim());
                            a = 255;
                            ok = true;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                throw new ParseException("Illegal color value", i1);
            }
            if (ok) {
                try {
                    color = new RGBA(r, g, b, a);
                } catch (Exception e) {
                    throw new ParseException("Illegal color value", -1);
                }
            } else {
                throw new ParseException("Illegal color value", -1);
            }
        }
        return color;
    }

    private static void checkComponent(int component) {
        if (component < 0 || component > 255) {
            throw new IllegalArgumentException("component out of bounds: "
                    + component);
        }
    }

    private static void checkComponent(float component) {
        if (component < 0.0f || component > 1.0f) {
            throw new IllegalArgumentException("component out of bounds: "
                    + component);
        }
    }
}
