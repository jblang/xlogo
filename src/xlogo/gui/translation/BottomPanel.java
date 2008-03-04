package xlogo.gui.translation;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import xlogo.Logo;
import xlogo.utils.Utils;
public class BottomPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private TranslateXLogo tx;
	private JTabbedPane jt;
	private MyTable messageTable;
	private MyTable primTable;
	private String id;
	private String action;
	private JButton searchButton;
	 private ImageIcon ichercher=new ImageIcon(Utils.dimensionne_image("chercher.png",this));

	protected BottomPanel(TranslateXLogo tx,String action, String id){
		this.tx=tx;
		this.action=action;
		this.id=id;
		initGui();
	}
	private void initGui(){
		setLayout(new BorderLayout());
		jt= new JTabbedPane();
		messageTable=new MyTable(tx,action,id,"langage");
		primTable=new MyTable(tx,action,id,"primitives");
		jt.add(primTable,Logo.messages.getString("primitives"));
		jt.add(messageTable,Logo.messages.getString("messages"));
		javax.swing.JScrollPane scroll=new javax.swing.JScrollPane(jt);
		
		add(scroll,BorderLayout.CENTER);
		searchButton=new JButton(ichercher);
		searchButton.setToolTipText(Logo.messages.getString("find"));
		searchButton.addActionListener(tx);
		searchButton.setActionCommand(TranslateXLogo.SEARCH);
		searchButton.setSize(new java.awt.Dimension(100,50));
		add(searchButton,BorderLayout.EAST);
	}
	protected String getPrimValue(int a, int b){
		return primTable.getValue(a, b);
	}
	protected String getMessageValue(int a, int b){
		String st=messageTable.getValue(a, b);
		return st;
	}
	protected MyTable getMessageTable(){
		return this.messageTable;
	}
	protected MyTable getPrimTable(){
		return this.primTable;
	}
	protected MyTable getVisibleTable(){
		if (jt.getSelectedIndex()==0) return primTable;
		return messageTable;
	}
}
