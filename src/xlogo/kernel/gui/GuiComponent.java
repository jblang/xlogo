package xlogo.kernel.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class GuiComponent implements ActionListener {
    public boolean hasAction = false;
    protected int originalWidth, originalHeight;
    protected JComponent guiObject;
    protected boolean actionFinished;
    private String id;

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public boolean hasAction() {
        return hasAction;
    }

    public JComponent getGuiObject() {
        return guiObject;
    }

    public void setLocation(int x, int y) {
        guiObject.setLocation(x, y);
    }

    public Point getLocation() {
        return guiObject.getLocation();
    }

    public void setSize(int width, int height) {
        guiObject.setSize(width, height);
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public abstract void actionPerformed(ActionEvent e);

    public abstract boolean isButton();

    public abstract boolean isMenu();
}
