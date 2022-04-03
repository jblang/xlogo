package xlogo;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Language {
    FRENCH("Francais", "fr", "FR", "language.french"),
    ENGLISH("English", "en", "US", "language.english"),
    ARABIC("عربية", "ar", "MA", "language.arabic"),
    SPANISH("Español", "es", "ES", "language.spanish"),
    PORTUGUESE("Português", "pt", "BR", "language.portuguese"),
    ESPERANTO("Esperanto", "eo", "EO", "language.esperanto"),
    GERMAN("Deutsch", "de", "DE", "language.german"),
    GALICIAN("Galego", "gl", "ES", "language.galician"),
    ASTURIAN("Asturianu", "as", "ES", "language.asturian"),
    GREEK("Ελληνικά", "el", "GR", "language.greek"),
    ITALIAN("Italiano", "it", "IT", "language.italian"),
    CATALAN("Català", "ca", "ES", "language.catalan"),
    HUNGARIAN("Magyar", "hu", "HU", "language.hungarian");

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
