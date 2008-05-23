package xlogo.kernel.perspective;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import xlogo.Config;
import xlogo.Logo;

public class FogDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private String[] type={Logo.messages.getString("3d.fog.none"),Logo.messages.getString("3d.fog.linear"),
			Logo.messages.getString("3d.fog.exponential")};
	private JComboBox comboType;
	private PanelValue panelDensity;
	private PanelValue panelFront;
	private PanelValue panelBack;
	private JLabel labelType;
	private JButton ok;
	private JButton refresh;
	private Viewer3D viewer3d;
	private MyFog fog;
	FogDialog(Viewer3D viewer3d, MyFog fog,String title){
		super(viewer3d,title,true);
		this.viewer3d=viewer3d;
		this.fog=fog;
		initGui();
	}
	private void initGui(){
		getContentPane().setLayout(new GridBagLayout());
		setSize(450,200);
		labelType=new JLabel(Logo.messages.getString("3d.fog.type"));
		comboType=new JComboBox(type);
		comboType.setSelectedIndex(fog.getType());

		panelDensity=new PanelValue(fog.getDensity(),Logo.messages.getString("3d.fog.density"));
		panelFront=new PanelValue((int)(fog.getFront()*1000),Logo.messages.getString("3d.fog.frontdistance"));
		panelBack=new PanelValue((int)(fog.getBack()*1000),Logo.messages.getString("3d.fog.backdistance"));
		
		ok=new JButton(Logo.messages.getString("pref.ok"));
		refresh=new JButton(Logo.messages.getString("3d.light.apply"));
		labelType.setFont(Config.police);
		comboType.setFont(Config.police);
		ok.setFont(Config.police);
		refresh.setFont(Config.police);

		comboType.addActionListener(this);
		comboType.setActionCommand("combo");
		ok.addActionListener(this);
		ok.setActionCommand("ok");
		refresh.addActionListener(this);
		refresh.setActionCommand("refresh");
		
		getContentPane().add(labelType, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(comboType, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(panelFront, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(panelBack, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(panelDensity, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(refresh, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(ok, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
		selectComponents();
		setVisible(true);
		
	}
	public void actionPerformed(ActionEvent e){
		String cmd=e.getActionCommand();
		// The selected item in the combo Box has changed
		if (cmd.equals("combo")) selectComponents();
		// Button Ok pressed
		else if (cmd.equals("ok")){
			updateFog();
			dispose();
		}
		// Button Apply pressed
		else if (cmd.equals("refresh")){
			updateFog();
		}
	}
	private void updateFog(){
		int t=comboType.getSelectedIndex();
		float d=panelDensity.getTextValue();
		float back=panelBack.getTextValue();
		float front=panelFront.getTextValue();
		fog.setType(t);
		fog.setDensity(d);
		fog.setBack(back/1000);
		fog.setFront(front/1000);
		fog.detach();
		fog.removeAllChildren();
		fog.createFog();
		viewer3d.addNode(fog);
	}
	private void selectComponents(){
		int id=comboType.getSelectedIndex();
		// None
		if (id==MyFog.FOG_OFF){
			panelDensity.setEnabled(false);
			panelBack.setEnabled(false);
			panelFront.setEnabled(false);
		}
		// Linear Fog
		else if (id==MyFog.FOG_LINEAR){
			panelDensity.setEnabled(false);
			panelBack.setEnabled(true);
			panelFront.setEnabled(true);
		}
		// Exponential Fog
		else if (id==MyFog.FOG_EXPONENTIAL){
			panelBack.setEnabled(false);
			panelFront.setEnabled(false);
			panelDensity.setEnabled(true);
		}
	}
	
	
	class PanelValue extends JPanel{
		private static final long serialVersionUID = 1L;
		private JLabel label;
		private JTextField text;
		private String title;
		private float textValue;
		PanelValue(float textValue,String title){
			this.textValue=textValue;
			this.title=title;
			initGui();
		}
		private void initGui(){
			label=new JLabel(title);
			label.setFont(Config.police);
			String st;
			int i=(int)textValue;
			if (i==textValue) {
				st=String.valueOf(i);
				
			}
			else st=String.valueOf(textValue);
			text=new JTextField(st,4);
			add(label);
			add(text);
		}
		public void setEnabled(boolean b){
			super.setEnabled(b);
			label.setEnabled(b);
			text.setEnabled(b);	
		}
		float getTextValue(){
			try{
				float f=Float.parseFloat(text.getText());
				return f;
			}
			catch(NumberFormatException e){
			}
			return 0;
		}
	}
	
}
