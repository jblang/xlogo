package xlogo.resources;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import xlogo.Logo;

import javax.swing.*;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ResourceLoader {
    public static ImageIcon getIcon(String name) {
        var res = ResourceLoader.class.getResource("icons/" + name + ".svg");
        if (res == null)
            return null;
        else
            return new FlatSVGIcon(res);
    }

    public static FlatSVGIcon getFlag(String name) {
        var res = ResourceLoader.class.getResource("flags/" + name + ".svg");
        if (res == null)
            return null;
        else
            return new FlatSVGIcon(res);
    }

    public static FlatSVGIcon getFlag(int i) {
        return getFlag(Logo.locales[i]);
    }

    public static ImageIcon getAppIcon() {
        return new ImageIcon(Objects.requireNonNull(ResourceLoader.class.getResource("appicon.png")));
    }

    public static FlatSVGIcon getTurtle(int i) {
        var res = ResourceLoader.class.getResource("turtles/turtle" + i + ".svg");
        if (res == null)
            return null;
        else
            return new FlatSVGIcon(res);
    }

    public static ResourceBundle getLanguageBundle(Locale locale) {
        return ResourceBundle.getBundle("xlogo.resources.language", locale);
    }

    public static ResourceBundle getPrimitiveBundle(Locale locale) {
        return ResourceBundle.getBundle("xlogo.resources.primitives", locale);
    }
}
