package xlogo.gui.preferences;

import java.awt.BorderLayout;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import xlogo.Config;
import xlogo.Application;
import xlogo.Logo;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
public class Panel_Sound extends JPanel {
	private static final long serialVersionUID = 1L;
	private Application cadre;
	private JLabel instruments_dispo = new JLabel(Logo.messages
			.getString("pref.sound.instruments"));
	private JList instrument = new JList();
	private JScrollPane scroll_son;
	protected Panel_Sound(Application cadre){
		this.cadre=cadre;
		initGui();
	}
	private void initGui(){
		instruments_dispo.setFont(Config.police);
		setBackground(Preference.violet);
		instruments_dispo.setBackground(Preference.violet);
		setLayout(new BorderLayout());
		add(instruments_dispo, BorderLayout.NORTH);
		try {
			Synthesizer synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			Instrument[] in = synthesizer.getAvailableInstruments();
			Object[] ob = new Object[in.length];
			for (int i = 0; i < in.length; i++) {
				ob[i] = i + " " + in[i].getName();
			}
			synthesizer.close();
			instrument = new JList(ob);
			instrument.setFont(Config.police);
			instrument.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			instrument.setSelectedIndex(cadre.getSon().getInstrument());
			scroll_son = new JScrollPane(instrument);
			add(scroll_son, BorderLayout.CENTER);
		} catch (MidiUnavailableException e) {
			Object[] ob = { Logo.messages.getString("pref.sound.problem") };
			instrument = new JList(ob);
			scroll_son = new JScrollPane(instrument);
			add(scroll_son, BorderLayout.CENTER);
		}


	}
	protected void update(){
		int i = instrument.getSelectedIndex();
		if (i != -1)
			cadre.getSon().setInstrument(i);
	}
}
