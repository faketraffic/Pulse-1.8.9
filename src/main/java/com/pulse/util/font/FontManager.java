package com.pulse.util.font;

import java.awt.*;

public class FontManager {

    private static FontManager instance;

    private CustomFont normal;
    private CustomFont bold;
    private CustomFont small;
    private CustomFont large;

    public void init() {
        normal = new CustomFont("Verdana", Font.PLAIN, 9);
        bold   = new CustomFont("Verdana", Font.BOLD, 9);
        small  = new CustomFont("Verdana", Font.PLAIN, 7);
        large  = new CustomFont("Verdana", Font.BOLD, 11);

        normal.build();
        bold.build();
        small.build();
        large.build();
    }

    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }

    public CustomFont getNormal() { return normal; }
    public CustomFont getBold()   { return bold; }
    public CustomFont getSmall()  { return small; }
    public CustomFont getLarge()   { return large; }
}
