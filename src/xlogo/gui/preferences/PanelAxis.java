package xlogo.gui.preferences;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import xlogo.Config;
import xlogo.Logo;

public class PanelAxis extends JPanel  implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JCheckBox checkXAxis;
	private JCheckBox checkYAxis;
	private JLabel labelXAxis ;
	private JLabel labelYAxis ;
	private JTextField jtXAxis;
	private JTextField jtYAxis;
	private PanelColor panelAxisColor;

	PanelAxis(){
		initGui();
	}
	public void actionPerformed(ActionEvent e){
		boolean b1=checkXAxis.isSelected();
		boolean b2=checkYAxis.isSelected();
		labelXAxis.setEnabled(b1);
		labelYAxis.setEnabled(b2);
		jtXAxis.setEnabled(b1);
		jtYAxis.setEnabled(b2);
		panelAxisColor.setEnabled(b1||b2);		
	}
	protected int getAxisColor(){
		return panelAxisColor.getValue().getRGB();
	}
	protected int getXAxis(){
		try{
			int x=Integer.parseInt(jtXAxis.getText());
			return x;
		}
		catch(NumberFormatException e){
			return 30;
		}
	}
	protected int getYAxis(){
		try{
			int x=Integer.parseInt(jtYAxis.getText());
			return x;
		}
		catch(NumberFormatException e){
			return 30;
		}
	}
	protected boolean xAxisVisible(){
		return checkXAxis.isSelected();
	}
	protected boolean yAxisVisible(){
		return checkYAxis.isSelected();
	}
	
	private void initGui(){
		checkXAxis=new JCheckBox(Logo.messages.getString("active_xaxis"));
		checkXAxis.setSelected(Config.drawXAxis);
		checkYAxis=new JCheckBox(Logo.messages.getString("active_yaxis"));
		checkYAxis.setSelected(Config.drawYAxis);
		labelXAxis =new JLabel(Logo.messages.getString("pas"));
		labelYAxis =new JLabel(Logo.messages.getString("pas"));
		jtXAxis=new JTextField(String.valueOf(Config.XAxis));
		jtYAxis=new JTextField(String.valueOf(Config.YAxis));
		panelAxisColor=new PanelColor(new Color(Config.axisColor));
		labelXAxis.setFont(Config.police);
		labelYAxis.setFont(Config.police);
		checkXAxis.setFont(Config.police);
		checkYAxis.setFont(Config.police);
		panelAxisColor.setFont(Config.police);
		setLayout(new GridBagLayout());
		add(checkXAxis, new GridBagConstraints(0, 0, 1, 1, 0.3, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(labelXAxis, new GridBagConstraints(1, 0, 1, 1, 0.25, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(jtXAxis, new GridBagConstraints(2, 0, 1, 1, 0.25, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(checkYAxis, new GridBagConstraints(0, 1, 1, 1, 0.3, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(labelYAxis, new GridBagConstraints(1, 1, 1, 1, 0.25, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(jtYAxis, new GridBagConstraints(2, 1, 1, 1, 0.25, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(panelAxisColor, new GridBagConstraints(0, 2, 1, 1, 0.3, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		labelXAxis.setEnabled(Config.drawXAxis);
		labelYAxis.setEnabled(Config.drawYAxis);
		jtXAxis.setEnabled(Config.drawXAxis);
		jtYAxis.setEnabled(Config.drawYAxis);
		panelAxisColor.setEnabled(Config.drawXAxis||Config.drawYAxis);
		
		checkXAxis.setBackground(Preference.violet);
		checkYAxis.setBackground(Preference.violet);
		panelAxisColor.setBackground(Preference.violet);
		setBackground(Preference.violet);
		TitledBorder tb=BorderFactory.createTitledBorder(Logo.messages.getString("title_axis"));
		tb.setTitleFont(Config.police);
		setBorder(tb);
		checkXAxis.addActionListener(this);
		checkYAxis.addActionListener(this);
	}

	
}
