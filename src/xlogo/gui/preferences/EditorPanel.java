package xlogo.gui.preferences;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.utils.GridBagPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class EditorPanel extends GridBagPanel {
    private static final int[] tokenTypes = new int[] {
            Token.COMMENT_EOL,
            Token.IDENTIFIER,
            Token.VARIABLE,
            Token.FUNCTION,
            Token.RESERVED_WORD,
            Token.OPERATOR,
            Token.SEPARATOR,
            Token.LITERAL_BOOLEAN,
            Token.LITERAL_NUMBER_FLOAT
    };
    private static final String[] tokenNames = new String[] {
            "Comment",
            "Identifier",
            "Variable",
            "Function",
            "Reserved Word",
            "Operator",
            "Separator",
            "Boolean Literal",
            "Number Literal"
    };

    private final RSyntaxTextArea preview;
    private JComboBox<String> fontCombo;
    private JComboBox<Integer> fontSizeCombo;

    private int tokenType;
    private JComboBox<String> tokenCombo;
    private JCheckBox tokenForegroundEnabled;
    private JButton tokenForegroundColor;
    private JCheckBox tokenBackgroundEnabled;
    private JButton tokenBackgroundColor;
    private JCheckBox tokenBold;
    private JCheckBox tokenItalic;
    private JCheckBox tokenUnderline;

    protected EditorPanel(Application ignoredApp) {
        super();
        gb.insets = new Insets(5, 5, 5, 5);

        preview = new RSyntaxTextArea(5, 80);
        preview.setText(Logo.getString("pref.highlight.example"));
        Logo.config.configureEditor(preview);
        preview.setEditable(false);

        gb.fill = GridBagConstraints.HORIZONTAL;
        gb.anchor = GridBagConstraints.LINE_START;
        gb.weighty = 0.0;
        addCol(0, 0, initOptionsPanel(), initThemePanel());

        gb.fill = GridBagConstraints.BOTH;
        gb.weighty = 1.0;
        add(0, 2, preview);
    }

    private boolean isSyntaxHighlightingEnabled() {
        return preview.getSyntaxEditingStyle().equals("text/logo");
    }

    private void setSyntaxHighlightingEnabled(boolean enabled) {
        if (enabled) {
            preview.setSyntaxEditingStyle("text/logo");
        } else {
            preview.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        }
        preview.repaint();
    }

    private interface BooleanSetter {
        void setBoolean(boolean value);
    }

    private interface BooleanGetter {
        boolean getBoolean();
    }

    private JCheckBox createCheckBox(String label, BooleanGetter getter, BooleanSetter setter) {
        var cb = new JCheckBox(label);
        cb.setSelected(getter.getBoolean());
        cb.addActionListener(e -> {
            setter.setBoolean(cb.isSelected());
            preview.repaint();
        });
        return cb;
    }

    private interface ColorSetter {
        void setColor(Color value);
    }

    private interface ColorGetter {
        Color getColor();
    }

    private JButton createColorButton(ColorGetter getter, ColorSetter setter) {
        var button = new JButton("  ");
        button.setBackground(getter.getColor());
        button.addActionListener(e -> {
            var color = JColorChooser.showDialog(this, "", button.getBackground());
            if (color != null) {
                button.setBackground(color);
                setter.setColor(color);
                preview.repaint();
            }
        });
        return button;
    }

    private interface IntegerSetter {
        void setInteger(Integer value);
    }

    private interface IntegerGetter {
        Integer getInteger();
    }

    private JTextField createIntegerField(IntegerGetter getter, IntegerSetter setter) {
        var field = new JTextField();
        field.setText(Integer.toString(getter.getInteger()));
        field.addActionListener(e -> {
            try {
                setter.setInteger(Integer.parseInt(field.getText()));
                preview.repaint();
            } catch (NumberFormatException ignored) {
            }
        });
        return field;
    }

    private void setFont() {
        var fontName = (String) fontCombo.getSelectedItem();
        var fontSize = (int) Objects.requireNonNull(fontSizeCombo.getSelectedItem());
        preview.setFont(new Font(fontName, Font.PLAIN, fontSize));
        var scheme = preview.getSyntaxScheme();
        int i = 0;
        for (var style : scheme.getStyles()) {
            if (style.font != null) {
                style.font = new Font(fontName, style.font.getStyle(), fontSize);
                scheme.setStyle(i++, style);
            }
        }
        preview.setSyntaxScheme(scheme);
        preview.repaint();
    }

    private JPanel initThemePanel() {
        var fontLabel = new JLabel("Font Family: ");
        fontCombo = new JComboBox<>();
        var fontNames = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontNames.forEach(f -> fontCombo.addItem(f));
        fontCombo.setSelectedItem(preview.getFont().getFontName());

        var fontSizeLabel = new JLabel("Font Size: ");
        var fontSizes = Arrays.asList(8, 9, 10, 11, 12, 13, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72);
        fontSizeCombo = new JComboBox<>();
        fontSizes.forEach(s -> fontSizeCombo.addItem(s));
        fontSizeCombo.setSelectedItem(preview.getFont().getSize());
        fontSizeCombo.addActionListener(e -> setFont());
        fontCombo.addActionListener(e -> setFont());

        var foregroundLabel = new JLabel("Foreground: ");
        var foregroundColor = createColorButton(preview::getForeground, preview::setForeground);
        var backgroundLabel = new JLabel("Background: ");
        var backgroundColor = createColorButton(preview::getBackground, preview::setBackground);

        var tabLineLabel = new JLabel("Tab Line: ");
        var tabLineColor = createColorButton(preview::getTabLineColor, preview::setTabLineColor);

        var marginLineLabel = new JLabel("Margin Line: ");
        var marginLineColor = createColorButton(preview::getMarginLineColor, preview::setMarginLineColor);

        var currentLineLabel = new JLabel("Current Line: ");
        var currentLineColor = createColorButton(preview::getCurrentLineHighlightColor, preview::setCurrentLineHighlightColor);

        var bracketBorderColor = createColorButton(preview::getMatchedBracketBorderColor, preview::setMatchedBracketBorderColor);
        var bracketBackgroundLabel = new JLabel("Bracket BG: ");
        var bracketBackgroundColor = createColorButton(preview::getMatchedBracketBGColor, preview::setMatchedBracketBGColor);
        var bracketBorderLabel = new JLabel("Bracket Border: ");

        var tokenLabel = new JLabel("Token Type: ");
        tokenCombo = new JComboBox<>(tokenNames);
        tokenCombo.addActionListener(e -> updateTokenType());

        tokenForegroundEnabled = new JCheckBox("Foreground:");
        tokenForegroundEnabled.addActionListener(e -> setTokenColorEnabled(true));
        tokenForegroundColor = new JButton("  ");
        tokenForegroundColor.addActionListener(e -> setTokenColor(true));

        tokenBackgroundEnabled = new JCheckBox("Background:");
        tokenBackgroundEnabled.addActionListener(e -> setTokenColorEnabled(false));
        tokenBackgroundColor = new JButton("  ");
        tokenBackgroundColor.addActionListener(e -> setTokenColor(false));

        tokenBold = new JCheckBox("Bold");
        tokenBold.addActionListener(e -> setTokenFontStyle(Font.BOLD));

        tokenItalic = new JCheckBox("Italic");
        tokenItalic.addActionListener(e -> setTokenFontStyle(Font.ITALIC));

        tokenUnderline = new JCheckBox("Underline");
        tokenUnderline.addActionListener(e -> setTokenUnderline());

        updateTokenType();

        var p = new GridBagPanel();
        p.setBorder(new TitledBorder("Theme"));
        p.gb.insets = new Insets(2, 3, 2, 3);
        p.gb.anchor = GridBagConstraints.LINE_START;
        p.gb.fill = GridBagConstraints.NONE;

        p.addCol(0, 0, fontLabel, fontSizeLabel, foregroundLabel, bracketBorderLabel, tabLineLabel);
        p.addCol(1, 1, fontSizeCombo, foregroundColor, bracketBorderColor, tabLineColor);
        p.addCol(2, 1, currentLineLabel, backgroundLabel, bracketBackgroundLabel, marginLineLabel);
        p.addCol(3, 1, currentLineColor, backgroundColor, bracketBackgroundColor, marginLineColor);
        p.addCol(5, 0, tokenLabel, tokenForegroundEnabled, tokenBackgroundEnabled);
        p.addCol(6, 1, tokenForegroundColor, tokenBackgroundColor);
        p.addCol(7, 1, tokenBold, tokenItalic, tokenUnderline);

        p.setSpan(3, 1, GridBagConstraints.HORIZONTAL);
        p.add(1, 0, fontCombo);
        p.setSpan(2, 1);
        p.add(6, 0, tokenCombo);

        p.setSpan(1, 5, GridBagConstraints.VERTICAL);
        p.add(4, 0, new JSeparator(SwingConstants.VERTICAL));

        return p;
    }

    private JPanel initOptionsPanel() {
        var tabSizeLabel = new JLabel("Tab size: ");
        var tabSize = createIntegerField(preview::getTabSize, preview::setTabSize);
        var tabsEmulated = createCheckBox("Use spaces for tabs", preview::getTabsEmulated, preview::setTabsEmulated);
        var marginPositionLabel = new JLabel("Margin Position: ");
        var marginPosition = createIntegerField(preview::getMarginLinePosition, preview::setMarginLinePosition);
        var syntaxHighlighting = createCheckBox("Syntax highlighting", this::isSyntaxHighlightingEnabled, this::setSyntaxHighlightingEnabled);
        var highlightCurrentLine = createCheckBox("Highlight current line", preview::getHighlightCurrentLine, preview::setHighlightCurrentLine);
        var bracketMatchingEnabled = createCheckBox("Bracket matching", preview::isBracketMatchingEnabled, preview::setBracketMatchingEnabled);
        var hyperlinksEnabled = createCheckBox("Clickable hyperlinks", preview::getHyperlinksEnabled, preview::setHyperlinksEnabled);
        var antiAliasingEnabled = createCheckBox("Anti-aliasing", preview::getAntiAliasingEnabled, preview::setAntiAliasingEnabled);
        var fractionalFontEnabled = createCheckBox("Fractional font metrics", preview::getFractionalFontMetricsEnabled, preview::setFractionalFontMetricsEnabled);

        var tabLineEnabled = createCheckBox("Indent guides", preview::getPaintTabLines, preview::setPaintTabLines);
        var marginLineEnabled = createCheckBox("Draw margin", preview::isMarginLineEnabled, preview::setMarginLineEnabled);
        var visibleWhitespace = createCheckBox("Visible whitespace", preview::isWhitespaceVisible, preview::setWhitespaceVisible);
        var eolMarkers = createCheckBox("End of line markers", preview::getEOLMarkersVisible, preview::setEOLMarkersVisible);

        var p = new GridBagPanel();
        p.setBorder(new TitledBorder("Options"));
        p.gb.anchor = GridBagConstraints.LINE_START;
        p.gb.fill = GridBagConstraints.NONE;
        p.gb.insets = new Insets(2, 3, 2, 3);

        p.addRow(4, 0, tabSizeLabel, tabSize);
        p.addRow(7, 2, marginPositionLabel, marginPosition);
        p.addCol(0, 0, syntaxHighlighting, highlightCurrentLine, bracketMatchingEnabled);
        p.addCol(2, 0, antiAliasingEnabled, fractionalFontEnabled, hyperlinksEnabled);

        p.setSpan(2, 1);
        p.addCol(4, 1, tabsEmulated, tabLineEnabled, visibleWhitespace);
        p.addCol(7, 0, eolMarkers, marginLineEnabled);

        p.setSpan(1, 4, GridBagConstraints.VERTICAL);
        p.add(1,0, new JSeparator(SwingConstants.VERTICAL));
        p.add(3,0, new JSeparator(SwingConstants.VERTICAL));
        p.add(6,0, new JSeparator(SwingConstants.VERTICAL));

        return p;
    }

    private void updateTokenType() {
        tokenType = tokenTypes[tokenCombo.getSelectedIndex()];
        var style = preview.getSyntaxScheme().getStyle(tokenType);
        if (style.foreground == null) {
            tokenForegroundEnabled.setSelected(false);
            tokenForegroundColor.setEnabled(false);
        } else {
            tokenForegroundEnabled.setSelected(true);
            tokenForegroundColor.setEnabled(true);
            tokenForegroundColor.setBackground(style.foreground);
        }
        if (style.background == null) {
            tokenBackgroundEnabled.setSelected(false);
            tokenBackgroundColor.setEnabled(false);
        } else {
            tokenBackgroundEnabled.setSelected(true);
            tokenBackgroundColor.setEnabled(true);
            tokenBackgroundColor.setBackground(style.background);
        }
        var font = style.font != null ? style.font : preview.getFont();
        tokenBold.setSelected((font.getStyle() & Font.BOLD) != 0);
        tokenItalic.setSelected((font.getStyle() & Font.ITALIC) != 0);
        tokenUnderline.setSelected(style.underline);
    }

    private Style getTokenStyle() {
        return preview.getSyntaxScheme().getStyle(tokenType);
    }

    private void setTokenStyle(Style style) {
        var scheme = preview.getSyntaxScheme();
        scheme.setStyle(tokenType, style);
        preview.setSyntaxScheme(scheme);
        preview.repaint();
    }

    private void setTokenColorEnabled(boolean foreground) {
        var style = getTokenStyle();
        var checkbox = foreground ? tokenForegroundEnabled : tokenBackgroundEnabled;
        var button = foreground ? tokenForegroundColor : tokenBackgroundColor;
        var color = checkbox.isSelected() ? button.getBackground() : null;
        button.setEnabled(checkbox.isSelected());
        if (foreground) {
            style.foreground = color;
        } else {
            style.background = color;
        }
        setTokenStyle(style);
    }

    private void setTokenColor(boolean foreground) {
        var style = getTokenStyle();
        var button = foreground ? tokenForegroundColor : tokenBackgroundColor;
        var color = JColorChooser.showDialog(this, "", button.getBackground());
        if (color != null) {
            button.setBackground(color);
            if (foreground) {
                style.foreground = color;
            } else {
                style.background = color;
            }
        }
        setTokenStyle(style);
    }

    private void setTokenFontStyle(int fontStyle) {
        var style = getTokenStyle();
        var checkbox = fontStyle == Font.BOLD ? tokenBold : tokenItalic;
        var sourceFont = style.font != null ? style.font : preview.getFont();
        if (checkbox.isSelected()) {
            style.font = sourceFont.deriveFont(sourceFont.getStyle() | fontStyle);
        } else {
            style.font = sourceFont.deriveFont(sourceFont.getStyle() & ~fontStyle);
        }
        setTokenStyle(style);
    }

    private void setTokenUnderline() {
        var style = getTokenStyle();
        style.underline = tokenUnderline.isSelected();
        setTokenStyle(style);
    }

    protected void update() {
        Logo.config.saveEditorConfiguration(preview);
    }
}
