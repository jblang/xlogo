package xlogo.gui.preferences;

import xlogo.Config;
import xlogo.Logo;
import xlogo.utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BorderImagePanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private Color borderColor = Config.borderColor;
    private String path = Config.borderImageSelected;
    private final ArrayList<String> externalImages;
    private final JLabel labelBorderMotif = new JLabel(Logo.messages.getString("bordermotif"));
    private final JButton button = new JButton(Logo.messages.getString("pref.highlight.other"));
    private Thumbnail previewPanel;
    private ThumbFrame frame = null;

    BorderImagePanel() {
        externalImages = new ArrayList<String>(Config.borderExternalImage);
        initGui();
    }

    protected ArrayList<String> getExternalImages() {
        return externalImages;
    }

    protected String getPath() {
        return path;
    }

    protected void setPath(String st) {
        path = st;
    }

    protected Color getBorderColor() {
        return borderColor;
    }

    protected void setBorderColor(Color c) {
        borderColor = c;
    }

    private void initGui() {
        previewPanel = new Thumbnail();
        setBackground(Preference.violet);
        labelBorderMotif.setBackground(Preference.violet);
        labelBorderMotif.setFont(Config.police);
        button.setFont(Config.police);
        setLayout(new GridBagLayout());

        add(labelBorderMotif, new GridBagConstraints(0, 0, 2, 1, 2.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(previewPanel, new GridBagConstraints(2, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(button, new GridBagConstraints(3, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0));
        button.addActionListener(this);
    }

    protected void updatePreviewBorderImage() {
        remove(previewPanel);
        previewPanel = new Thumbnail();
        add(previewPanel, new GridBagConstraints(2, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        validate();
        // When Image are larger, need a repaint
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (null == frame) frame = new ThumbFrame(this);
        else frame.setVisible(true);
    }

    class Thumbnail extends JPanel {
        private static final long serialVersionUID = 1L;
        private double ratio;
        private BufferedImage preview;

        Thumbnail() {
            initGui();
        }

        private void initGui() {
            try {
                if (null == borderColor) {
                    if (Config.searchInternalImage(path) != -1)
                        preview = ImageIO.read(Utils.class.getResource(path));
                    else {
                        if (Utils.fileExists(path)) preview = ImageIO.read(new File(path));
                        else preview = ImageIO.read(Utils.class.getResource("background.png"));
                    }
                } else {
                    preview = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
                    Graphics g = preview.getGraphics();
                    g.setColor(borderColor);
                    g.fillRect(0, 0, 50, 50);
                    g.dispose();
                }
                ratio = 50.0d / preview.getWidth();
                setMinimumSize(new java.awt.Dimension((int) (preview.getWidth() * ratio), 50));
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

        }

        public void paintComponent(Graphics g) {
            if (null != preview) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.scale(ratio, ratio);
                g.drawImage(preview, 0, 0, this);
                g2d.scale(1 / ratio, 1 / ratio);
            }
        }
    }
}
