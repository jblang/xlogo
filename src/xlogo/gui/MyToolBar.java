package xlogo.gui;

import xlogo.MenuListener;
import xlogo.utils.Utils;

import javax.swing.*;

public class MyToolBar extends JToolBar {
    private static final long serialVersionUID = 1L;
    private final MenuListener menulistener;
    private final ImageIcon izoomin = new ImageIcon(Utils.dimensionne_image("zoomin.png", this));
    private final ImageIcon izoomout = new ImageIcon(Utils.dimensionne_image("zoomout.png", this));
    private final ImageIcon icopier = new ImageIcon(Utils.dimensionne_image("editcopy.png", this));
    private final ImageIcon icoller = new ImageIcon(Utils.dimensionne_image("editpaste.png", this));
    private final ImageIcon icouper = new ImageIcon(Utils.dimensionne_image("editcut.png", this));
    private final ImageIcon iplay = new ImageIcon(Utils.dimensionne_image("play.png", this));
    //private ImageIcon iturtleProp=new ImageIcon(Utils.dimensionne_image("turtleProp.png", this));
    private final JButton zoomin = new JButton(izoomin);
    private final JButton zoomout = new JButton(izoomout);
    private final JButton copier = new JButton(icopier);
    private final JButton coller = new JButton(icoller);
    private final JButton couper = new JButton(icouper);
    private final JButton play = new JButton(iplay);
    //private JButton turtleProp=new JButton(iturtleProp);


    public MyToolBar(MenuListener menulistener) {
        super(JToolBar.VERTICAL);
        this.menulistener = menulistener;
        initGui();
    }

    private void initGui() {
        zoomin.addActionListener(menulistener);
        zoomin.setActionCommand(MenuListener.ZOOMIN);
        zoomout.addActionListener(menulistener);
        zoomout.setActionCommand(MenuListener.ZOOMOUT);
        copier.addActionListener(menulistener);
        copier.setActionCommand(MenuListener.EDIT_COPY);
        couper.addActionListener(menulistener);
        couper.setActionCommand(MenuListener.EDIT_CUT);
        coller.addActionListener(menulistener);
        coller.setActionCommand(MenuListener.EDIT_PASTE);
        play.addActionListener(menulistener);
        play.setActionCommand(MenuListener.PLAY);
/*		slider= new JSlider(JSlider.VERTICAL);
		slider.setValue(slider.getMaximum()-Config.turtleSpeed);
		//Create the label table
		Hashtable labelTable = new Hashtable();
		labelTable.put( new Integer( 0 ), new JLabel("Slow") );
		labelTable.put( new Integer( 100 ), new JLabel("Fast") );
		slider.setLabelTable( labelTable );
		slider.setPaintLabels(true);
		/*	slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);

		slider.setSnapToTicks(true);
		slider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    int value=source.getValue();
			    Config.turtleSpeed=source.getMaximum()-value;			
			}
		});
		int width=Toolkit.getDefaultToolkit().getScreenSize().width;
		width=32*width/1024;
		slider.setMinimumSize(new java.awt.Dimension(width,200));
		slider.setMaximumSize(new java.awt.Dimension(width,200));
		*/
        add(zoomin);
        addSeparator();
        add(zoomout);
        addSeparator();
        add(couper);
        addSeparator();
        add(copier);
        addSeparator();
        add(coller);
        addSeparator();
        add(play);
        addSeparator();
//		add(slider);
    }

    public void enabledPlay(boolean b) {
        play.setEnabled(b);
    }

    /**
     * Enables or disables the zoom buttons
     *
     * @param b The boolean
     */
    public void setZoomEnabled(boolean b) {
        zoomin.setEnabled(b);
        zoomout.setEnabled(b);
//		repaint();
    }
}
