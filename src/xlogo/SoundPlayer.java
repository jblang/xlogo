/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

package xlogo;

import xlogo.utils.LogoException;

import javax.sound.midi.*;
import java.util.StringTokenizer;

/**
 * This class is designed to play music in XLogo
 * @author loic
 *
 */
public class SoundPlayer {
    // These are some MIDI constants from the spec.  They aren't defined
    public static final int END_OF_TRACK = 47;
    static final int[] offsets = {0, 2, 4, 5, 7, 9, 11};
    private final Application cadre;
    private final String[] notes = {Logo.messages.getString("note.do"), Logo.messages.getString("note.re"), Logo.messages.getString("note.mi"), Logo.messages.getString("note.fa"), Logo.messages.getString("note.sol"), Logo.messages.getString("note.la"), Logo.messages.getString("note.si")};
    private Track track = null;
    private int instrument = 0; //instrument selectionne
    private int ticks = 0; // Temps écoulé depuis le début de la piste
    private Synthesizer synthesizer;
    private Sequencer sequencer;
    private Sequence sequence;

    /**
     * Builds our Sound Player
     * @param cadre The main Frame
     */
    SoundPlayer(Application cadre) {
        this.cadre = cadre;
        try {
            sequence = new Sequence(Sequence.PPQ, 16);
        } catch (InvalidMidiDataException e) {
        }
    }

    /**
     * Return the current instrument
     * @return The integer that represents the current instrument
     */
    public int getInstrument() {
        return instrument;
    }

    /**
     * Choose a specific instrument
     * @param i The integer that represents this instrument
     */
    public void setInstrument(int i) {
        instrument = i;
    }

    /**
     * This methode parses the note sequence and write it into a MIDI Sequence
     * @param li This List contains the sequence of note to play
     * @throws LogoException If the sequence in't valid
     */
    public void cree_sequence(String li) throws LogoException {
        // 16 ticks per quarter note.
        try {

            /*
             * Cette méthode met les notes contenues dans li dans la piste Track
             * Voici les notations utilisées:
             * do-re-mi-fa-sol-la-si:   Les notes do+ pour le dièse, do- pour le bémol
             * :+,:++ etc     Monte d'une octave, de deux octaves etc
             *  :-,:--, etc    Descend d'une octave, de deux octaves etc
             * 1 ou 0.5 ou 2 :    Durée affectée à la série de notes
             */
            if (null == track) track = sequence.createTrack();  // Begin with a new track
            // Set the instrument on channel 0
            ShortMessage sm = new ShortMessage();
            sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrument, 0);

            track.add(new MidiEvent(sm, 0));
            int notelength = 64; // valeur d'une noire
            int velocity = 64;   // default to middle volume
            int basekey = 60;    // 60 is middle C. Adjusted up and down by octave
            int key = 0;
            StringTokenizer st = new StringTokenizer(li);
            String element = "";
            if (st.hasMoreTokens()) element = st.nextToken().toLowerCase();
            while (!element.equals("")) {
                int i = isnotes(element);
                if (i != -1) {
                    key = basekey + offsets[isnotes(element)];
                    if (st.hasMoreTokens()) element = st.nextToken().toLowerCase();
                    else element = "";
                    if (element.equals("+")) { //dièse
                        key++;
                        if (st.hasMoreTokens()) element = st.nextToken().toLowerCase();
                        else element = "";
                    } else if (element.equals("-")) {  //bémol
                        key--;
                        if (st.hasMoreTokens()) element = st.nextToken().toLowerCase();
                        else element = "";
                    }
                    //	System.out.println(ticks+" "+notelength+" "+velocity+" "+key);
                    addNote(ticks, notelength, key, velocity);
                    ticks += notelength;
                }
                if (element.equals(":")) {
                    while (st.hasMoreTokens()) {
                        element = st.nextToken().toLowerCase();
                        if (element.equals("+")) {
                            basekey += 12;  //Une octave en plus
                            if (!st.hasMoreTokens()) element = "";
                        } else if (element.equals("-")) {
                            basekey -= 12;  //Une octave en moins
                            if (!st.hasMoreTokens()) element = "";
                        } else break;
                    }
                } else {
                    try {
                        double longueur = Double.parseDouble(element);
                        notelength = (int) (longueur * 64 + 0.5);
                        if (st.hasMoreTokens()) element = st.nextToken().toLowerCase();
                        else element = "";
                    } catch (NumberFormatException e) {
                        if (!element.equals("") && isnotes(element) == -1 && !element.equals(":"))
                            throw new LogoException(cadre, Logo.messages.getString("probleme_sequence"));
                    }
                }
            }
        } catch (InvalidMidiDataException e2) {
        }
    }

    /**
     * If element is a note return the index for the notes in the array list "notes"
     * @param element The String to test
     * @return The index for this notes, -1 if it doesn't exist
     */
    int isnotes(String element) {  //teste si element est une note et renvoie alors son numéro sinon renvoie -1
        for (int i = 0; i < notes.length; i++) {
            if (notes[i].equals(element)) return i;
        }
        return -1;
    }

    /**
     * Play the Track
     */
    public void joue() {
        if (null != track) {
            try {
                // Set up the Sequencer and Synthesizer objects
                sequencer = MidiSystem.getSequencer();
                sequencer.open();
                synthesizer = MidiSystem.getSynthesizer();
                synthesizer.open();
                sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
                // Specify the sequence to play, and the tempo to play it at
                sequencer.setSequence(sequence);
                sequencer.setTempoInBPM(240);
                // Let us know when it is done playing
                sequencer.addMetaEventListener(new MetaEventListener() {
                    public void meta(MetaMessage m) {
                        // A message of this type is automatically sent
                        // when we reach the end of the track
                        if (m.getType() == END_OF_TRACK) {
                            sequencer.close();
                            synthesizer.close();
                        }
                    }
                });
                // And start playing now.
                sequencer.start();
                try {
                    long temps = sequence.getTickLength();
                    while (temps > 0) { //On attends que la séquence soit jouée.
                        Thread.sleep(500);
                        temps -= 32;
                        if (LogoException.lance && cadre.error) { //On a appuyé sur le bouton stop
                            sequencer.close();
                            synthesizer.close();
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                }
            } catch (MidiUnavailableException e1) {
            } catch (InvalidMidiDataException e2) {
            }
        }
    }

    /**
     * Delete the sequence in memory
     */
    public void efface_sequence() {
        try {
            sequence = new Sequence(Sequence.PPQ, 16);
            track = null;
            ticks = 0;
        } catch (InvalidMidiDataException e) {
        }


    }

    /**
     * return tick index
     * @return Tick index
     */
    public int getTicks() {
        return ticks;
    }

    /**
     * set tick index
     * @param i
     */
    public void setTicks(int i) {
        ticks = i;
    }

    /**
     * A convenience method to add a note to the track on channel 0
     * @param startTick the starting tick
     * @param tickLength the tick length
     * @param key the note
     * @param velocity the volume
     * @throws InvalidMidiDataException
     */

    public void addNote(int startTick,
                        int tickLength, int key, int velocity)
            throws InvalidMidiDataException {
        ShortMessage on = new ShortMessage();
        on.setMessage(ShortMessage.NOTE_ON, 0, key, velocity);
        ShortMessage off = new ShortMessage();
        off.setMessage(ShortMessage.NOTE_OFF, 0, key, velocity);
        track.add(new MidiEvent(on, startTick));
        track.add(new MidiEvent(off, startTick + tickLength));
    }
}