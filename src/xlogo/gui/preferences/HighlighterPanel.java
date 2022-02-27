package xlogo.gui.preferences;

import xlogo.gui.Application;
import xlogo.Logo;
import xlogo.document.LogoDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class HighlighterPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final Application cadre;
    private final SyntaxStylePanel p_comment;
    private final SyntaxStylePanel p_parenthese;
    private final SyntaxStylePanel p_primitive;
    private final SyntaxStylePanel p_operande;
    private final GridBagLayout gb = new GridBagLayout();
    private final JTextPane jt = new JTextPane();
    private final LogoDocument dsd;
    private final JCheckBox active_coloration = new JCheckBox();
    private final JLabel l_active = new JLabel();
    private final JButton bdefaut = new JButton();

    protected HighlighterPanel(Application cadre) {
        this.cadre = cadre;
        l_active.setText(Logo.messages.getString("pref.highlight.enabled"));
        bdefaut.setText(Logo.messages.getString("pref.highlight.init"));
        setLayout(gb);
        jt.setOpaque(true);
        dsd = new LogoDocument();
        jt.setDocument(dsd);
        dsd.setColore_Parenthese(true);
        p_comment = new SyntaxStylePanel(Logo.config.getSyntaxCommentColor(), Logo.config.getSyntaxCommentStyle(), Logo.messages.getString("pref.highlight.comment"), this);
        p_parenthese = new SyntaxStylePanel(Logo.config.getSyntaxBracketColor(), Logo.config.getSyntaxBracketStyle(), Logo.messages.getString("pref.highlight.parenthesis"), this);
        p_primitive = new SyntaxStylePanel(Logo.config.getSyntaxPrimitiveColor(), Logo.config.getSyntaxPrimitiveStyle(), Logo.messages.getString("pref.highlight.primitive"), this);
        p_operande = new SyntaxStylePanel(Logo.config.getSyntaxOperandColor(), Logo.config.getSyntaxOperandStyle(), Logo.messages.getString("pref.highlight.operand"), this);
        jt.setText(Logo.messages.getString("pref.highlight.example"));
        if (Logo.config.isSyntaxHighlightingEnabled()) active_coloration.setSelected(true);
        else {
            active_coloration.setSelected(false);
            p_comment.setEnabled(false);
            p_primitive.setEnabled(false);
            p_parenthese.setEnabled(false);
            p_operande.setEnabled(false);
            bdefaut.setEnabled(false);
        }
        add(active_coloration, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(
                5, 10, 5, 10), 0, 0));
        add(l_active, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                5, 10, 5, 10), 0, 0));
        add(p_comment, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                5, 10, 5, 10), 0, 0));
        add(p_primitive, new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                5, 10, 5, 10), 0, 0));
        add(p_operande, new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                5, 10, 5, 10), 0, 0));
        add(p_parenthese, new GridBagConstraints(0, 4, 3, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                5, 10, 5, 10), 0, 0));
        add(jt, new GridBagConstraints(0, 5, 3, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                10, 10, 10, 10), 0, 0));
        add(bdefaut, new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                10, 10, 10, 10), 0, 0));
        active_coloration.addActionListener(this);
        active_coloration.setActionCommand("checkbox");
        bdefaut.addActionListener(this);
        bdefaut.setActionCommand("bouton");
    }

    protected void update() {

        if (active_coloration.isSelected()) {
            if (!Logo.config.isSyntaxHighlightingEnabled()) {
                Logo.config.setSyntaxHighlightingEnabled(true);
            }
            Logo.config.setSyntaxCommentColor(p_comment.color());
            Logo.config.setSyntaxOperandColor(p_operande.color());
            Logo.config.setSyntaxPrimitiveColor(p_primitive.color());
            Logo.config.setSyntaxBracketColor(p_parenthese.color());
            Logo.config.setSyntaxCommentStyle(p_comment.style());
            Logo.config.setSyntaxOperandStyle(p_operande.style());
            Logo.config.setSyntaxPrimitiveStyle(p_primitive.style());
            Logo.config.setSyntaxBracketStyle(p_parenthese.style());

            // On attribue les styles sélectionnés à l'éditeur
            cadre.changeSyntaxHighlightingStyle();
        } else {
            if (Logo.config.isSyntaxHighlightingEnabled()) {
                Logo.config.setSyntaxHighlightingEnabled(false);
            }
        }
        cadre.setSyntaxHighlightingEnabled(Logo.config.isSyntaxHighlightingEnabled());
        cadre.resizeCommandLine();
        cadre.validate();
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("checkbox")) {
            if (p_comment.isEnabled()) {
                p_comment.setEnabled(false);
                p_primitive.setEnabled(false);
                p_parenthese.setEnabled(false);
                p_operande.setEnabled(false);
                bdefaut.setEnabled(false);
                dsd.setColoration(false);
                jt.setText(Logo.messages.getString("pref.highlight.example"));
            } else {
                p_comment.setEnabled(true);
                p_primitive.setEnabled(true);
                p_parenthese.setEnabled(true);
                p_operande.setEnabled(true);
                bdefaut.setEnabled(true);
                dsd.setColoration(true);
                rafraichis_texte();
            }
        } else if (cmd.equals("bouton")) {
            p_comment.setColorAndStyle(Color.GRAY.getRGB(), Font.PLAIN);
            p_parenthese.setColorAndStyle(Color.RED.getRGB(), Font.BOLD);
            p_primitive.setColorAndStyle(new Color(0, 128, 0).getRGB(), Font.PLAIN);
            p_operande.setColorAndStyle(Color.BLUE.getRGB(), Font.PLAIN);
            rafraichis_texte();
        }

    }

    protected void rafraichis_texte() {
        dsd.initStyles(p_comment.color(), p_comment.style(), p_primitive.color(), p_primitive.style(),
                p_parenthese.color(), p_parenthese.style(), p_operande.color(), p_operande.style());
        jt.setText(Logo.messages.getString("pref.highlight.example"));
        dsd.initStyles(Logo.config.getSyntaxCommentColor(), Logo.config.getSyntaxCommentStyle(), Logo.config.getSyntaxPrimitiveColor(), Logo.config.getSyntaxPrimitiveStyle(),
                Logo.config.getSyntaxBracketColor(), Logo.config.getSyntaxBracketStyle(), Logo.config.getSyntaxOperandColor(), Logo.config.getSyntaxOperandStyle());

    }
}
