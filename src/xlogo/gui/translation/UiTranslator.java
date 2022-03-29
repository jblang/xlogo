package xlogo.gui.translation;

import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.gui.SearchFrame;
import xlogo.resources.ResourceLoader;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

public class UiTranslator extends JFrame implements ActionListener {
    protected static final String OK = "ok";
    protected static final String SEND = "send";
    protected static final String SEARCH = "search";
    protected static final String CONSULT = "0";
    protected static final String MODIFY = "1";
    protected static final String CREATE = "2";
    protected static final String COMPLETE = "3";
    private String id = "";
    private String action;
    private final Application app;
    private FirstPanel first;
    private BottomPanel bottom;

    private SearchFrame sf = null;

    public UiTranslator(Application app) {
        this.app = app;
        initGui();
    }

    private void initGui() {
        setIconImage(ResourceLoader.getAppIcon().getImage());
        setTitle(Logo.messages.getString("menu.help.translatexlogo"));
        first = new FirstPanel(this);
        getContentPane().add(first);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case UiTranslator.OK:
                action = first.getAction();

                if (null == action) return;
                else if (action.equals(UiTranslator.MODIFY)) id = first.getLang();
                else if (action.equals(UiTranslator.COMPLETE)) id = first.getLang();
                remove(first);
                bottom = new BottomPanel(this, action, id);
                getContentPane().setLayout(new BorderLayout());
                if (!action.equals(UiTranslator.CONSULT)) {
                    TopPanel top = new TopPanel(this);
                    getContentPane().add(top, BorderLayout.NORTH);

                }

                getContentPane().add(bottom, BorderLayout.CENTER);
                this.getContentPane().validate();
                break;
            case UiTranslator.SEND:
                String path = "";
                JFileChooser jf = new JFileChooser(Utils.unescapeString(Logo.config.getDefaultFolder()));
                int retval = jf.showDialog(this, Logo.messages
                        .getString("menu.file.save"));
                if (retval == JFileChooser.APPROVE_OPTION) {
                    path = jf.getSelectedFile().getPath();
                    StringBuffer sb = new StringBuffer();
                    try {
                        Locale locale = null;
                        if (action.equals(UiTranslator.CREATE)) {
                            locale = Logo.getLocale(0);
                        } else if (!action.equals(UiTranslator.CONSULT)) {
                            locale = Logo.getLocale(Integer.parseInt(id));
                        }
                        Vector<String> v = bottom.getPrimitiveTable().getKeys();
                        ResourceBundle rb = ResourceLoader.getPrimitiveBundle(locale);
                        for (int i = 0; i < v.size(); i++) {
                            String key = v.get(i);
                            if (action.equals(UiTranslator.CREATE)) {
                                writeLine(sb, key, bottom.getPrimValue(i, 0));
                            } else if (!action.equals(UiTranslator.CONSULT)) {
                                String element = bottom.getPrimValue(i, Integer.parseInt(id));
                                //				System.out.println(element+" clé "+key);
                                if (!rb.getString(key).equals(element)) writeLine(sb, key, element);
                            }
                        }
                        sb.append("\n---------------------------------------\n");
                        v = bottom.getMessageTable().getKeys();
                        rb = ResourceLoader.getLanguageBundle(locale);
                        for (int i = 0; i < v.size(); i++) {
                            String key = v.get(i);
                            if (action.equals(UiTranslator.CREATE)) {
                                writeLine(sb, key, bottom.getMessageValue(i, 0).replaceAll("\\n", "\\\\n"));
                            } else if (!action.equals(UiTranslator.CONSULT)) {
                                String element = bottom.getMessageValue(i, Integer.parseInt(id));
                                if (!rb.getString(key).equals(element))
                                    writeLine(sb, key, element.replaceAll("\\n", "\\\\n"));
                            }
                        }
                        Utils.writeLogoFile(path, sb.toString());
                    } catch (NullPointerException e3) {
                        System.out.println("annulation");
                    }
                    catch (IOException e2) {
                        app.updateHistory("erreur", Logo.messages.getString("error.ioecriture"));
                    }
                }
                break;
            case UiTranslator.SEARCH:
                if (null == sf) {
                    sf = new SearchFrame(this, bottom.getVisibleTable());
                    sf.setSize(350, 350);
                    sf.setVisible(true);
                }
                break;
        }
    }

    protected void resetSearchFrame() {
        sf = null;
    }

    private void writeLine(StringBuffer sb, String key, String value) {
        sb.append(key);
        sb.append("=");
        sb.append(value);
        sb.append("\n");

    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            app.closeUiTranslator();
        }

    }
}
