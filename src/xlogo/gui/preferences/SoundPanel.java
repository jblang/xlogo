package xlogo.gui.preferences;

import xlogo.Logo;
import xlogo.gui.Application;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.*;
import java.awt.*;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class SoundPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Application cadre;
    private final JLabel instruments_dispo = new JLabel(Logo
            .getString("pref.sound.instruments"));
    private JList instrument = new JList();
    private JScrollPane scroll_son;

    protected SoundPanel(Application cadre) {
        this.cadre = cadre;
        initGui();
    }

    private void initGui() {
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
            instrument.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            instrument.setSelectedIndex(cadre.getSoundPlayer().getInstrument());
            scroll_son = new JScrollPane(instrument);
            add(scroll_son, BorderLayout.CENTER);
        } catch (MidiUnavailableException e) {
            Object[] ob = {Logo.getString("pref.sound.problem")};
            instrument = new JList(ob);
            scroll_son = new JScrollPane(instrument);
            add(scroll_son, BorderLayout.CENTER);
        }


    }

    protected void update() {
        int i = instrument.getSelectedIndex();
        if (i != -1)
            cadre.getSoundPlayer().setInstrument(i);
    }
}
