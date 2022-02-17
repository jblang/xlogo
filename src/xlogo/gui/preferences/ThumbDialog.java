package xlogo.gui.preferences;

import xlogo.Config;
import xlogo.Logo;
import xlogo.gui.MessageTextArea;
import xlogo.utils.ExtensionFilter;
import xlogo.utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class ThumbDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    Color col;
    JPanel mainPanel;
    BorderImagePanel bip;
    private final HashMap<String, String> map = new HashMap<String, String>();
    private final Vector<JToggleButton> toggleButtons = new Vector<JToggleButton>();
    private JScrollPane js;
    private JButton add, ok, remove;
    private final ButtonGroup bg = new ButtonGroup();
    private int columns;
    private ColorThumbPanel pc;
    private final ArrayList<String> externalImages;

    ThumbDialog(BorderImagePanel bip) {
        super((JDialog) (bip.getTopLevelAncestor()));
        this.bip = bip;
        this.externalImages = bip.getExternalImages();
        initGui();

    }

    private BufferedImage scale(String path, boolean internal) throws Exception {
        BufferedImage bi = null;
        if (internal) bi = ImageIO.read(Utils.class.getResource(path));
        else {
            File f = new File(path);
            bi = ImageIO.read(f);

        }
        double scaleValue = 50.0d / bi.getHeight();
        int width = (int) (bi.getWidth() * scaleValue);
        int height = (int) (bi.getHeight() * scaleValue);
        Image im = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(im, 0, 0, null);
        g.dispose();
        return (bufferedImage);
    }

    private void createToggle(ImageIcon ic, int i) {
        toggleButtons.add(new JToggleButton(ic));
        int row = i % columns;
        int col = i / columns;
        mainPanel.add(getButton(i), new GridBagConstraints(row, col, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        bg.add(getButton(i));
        getButton(i).addActionListener(this);
        getButton(i).setActionCommand("Button" + i);
    }

    protected void updateFirstButton(Color c) {
        BufferedImage im = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics g = im.createGraphics();
        g.setPaintMode();
        g.setColor(c);
        g.fillRect(0, 0, 50, 50);
        ImageIcon ic = new ImageIcon(im);
        g.dispose();
        getButton(0).setIcon(ic);
        col = c;
    }

    private void initMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        columns = (int) (Math.sqrt(Config.borderInternalImage.length + externalImages.size() + 1) + .5);
        // Add the first button for uniform color
        toggleButtons.add(new JToggleButton());
        col = Config.borderColor;
        if (null == col) col = new java.awt.Color(218, 176, 130);
        updateFirstButton(col);
        bg.add(getButton(0));
        if (null != Config.borderColor) getButton(0).setSelected(true);
        mainPanel.add(getButton(0), new GridBagConstraints(0, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        getButton(0).addActionListener(this);
        getButton(0).setActionCommand("FirstButton");
        // Add buttons from Internal Images
        for (int i = 1; i < Config.borderInternalImage.length + 1; i++) {
            try {
                ImageIcon ic = new ImageIcon(scale(Config.borderInternalImage[i - 1], true));
                this.createToggle(ic, i);
                if (Config.borderInternalImage[i - 1].equals(Config.borderImageSelected) && null == Config.borderColor) {
                    getButton(i).setSelected(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Check for all External Images
        for (int i = 0; i < externalImages.size(); i++) {
            String name = externalImages.get(i);
            if (!Utils.fileExists(name)) {
                externalImages.remove(i);
                i--;
            }
        }

        // Add buttons from External Images
        for (int i = Config.borderInternalImage.length + 1; i < externalImages.size() + Config.borderInternalImage.length + 1; i++) {
            try {
                String name = externalImages.get(i - Config.borderInternalImage.length - 1);
                ImageIcon ic = new ImageIcon(scale(name, false));
                this.createToggle(ic, i);
                if (name.equals(Config.borderImageSelected) && null == Config.borderColor) {
                    getButton(i).setSelected(true);
                }
                map.put(String.valueOf(i), name);
            } catch (Exception e) {
            }
        }
        js = new JScrollPane(mainPanel);
    }

    private void initGui() {
        setSize(500, 400);
        initMainPanel();
        getContentPane().setLayout(new GridBagLayout());
        add = new JButton(Logo.messages.getString("ajouter"));
        remove = new JButton(Logo.messages.getString("enlever"));
        ok = new JButton(Logo.messages.getString("pref.ok"));
        add.addActionListener(this);
        ok.addActionListener(this);
        remove.addActionListener(this);
        add.setActionCommand("add");
        remove.setActionCommand("remove");
        ok.setActionCommand("ok");
        pc = new ColorThumbPanel(col, this);
        pc.setEnabled(getButton(0).isSelected());
        TitledBorder tb = BorderFactory.createTitledBorder(Logo.messages.getString("uniform_color"));
        pc.setBorder(tb);
        setTitle(Logo.messages.getString("bordermotif"));
        TitledBorder tb2 = BorderFactory.createTitledBorder(Logo.messages.getString("images"));
        js.setBorder(tb2);
        getContentPane().add(js, new GridBagConstraints(0, 0, 4, 3, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        getContentPane().add(add, new GridBagConstraints(4, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0));
        getContentPane().add(remove, new GridBagConstraints(4, 1, 1, 1, 1.0,
                1.0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0));
        getContentPane().add(pc, new GridBagConstraints(0, 3, 2, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        getContentPane().add(ok, new GridBagConstraints(3, 3, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0));
        setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("add")) {
            JFileChooser jf = new JFileChooser();
            String[] ext = {".png", ".jpg"};
            jf.addChoosableFileFilter(new ExtensionFilter(Logo.messages.getString("imagefile"),
                    ext));
            int retval = jf.showDialog(this, Logo.messages.getString("ajouter"));
            if (retval == JFileChooser.APPROVE_OPTION) {
                String path = jf.getSelectedFile().getPath();
                String copie_path = path.toLowerCase();
                if (copie_path.endsWith(".jpg") || copie_path.endsWith(".png")) {
                    if (!externalImages.contains(path)) {
                        try {
                            File f = new File(path);
                            if (f.length() > 102400) {
                                MessageTextArea jt = new MessageTextArea(Logo.messages.getString("file_100K"));
                                JOptionPane.showMessageDialog(this, jt, Logo.messages.getString("erreur"), JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            ImageIcon ic = new ImageIcon(scale(path, false));
                            toggleButtons.add(new JToggleButton(ic));
                            int i = toggleButtons.size() - 1;
                            int row = i % columns;
                            int col = i / columns;
                            mainPanel.add(getButton(i), new GridBagConstraints(row, col, 1, 1, 1.0,
                                    1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                    new Insets(5, 5, 5, 5), 0, 0));
                            mainPanel.validate();
                            mainPanel.revalidate();
                            bg.add(getButton(i));
                            getButton(i).addActionListener(this);
                            getButton(i).setActionCommand("Button" + i);
                            externalImages.add(path);
                            map.put(String.valueOf(i), path);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }

            }
        } else if (cmd.equals("remove")) {
            int id = getSelectedButton();
            if (id > Config.borderInternalImage.length) {
                String key = String.valueOf(id);
                String name = map.get(key);
                map.remove(key);
                for (int i = id; i < Config.borderInternalImage.length + externalImages.size(); i++) {
                    map.put(String.valueOf(id), map.get(String.valueOf(i + 1)));
                }
                map.remove(String.valueOf(Config.borderInternalImage.length + externalImages.size() - 1));

                externalImages.remove(name);
                mainPanel.remove(getButton(id));
                toggleButtons.remove(id);
                mainPanel.validate();
                mainPanel.repaint();
            }
        } else if (cmd.equals("ok")) {
            if (getButton(0).isSelected()) {
                bip.setBorderColor(col);
            } else {
                bip.setBorderColor(null);
                int id = getSelectedButton();
                if (id == -1) id = 1;
                if (id <= Config.borderInternalImage.length)
                    bip.setPath(Config.borderInternalImage[id - 1]);
                else
                    bip.setPath(externalImages.get(id - 1 - Config.borderInternalImage.length));
            }
            bip.updatePreviewBorderImage();
            dispose();
        } else if (cmd.equals("FirstButton")) {
            pc.setEnabled(true);
            remove.setEnabled(false);
        } else {
            pc.setEnabled(false);
            int id = getSelectedButton();
            remove.setEnabled(id > Config.borderInternalImage.length);
        }

    }

    private JToggleButton getButton(int id) {
        return toggleButtons.get(id);
    }

    private int getSelectedButton() {
        int id = -1;
        for (int i = 0; i < toggleButtons.size(); i++) {
            if (getButton(i).isSelected()) {
                id = i;
                break;
            }
        }
        return id;
    }
}
