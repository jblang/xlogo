/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.gui;

import xlogo.Logo;
import xlogo.kernel.*;
import xlogo.kernel.perspective.Viewer3D;
import xlogo.utils.ImageWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Locale;

import static xlogo.utils.Utils.createButton;

public class GraphFrame extends JFrame {
    static final double ZOOM_FACTOR = 1.25;
    public static Viewer3D viewer3D = null;
    final SoundPlayer soundPlayer = new SoundPlayer(this);
    private final DrawPanel drawPanel;
    public boolean error = false;
    public EditorFrame editor;
    public Animation animation = null;
    public JScrollPane scrollPane = new JScrollPane();
    boolean stop = false;
    private JButton zoomInButton;
    private JButton zoomOutButton;

    /**
     * Builds the main frame
     */
    public GraphFrame(EditorFrame editor) {
        this.editor = editor;
        Logo.kernel = new Kernel(this);
        drawPanel = new DrawPanel(this);
        resizeDrawingZone();
        Logo.kernel.initInterpreter();
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        setTitle("XLogo");
        setIconImage(Logo.getAppIcon().getImage());
        initContent();
        pack();
        setLocationRelativeTo(null);
    }

    JToolBar initToolBar() {
        var toolBar = new JToolBar();
        createButton(toolBar, "screenshot", e -> saveImage());
        createButton(toolBar, "print", "imprimer_editeur", e -> printImage());
        createButton(toolBar, "copy", e -> copyImage());
        toolBar.addSeparator();
        zoomInButton = createButton(toolBar, "zoomIn", e -> zoomIn());
        zoomOutButton = createButton(toolBar, "zoomOut", e -> zoomOut());
        return toolBar;
    }

    void initContent() {
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(initToolBar(), BorderLayout.NORTH);

        drawPanel.setSize(new java.awt.Dimension((int) (Logo.config.getImageWidth() * DrawPanel.zoom), (int) (Logo.config.getImageHeight() * DrawPanel.zoom)));
        scrollPane.getViewport().add(drawPanel);
        scrollPane.getHorizontalScrollBar().setBlockIncrement(5);
        scrollPane.getVerticalScrollBar().setBlockIncrement(5);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Close the window
     */
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() != WindowEvent.WINDOW_CLOSING) {
            super.processWindowEvent(e);
        }
    }

    // What happens when validating the text box

    void saveImage() {
        ImageWriter imageWriter = new ImageWriter(this, getCanvas().getSelectionImage());
        int value = imageWriter.chooseFile();
        if (value == JFileChooser.APPROVE_OPTION) {
            imageWriter.start();
        }
    }

    void printImage() {
        PrinterPanel panel = new PrinterPanel(getCanvas().getSelectionImage());
        Thread printer = new Thread(panel);
        printer.start();
    }

    void showColorChooser(boolean pen) {
        var title = pen ? "couleur_du_crayon" : "couleur_du_fond";
        Color color = JColorChooser.showDialog(this, Logo.messages.getString(title), getCanvas().getScreenColor());
        if (null != color) {
            Locale locale = Logo.getLocale(Logo.config.getLanguage());
            java.util.ResourceBundle rs = java.util.ResourceBundle.getBundle("primitives", locale);
            var f = rs.getString(pen ? "fcc" : "fcfg");
            f = f.substring(0, f.indexOf(" "));
            editor.updateHistory("commentaire", f + " [" + color.getRed() + " " + color.getGreen() + " " + color.getBlue() + "]\n");
            if (pen) {
                Logo.kernel.fcc(color);
            } else {
                Logo.kernel.fcfg(color);
            }
        }
    }

    void zoomIn() {
        getCanvas().zoom(ZOOM_FACTOR * DrawPanel.zoom, true);
    }

    void zoomOut() {
        getCanvas().zoom(1 / ZOOM_FACTOR * DrawPanel.zoom, true);
    }

    // change language for the interface
    // change la langue de l'interface

    // Change font for the interface
    // Change la police de l'interface

    /**
     * Resize the dawing area
     */
    public void resizeDrawingZone() {
        if (null != animation) {
            animation.setPause(true);
        }
        // resize the drawing image
        SwingUtilities.invokeLater(() -> {

            MediaTracker tracker = new MediaTracker(drawPanel);
            try {
                tracker.waitForID(0);
            } catch (InterruptedException ignored) {
            }

            drawPanel.setPreferredSize(new Dimension(Logo.config.getImageWidth(), Logo.config.getImageHeight()));
            drawPanel.revalidate();
            drawPanel.initGraphics();
            Logo.kernel.initGraphics();
            //drawPanel.repaint();

            if (null != animation) animation.setPause(false);
        });

    }

    /**
     * Modify the Look&Feel for the GraphFrame
     */
    public void changeLookAndFeel() {
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * Return the drawing area
     *
     * @return The drawing area
     */
    DrawPanel getCanvas() {
        return drawPanel;
    }

    void copyImage() {
        Thread copie = new ImageCopier();
        copie.start();
    }

    /**
     * This boolean indicates if the viewer3D is visible
     *
     * @return true or false
     */
    public boolean viewer3DVisible() {
        if (null != viewer3D) return viewer3D.isVisible();
        return false;
    }

    /**
     * Initialize the 3D Viewer
     */
    public void initViewer3D() {
        if (null == viewer3D) {
            viewer3D = new Viewer3D(drawPanel.getWorld3D(), drawPanel.getScreenColor());
        }

    }

    public Viewer3D getViewer3D() {
        return viewer3D;
    }

    /**
     * Open the View3dFrame Frame
     */
    public void viewerOpen() {
        if (null == viewer3D) {
            viewer3D = new Viewer3D(drawPanel.getWorld3D(), drawPanel.getScreenColor());
        } else {
            viewer3D.setVisible(false);
        }
        viewer3D.insertBranch();
        viewer3D.setVisible(true);
        viewer3D.requestFocus();
    }


    /**
     * Returns the current draw panel
     *
     * @return The DrawPanel Object associated to main frame
     */
    public DrawPanel getDrawPanel() {
        return drawPanel;
    }

    /**
     * Enables or disables the zoom buttons
     *
     * @param b The boolean
     */
    public void setZoomEnabled(boolean b) {
        zoomInButton.setEnabled(b);
        zoomOutButton.setEnabled(b);
    }

    class ImageCopier extends Thread implements Transferable {
        private final BufferedImage image;
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

        ImageCopier() {
            image = getCanvas().getSelectionImage();
        }

        public void run() {
            clip.setContents(this, null);
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
    }
}