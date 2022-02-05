package xlogo.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import xlogo.MenuListener;

import javax.swing.*;

public class MyToolBar extends JToolBar {
    private static final long serialVersionUID = 1L;
    private final MenuListener menulistener;
    private final ImageIcon izoomin = new FlatSVGIcon( "xlogo/icons/zoomIn.svg" );
    private final ImageIcon izoomout = new FlatSVGIcon( "xlogo/icons/zoomOut.svg" );
    private final ImageIcon icopier = new FlatSVGIcon( "xlogo/icons/copy.svg" );
    private final ImageIcon icoller = new FlatSVGIcon( "xlogo/icons/menu-paste.svg" );
    private final ImageIcon icouper = new FlatSVGIcon( "xlogo/icons/menu-cut.svg" );
    private final ImageIcon iplay = new FlatSVGIcon( "xlogo/icons/execute.svg" );
    private final ImageIcon istop = new FlatSVGIcon( "xlogo/icons/suspend.svg" );
    private final ImageIcon iedit = new FlatSVGIcon( "xlogo/icons/editSource.svg" );
    //private ImageIcon iturtleProp=new ImageIcon(Utils.dimensionne_image("turtleProp.png", this));
    private final JButton zoomin = new JButton(izoomin);
    private final JButton zoomout = new JButton(izoomout);
    private final JButton copier = new JButton(icopier);
    private final JButton coller = new JButton(icoller);
    private final JButton couper = new JButton(icouper);
    private final JButton play = new JButton(iplay);
    private final JButton stop = new JButton(istop);
    private final JButton edit = new JButton(iedit);
    //private JButton turtleProp=new JButton(iturtleProp);


    public MyToolBar(MenuListener menulistener) {
        super(JToolBar.HORIZONTAL);
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
        stop.addActionListener(menulistener);
        stop.setActionCommand(MenuListener.STOP);
        edit.addActionListener(menulistener);
        edit.setActionCommand(MenuListener.EDIT);
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
        add(edit);
        addSeparator();
        add(play);
        add(stop);
        addSeparator();
        add(zoomin);
        add(zoomout);
        addSeparator();
        add(couper);
        add(copier);
        add(coller);
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
