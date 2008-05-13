package xlogo.kernel.perspective;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import xlogo.Config;
import xlogo.Logo;
import xlogo.gui.preferences.PanelColor;
import xlogo.kernel.perspective.LightDialog.PanelAngle;
import xlogo.kernel.perspective.LightDialog.PanelPosition;

public class FogDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private String[] type={Logo.messages.getString("3d.fog.none"),Logo.messages.getString("3d.fog.linear"),
			Logo.messages.getString("3d.fog.exponential")};
	private JComboBox comboType;
	PanelColor panelColor;
	private PanelDensity panelDensity;
	private JLabel labelType;
	private JButton ok;
	private JButton refresh;
	private Viewer3D viewer3d;
	private MyFog fog;
	FogDialog(Viewer3D viewer3d, MyFog fog){
		super(viewer3d,true);
		this.viewer3d=viewer3d;
		this.fog=fog;
		initGui();
	}
	private void initGui(){
		getContentPane().setLayout(new GridBagLayout());
		setSize(400,200);
		labelType=new JLabel(Logo.messages.getString("3d.fog.type"));
		comboType=new JComboBox(type);
		comboType.setSelectedIndex(fog.getType());

		Color3f col=fog.getColor();
		if (null!=col)	panelColor=new PanelColor(col.get());
		else panelColor=new PanelColor(Color.white);
		panelColor.setBackground(comboType.getBackground());
		
		panelDensity=new PanelDensity(fog.getDensity());
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
		getContentPane().add(panelColor, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(panelDensity, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(refresh, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(ok, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
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
		Color3f c=new Color3f(panelColor.getValue());
		float d=panelDensity.getAngleValue();
		fog.setType(t);
		fog.setColor(c);
		fog.setDensity(d);
		fog.detach();
		fog.removeAllChildren();
		fog.createFog();
		viewer3d.addNode(fog);
	}
	private void selectComponents(){
		int id=comboType.getSelectedIndex();
		// None
		if (id==MyFog.FOG_OFF){
			panelColor.setEnabled(false);
			panelDensity.setEnabled(false);
		}
		// Linear Fog
		else if (id==MyFog.FOG_LINEAR){
			panelColor.setEnabled(true);
			panelDensity.setEnabled(false);
		}
		// Exponential Fog
		else if (id==MyFog.FOG_EXPONENTIAL){
			panelColor.setEnabled(true);
			panelDensity.setEnabled(true);
		}
	}
	class PanelDensity extends JPanel{
		private static final long serialVersionUID = 1L;
		private JLabel label;
		private JTextField density;
		private float densityValue;
		PanelDensity(float densityValue){
			this.densityValue=densityValue;
			
			initGui();
		}
		private void initGui(){
			label=new JLabel(Logo.messages.getString("3d.fog.density"));
			label.setFont(Config.police);
			density=new JTextField(String.valueOf(densityValue));
			density.setSize(30, Config.police.getSize()+10);
			add(label);
			add(density);
		}
		public void setEnabled(boolean b){
			super.setEnabled(b);
			label.setEnabled(b);
			density.setEnabled(b);	
		}
		float getAngleValue(){
			try{
				float f=Float.parseFloat(density.getText());
				return f;
			}
			catch(NumberFormatException e){
			}
			return MyFog.DEFAULT_DENSITY;
		}
	}
	
}
