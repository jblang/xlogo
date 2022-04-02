package xlogo;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Language {
    FRENCH("Francais", "fr", "FR", "pref.general.french"),
    ENGLISH("English", "en", "US", "pref.general.english"),
    ARABIC("عربية", "ar", "MA", "pref.general.arabic"),
    SPANISH("Español", "es", "ES", "pref.general.spanish"),
    PORTUGUESE("Português", "pt", "BR", "pref.general.portuguese"),
    ESPERANTO("Esperanto", "eo", "EO", "pref.general.esperanto"),
    GERMAN("Deutsch", "de", "DE", "pref.general.german"),
    GALICIAN("Galego", "gl", "ES", "pref.general.galician"),
    ASTURIAN("Asturianu", "as", "ES", "pref.general.asturian"),
    GREEK("Ελληνικά", "el", "GR", "pref.general.greek"),
    ITALIAN("Italiano", "it", "IT", "pref.general.italian"),
    CATALAN("Català", "ca", "ES", "pref.general.catalan"),
    HUNGARIAN("Magyar", "hu", "HU", "pref.general.hungarian");

    public final String key;
    public final String name;
    public final String code;
    public final Locale locale;
    public final ResourceBundle messages;

    /**
     * The selected language
     */
    Language(String name, String code, String country, String key) {
        this.key = key;
        this.name = name;
        this.code = code;
        this.locale = new Locale(code, country);
        this.messages = Logo.getLanguageBundle(locale);
    }

    public static Language byIndex(int id) {
        var languages = Language.values();
        return id < languages.length ? languages[id] : Language.ENGLISH;
    }

    public FlatSVGIcon getFlag() {
        var res = Logo.class.getResource("resources/flags/" + this.code + ".svg");
        if (res == null)
            return null;
        else
            return new FlatSVGIcon(res);
    }
}
