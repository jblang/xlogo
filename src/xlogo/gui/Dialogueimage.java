package xlogo.gui;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.datatransfer.*; 
import xlogo.kernel.DrawPanel;
import xlogo.Application;
import xlogo.Config;
import xlogo.utils.WriteImage;
import xlogo.Logo;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
/* Dialogueimage is the dialog box opened by clicking
 * 		File --> Capture image as ---> 
 * You can define a frame around the picture you want to print or save. 
 * 
 * */
 
public class Dialogueimage extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private Point coin1=null;
	private Point coin2=null;
	private Rectangle rec=null;
	private Image image;
	private Application cadre;
	private BorderLayout borderLayout1 = new BorderLayout();
	private JTextPane commentaire = new JTextPane();
	private JPanel jPanel1 = new JPanel();
	private JButton bouton_enregistrer = new JButton();
	private JButton bouton_annuler = new JButton();
	private Ev_click evclick=new Ev_click(this);
	private Ev_move evmove=new Ev_move();


  public Dialogueimage() throws HeadlessException {}
  public Dialogueimage(Application cadre,String text) throws HeadlessException {
  	super(cadre);
    this.cadre=cadre;
    this.image=DrawPanel.dessin;
		bouton_annuler.setFont(Config.police);
		bouton_enregistrer.setFont(Config.police);
		commentaire.setFont(Config.police);
		bouton_enregistrer.setText(text);

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    cadre.getArdoise().addMouseListener(evclick);
    cadre.getArdoise().addMouseMotionListener(evmove);
  }
  private void jbInit() throws Exception {
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.setEnabled(true);
    this.setModal(false);
    this.setResizable(true);
    this.setTitle(Logo.messages.getString("titredialogue"));
    this.getContentPane().setLayout(borderLayout1);
    commentaire.setBackground(Color.lightGray);
    commentaire.setEditable(false);
    commentaire.setText(Logo.messages.getString("consigne_cadre"));
    bouton_annuler.setText(Logo.messages.getString("pref.cancel"));

    this.getContentPane().add(commentaire, BorderLayout.CENTER);
    this.getContentPane().add(jPanel1,  BorderLayout.SOUTH);
    jPanel1.add(bouton_enregistrer, null);
    jPanel1.add(bouton_annuler, null);
    bouton_enregistrer.addActionListener(this);
    bouton_annuler.addActionListener(this);

  }
  public void actionPerformed(ActionEvent e){
    cadre.close_DialogueImage();
    int x=0,y=0,width=0,height=0;
    //On efface la boîte de dialogue
    setVisible(false);
// Si un rectangle de sélection a été tracé
    if (null != rec) {
      //On efface le rectangle de sélection
      Graphics g=image.getGraphics();
      g.setColor(Color.black);
      g.setXORMode(Color.white);
      x=(int)rec.getBounds().getX();
      y=(int)rec.getBounds().getY();
      width=(int)rec.getBounds().getWidth();
      height=(int)rec.getBounds().getHeight();
      g.drawRect(x,y,width,height);
      cadre.getArdoise().repaint(x,y,width+1,height+1);
        // On copie l'extrait de l'image sélectionné.
       if (!e.getActionCommand().equals(Logo.messages.getString("pref.cancel"))) {
		image=createImage(new FilteredImageSource(image.getSource(),new CropImageFilter(x,y,width,height)));
		MediaTracker tracker=new MediaTracker(this);
		tracker.addImage(image,0);
		try{tracker.waitForID(0);}
		catch(InterruptedException e1){}

	}
    }

    // On enlève les listener
    cadre.getArdoise().removeMouseListener(evclick);
    cadre.getArdoise().removeMouseMotionListener(evmove);

    if (e.getActionCommand().equals(Logo.messages.getString("menu.file.saveas")+"...")){
        WriteImage writeImage=new WriteImage(cadre,toBufferedImage(image));
    	int value=writeImage.chooseFile();
    	if (value==JFileChooser.APPROVE_OPTION){
       	        commentaire.setText(Logo.messages.getString("enregistrer_image"));
    	        this.setVisible(true);
    	        writeImage.start();
    	        this.dispose();
    	}
  }
  else if (e.getActionCommand().equals(Logo.messages.getString("menu.file.captureimage.print"))){ // Si l'utilisateur appuie sur le bouton imprimer
    AImprimer can=new AImprimer(image);
    //can.setBounds(0,0,(int)(largeur),(int)hauteur+30);
    Thread imprime=new Thread(can);
    imprime.start();
  }
  else if (e.getActionCommand().equals(Logo.messages.getString("menu.file.captureimage.clipboard"))){ // Si l'utilisateur appuie sur le bouton copie vers presse-papiers
	Thread copie=new CopieImage(image);
	copie.start();
}
  else if (e.getActionCommand().equals(Logo.messages.getString("pref.cancel"))) this.dispose();

  }
  class Ev_click extends MouseAdapter{
    private Dialogueimage diag;
    Ev_click(Dialogueimage diag){
    this.diag=diag;
    }
    public void mouseClicked(MouseEvent e){
      // S'il reste un rectangle de sélection précédemment tracé, il faut l'effacer
      if (null!=rec){
        Graphics g=image.getGraphics();
        g.setColor(Color.black);
        g.setXORMode(Color.white);
        int x=(int)rec.getBounds().getX();
        int y=(int)rec.getBounds().getY();
        int width=(int)rec.getBounds().getWidth();
        int height=(int)rec.getBounds().getHeight();
        g.drawRect(x,y,width,height);
        cadre.getArdoise().repaint(x,y,width+1,height+1);
        rec=null;

      }

      // on mémorise ensuite les coordonnées du coin cliqué
      if (null==coin1) coin1=e.getPoint();
      else {
        coin2=e.getPoint();
        int x=(int)Math.min(coin1.getX(),coin2.getX());
        int y=(int)Math.min(coin1.getY(),coin2.getY());
        int width=(int)Math.abs(coin2.getX()-coin1.getX());
        int height=(int)Math.abs(coin2.getY()-coin1.getY());
        Graphics g=image.getGraphics();
        g.setColor(Color.black);
        g.setXORMode(Color.white);
        g.drawRect(x,y,width,height);
        rec=new Rectangle(x,y,width,height);
        coin1=null;
        coin2=null;
        // On fait réapparaître la boîte de dialogue sous la sélection
        Point sel=cadre.jScrollPane1.getViewport().getViewPosition();
        diag.setLocation((int)(x-sel.getX()),(int)(y+height+100-sel.getY()));
        if (!diag.isVisible()) setVisible(true);
        diag.toFront();
        cadre.getArdoise().repaint(x,y,width+1,height+1);
      }
    }
  }
  class Ev_move extends MouseMotionAdapter{
    public void mouseMoved(MouseEvent e){
      if (null!=coin1){
        if (null!=coin2){
          int x=(int)rec.getBounds().getX();
          int y=(int)rec.getBounds().getY();
          int width=(int)rec.getBounds().getWidth();
          int height=(int)rec.getBounds().getHeight();
          Graphics g=image.getGraphics();
          g.setColor(Color.black);
          g.setXORMode(Color.white);
          g.drawRect(x,y,width,height);
          cadre.getArdoise().repaint(x,y,width+1,height+1);
        }
        coin2=e.getPoint();
        int x=(int)Math.min(coin1.getX(),coin2.getX());
        int y=(int)Math.min(coin1.getY(),coin2.getY());
        int width=(int)Math.abs(coin2.getX()-coin1.getX());
        int height=(int)Math.abs(coin2.getY()-coin1.getY());
        Graphics g=image.getGraphics();
        g.setColor(Color.black);
        g.setXORMode(Color.white);
        g.drawRect(x,y,width,height);
        cadre.getArdoise().repaint(x,y,width+1,height+1);
        rec=new Rectangle(x,y,width,height);
      }
    }
  }
 class CopieImage extends Thread implements Transferable{
	private Image image=null;
	Clipboard clip=Toolkit.getDefaultToolkit().getSystemClipboard();
	CopieImage(Image image){
		this.image=toBufferedImage(image);
	}
	public void run(){
        	clip.setContents(this,null);
	}
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {DataFlavor.imageFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}
	public Object getTransferData(DataFlavor flavor)throws UnsupportedFlavorException {
		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return image;
	}
 }

 //transforme une image en BufferedImage
 BufferedImage toBufferedImage(Image image) {
   // On teste si l'image n'est pas déja une instance de BufferedImage
   if (image instanceof BufferedImage) {
     // cool, rien à faire
     return ( (BufferedImage) image);
   }
   else {
     /* On s'assure que l'image est complètement chargée */
     image = new ImageIcon(image).getImage();

     /* On crée la nouvelle image */
     BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
        image.getHeight(null), BufferedImage.TYPE_INT_RGB);
    Graphics g = bufferedImage.createGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();
    return (bufferedImage);
  }
}
class Filtre extends RGBImageFilter{
  Filtre(){
    canFilterIndexColorModel=true;
  }

  public int filterRGB(int x,int y,int pixel){
    int alpha=pixel & 0xff000000;
    int red=pixel & 0xff0000;
    int green=pixel & 0xff00;
    int blue=pixel & 0xff;

    return alpha<<24|255-red<<16|255-green<<8|255-blue;
  }
}
}