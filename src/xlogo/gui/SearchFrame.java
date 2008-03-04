package xlogo.gui;

import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.text.Highlighter;
import java.awt.event.*;
import javax.swing.JFrame;
import xlogo.Logo;
import xlogo.Config;
public class SearchFrame extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	private final String FIND="find";
	
	private JButton find;
	private JRadioButton backward, forward;
	private JPanel buttonPanel;
	private JComboBox comboFind;
	private ButtonGroup bg;
	private JLabel labelFind,labelResult;
	private Searchable jtc;
	Highlighter.HighlightPainter cyanPainter;

	public SearchFrame(JFrame jf,Searchable jtc){
		super(jf);
		this.jtc=jtc;
		initGui();
	}	

	public void actionPerformed(ActionEvent e){
		String cmd=e.getActionCommand();
		if (cmd.equals(FIND)){
			find();

		}
	}
	private void find(){
		String element=comboFind.getSelectedItem().toString();
		// Add the element to the combobox
		addCombo(element,comboFind);
		boolean b=jtc.find(element,forward.isSelected());
		if (b) 	{
			// Found
			labelResult.setText("");
		}
		else {
			// Not found
			labelResult.setText(Logo.messages.getString("string_not_found"));
		}		
	}
	protected void processWindowEvent(WindowEvent e){
		super.processWindowEvent(e);
		if (e.getID()==WindowEvent.WINDOW_CLOSING){
			jtc.removeHighlight();
		}
	}
	private void addCombo(String element,JComboBox combo){
		boolean b=false;
		for (int i=0;i<combo.getItemCount();i++){
			if (combo.getItemAt(i).equals(element)) {
				b=true;
				break;
			}
		}
		if (!b){
			combo.insertItemAt(element, 0);
			int n=combo.getItemCount();
			if (n>10){
				combo.removeItemAt(n-1);
			}
		}
	}
	protected void setText(){
		backward.setFont(Config.police);
		forward.setFont(Config.police);
		find.setFont(Config.police);
		labelFind.setFont(Config.police);
		setFont(Config.police);
		backward=new JRadioButton(Logo.messages.getString("backward"));
		forward=new JRadioButton(Logo.messages.getString("forward"));
		TitledBorder tb=BorderFactory.createTitledBorder(Logo.messages.getString("direction"));
		tb.setTitleFont(Config.police);
		buttonPanel.setBorder(tb);
		find=new JButton(Logo.messages.getString("find"));
		labelFind=new JLabel(Logo.messages.getString("find")+" :");
		setTitle(Logo.messages.getString("find_replace"));
	}
	private void initGui(){
		setTitle(Logo.messages.getString("find_replace"));
		// Init the RadioButton for the direction search
		backward=new JRadioButton(Logo.messages.getString("backward"));
		forward=new JRadioButton(Logo.messages.getString("forward"));
		forward.setSelected(true);
		bg=new ButtonGroup();
		bg.add(forward);
		bg.add(backward);
		buttonPanel=new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
		buttonPanel.add(forward);
		buttonPanel.add(backward);
		TitledBorder tb=BorderFactory.createTitledBorder(Logo.messages.getString("direction"));
		tb.setTitleFont(Config.police);
		buttonPanel.setBorder(tb);
		
		// Init Buttons
		find=new JButton(Logo.messages.getString("find"));
		find.addActionListener(this);
		find.setActionCommand(FIND);
		
		// Init JLabel and JCombobox
		labelFind=new JLabel(Logo.messages.getString("find")+" :");
		labelResult=new JLabel();

		comboFind=new JComboBox();
		comboFind.setEditable(true);

		backward.setFont(Config.police);
		forward.setFont(Config.police);
		find.setFont(Config.police);
		labelFind.setFont(Config.police);
		setFont(Config.police);
		
		getContentPane().setLayout(new GridBagLayout());
		// Draw all
		
		getContentPane().add(labelFind, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 10), 0, 0));
		getContentPane().add(comboFind, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						10, 10, 10, 10), 0, 0));
		getContentPane().add(buttonPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 10), 0, 0));
		getContentPane().add(labelResult, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						10, 10, 10, 10), 0, 0));
		getContentPane().add(find, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
						10, 10, 10, 10), 0, 0));
	}
}
