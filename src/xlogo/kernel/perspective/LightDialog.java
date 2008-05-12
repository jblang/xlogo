package xlogo.kernel.perspective;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;

import xlogo.Config;
import xlogo.Logo;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import xlogo.gui.preferences.PanelColor;
public class LightDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private String[] type={Logo.messages.getString("3d.light.none"),Logo.messages.getString("3d.light.ambient"),
			Logo.messages.getString("3d.light.directional"),Logo.messages.getString("3d.light.point"),
			Logo.messages.getString("3d.light.spot")};
	private JComboBox comboType;
	PanelColor panelColor;
	private PanelPosition panelPosition;
	private PanelPosition panelDirection;
	private PanelAngle panelAngle;
	private JLabel labelType;
	private JButton ok;
	private JButton refresh;
	private Viewer3D viewer3d;
	private LightConfig lc;
	LightDialog(Viewer3D viewer3d,LightConfig lc){
		super(viewer3d);
		this.viewer3d=viewer3d;
		this.lc=lc;
		initGui();
	}
	private void initGui(){
		getContentPane().setLayout(new GridBagLayout());
		setSize(400,200);
		labelType=new JLabel(Logo.messages.getString("3d.light.type"));
		comboType=new JComboBox(type);
		comboType.setSelectedIndex(lc.getType());

		Color3f col=lc.getColor();
		if (null!=col)	panelColor=new PanelColor(col.get());
		else panelColor=new PanelColor(Color.white);
		panelColor.setBackground(comboType.getBackground());
		
		panelPosition=new PanelPosition(Logo.messages.getString("3d.light.position"),lc.getPosition());
		panelDirection=new PanelPosition(Logo.messages.getString("3d.light.direction"),lc.getDirection());
		panelAngle=new PanelAngle(lc.getAngle());
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
		getContentPane().add(panelPosition, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(panelDirection, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						0, 0, 0, 0), 0, 0));
		getContentPane().add(panelAngle, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
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
			updateLight();
			dispose();
		}
		// Button Apply pressed
		else if (cmd.equals("refresh")){
			updateLight();
		}
	}
	private void updateLight(){
		int t=comboType.getSelectedIndex();
		Color3f c=new Color3f(panelColor.getValue());
		Point3f p=(Point3f)panelPosition.getPosition();
		Vector3f d=(Vector3f)panelDirection.getDirection();
		float a=panelAngle.getAngleValue();
		lc.setType(t);
		lc.setColor(c);
		lc.setPosition(p);
		lc.setDirection(d);
		lc.setAngle(a);
		viewer3d.removeLight(lc.getLight());
		lc.createLight();
		viewer3d.addLight(lc.getLight());
		
	}
	private void selectComponents(){
		int id=comboType.getSelectedIndex();
		// None
		if (id==LightConfig.LIGHT_OFF){
			panelColor.setEnabled(false);
			panelPosition.setEnabled(false);
			panelDirection.setEnabled(false);
			panelAngle.setEnabled(false);
		}
		// Ambient
		else if (id==LightConfig.LIGHT_AMBIENT){
			panelColor.setEnabled(true);
			panelPosition.setEnabled(false);
			panelDirection.setEnabled(false);
			panelAngle.setEnabled(false);
		}
		// Directional
		else if (id==LightConfig.LIGHT_DIRECTIONAL){
			panelColor.setEnabled(true);
			panelPosition.setEnabled(false);
			panelDirection.setEnabled(true);
			panelAngle.setEnabled(false);
		}
		// PointLight
		else if (id==LightConfig.LIGHT_POINT){
			panelColor.setEnabled(true);
			panelPosition.setEnabled(true);
			panelDirection.setEnabled(true);
			panelAngle.setEnabled(false);
		}
		// Spot
		else if (id==LightConfig.LIGHT_SPOT){
			panelColor.setEnabled(true);
			panelPosition.setEnabled(true);
			panelDirection.setEnabled(true);
			panelAngle.setEnabled(true);
		}
	}
	class PanelAngle extends JPanel{
		private static final long serialVersionUID = 1L;
		private JLabel label;
		private JTextField angle;
		private float angleValue;
		PanelAngle(float angleValue){
			this.angleValue=angleValue;
			
			initGui();
		}
		private void initGui(){
			label=new JLabel(Logo.messages.getString("3d.light.angle"));
			label.setFont(Config.police);
			angle=new JTextField(String.valueOf(angleValue));
			angle.setSize(30, Config.police.getSize()+10);
			add(label);
			add(angle);
		}
		public void setEnabled(boolean b){
			super.setEnabled(b);
			label.setEnabled(b);
			angle.setEnabled(b);	
		}
		float getAngleValue(){
			try{
				float f=Float.parseFloat(angle.getText());
				return f;
			}
			catch(NumberFormatException e){
			}
			return LightConfig.DEFAULT_ANGLE;
		}
	}
	
	class PanelPosition extends JPanel{
		private static final long serialVersionUID = 1L;
		private String title;
		private JTextField Xpos;
		private JTextField Ypos;
		private JTextField Zpos;
		private JLabel sep1;
		private JLabel sep2;
		Tuple3f tuple;
		PanelPosition(String title,Tuple3f tuple){
			this.title=title;
			this.tuple=tuple;
			initGui();
		}
		private void initGui(){
			TitledBorder tb=BorderFactory.createTitledBorder(title);
			tb.setTitleFont(Config.police);
			setBorder(tb);
			sep1=new JLabel("x");
			sep2=new JLabel("x");
			Xpos=new JTextField();
			Ypos=new JTextField();
			Zpos=new JTextField();
			if (null!=tuple){
				Xpos.setText(String.valueOf((int)(tuple.x*1000)));
				Ypos.setText(String.valueOf((int)(tuple.y*1000)));
				Zpos.setText(String.valueOf((int)(tuple.z*1000)));
			}
			else {
				Xpos.setText("         ");
				Ypos.setText("         ");
				Zpos.setText("         ");
			}
			add(Xpos);
			add(sep1);
			add(Ypos);
			add(sep2);
			add(Zpos);
		}
		
		
		Point3f getPosition(){
			try{
				float x=Float.parseFloat(Xpos.getText());
				float y=Float.parseFloat(Ypos.getText());
				float z=Float.parseFloat(Zpos.getText());
				return new Point3f(x,y,z);
			}
			catch(NumberFormatException e){}
			return(new Point3f(0,0,0));
		}
		Vector3f getDirection(){
			try{
				float x=Float.parseFloat(Xpos.getText());
				float y=Float.parseFloat(Ypos.getText());
				float z=Float.parseFloat(Zpos.getText());
				return new Vector3f(x,y,z);
			}
			catch(NumberFormatException e){}
			return(new Vector3f(0,0,0));

		}
		
		public void setEnabled(boolean b){
			super.setEnabled(b);
			sep1.setEnabled(b);
			sep2.setEnabled(b);
			Xpos.setEnabled(b);
			Ypos.setEnabled(b);
			Zpos.setEnabled(b);
		}
		
	}

}
