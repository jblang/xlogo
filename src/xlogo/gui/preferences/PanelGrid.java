package xlogo.gui.preferences;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.event.*;

import xlogo.Logo;
import xlogo.Config;
import xlogo.gui.preferences.Preference;

public class PanelGrid extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JCheckBox checkGrid;
	private JLabel labelXGrid ;
	private JLabel labelYGrid ;
	private JTextField jtXGrid;
	private JTextField jtYGrid;
	private PanelColor panelColorGrid;

	PanelGrid(){
		initGui();
	}
	public void actionPerformed(ActionEvent e){
		boolean b=checkGrid.isSelected();
		jtXGrid.setEnabled(b);
		jtYGrid.setEnabled(b);
		labelXGrid.setEnabled(b);
		labelYGrid.setEnabled(b);
		panelColorGrid.setEnabled(b);
	}
	protected int getGridColor(){
		return panelColorGrid.getValue().getRGB();
	}
	protected int getXGrid(){
		try{
			int x=Integer.parseInt(jtXGrid.getText());
			return x;
		}
		catch(NumberFormatException e){
			return 20;
		}
	}
	protected int getYGrid(){
		try{
			int x=Integer.parseInt(jtYGrid.getText());
			return x;
		}
		catch(NumberFormatException e){
			return 20;
		}
	}
	protected boolean gridVisible(){
		return checkGrid.isSelected();
	}
	
	private void initGui(){
		checkGrid=new JCheckBox(Logo.messages.getString("active_grid"));
		checkGrid.setSelected(Config.drawGrid);
		labelXGrid =new JLabel(Logo.messages.getString("xgrid"));
		labelYGrid =new JLabel(Logo.messages.getString("ygrid"));
		jtXGrid=new JTextField(String.valueOf(Config.XGrid));
		jtYGrid=new JTextField(String.valueOf(Config.YGrid));
		panelColorGrid=new PanelColor(new Color(Config.gridColor));
		labelXGrid.setFont(Config.police);
		labelYGrid.setFont(Config.police);
		checkGrid.setFont(Config.police);
		panelColorGrid.setFont(Config.police);
		setLayout(new GridBagLayout());
		add(checkGrid, new GridBagConstraints(0, 0, 1, 1, 0.3, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(panelColorGrid, new GridBagConstraints(0, 1, 1, 1, 0.3, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(labelXGrid, new GridBagConstraints(1, 0, 1, 1, 0.25, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(jtXGrid, new GridBagConstraints(2, 0, 1, 1, 0.25, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(labelYGrid, new GridBagConstraints(1, 1, 1, 1, 0.25, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(10, 10, 10, 10), 0, 0));
		add(jtYGrid, new GridBagConstraints(2, 1, 1, 1, 0.25, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(10, 10, 0, 10), 0, 0));
		labelXGrid.setEnabled(Config.drawGrid);
		labelYGrid.setEnabled(Config.drawGrid);
		jtXGrid.setEnabled(Config.drawGrid);
		jtYGrid.setEnabled(Config.drawGrid);
		panelColorGrid.setEnabled(Config.drawGrid);
		
		checkGrid.setBackground(Preference.violet);
		panelColorGrid.setBackground(Preference.violet);
		setBackground(Preference.violet);
		TitledBorder tb=BorderFactory.createTitledBorder(Logo.messages.getString("draw_grid"));
		tb.setTitleFont(Config.police);
		setBorder(tb);
		checkGrid.addActionListener(this);
	}
}
