package xlogo.gui;

public interface Searchable {
    boolean find(String element, boolean forward);

    void replace(String element, boolean forward);

    void replaceAll(String element, String substitute);

    void removeHighlight();
}
