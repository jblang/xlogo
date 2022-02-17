/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class PrinterPanel extends JPanel implements Printable, Runnable {
    private static final long serialVersionUID = 1L;
    private Image image;

    public PrinterPanel(Image image) {
        this.image = image;
    }

    public void run() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
        double largeur = image.getWidth(this);
        double hauteur = image.getHeight(this);
        double facteur = pf.getImageableWidth() / largeur; // largeur
        // imprimable sur la
        // feuille
        double facteur2 = pf.getImageableHeight() / hauteur; // hauteur
        // imprimable
        // sur la
        // feuille

        if (facteur < 1 | facteur2 < 1) {
            facteur = Math.min(facteur, facteur2);
            image = image.getScaledInstance((int) (largeur * facteur),
                    (int) (hauteur * facteur), Image.SCALE_SMOOTH);
        }
        largeur = image.getWidth(this); // permet d'attendre que l'image soit
        // bien créée
        hauteur = image.getHeight(this);
        if (pi < 1) {
            g.drawImage(this.image, (int) pf.getImageableX(), (int) pf
                    .getImageableY(), this);
            return (Printable.PAGE_EXISTS);
        } else
            return Printable.NO_SUCH_PAGE;
    }
}