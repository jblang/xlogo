/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import xlogo.Logo;
import xlogo.utils.Utils;

import java.util.List;
import java.util.Stack;

/**
 * Sound-related primitives: MIDI sequences, MP3 playback, instruments.
 */
public class SoundPrimitives extends PrimitiveGroup {

    public SoundPrimitives(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("sound.deletesequence", 0, false, this::deleteSequence),
            new Primitive("sound.indexsequence", 0, false, this::getSequenceIndex),
            new Primitive("sound.instrument", 0, false, this::getInstrument),
            new Primitive("sound.mp3play", 1, false, this::playMP3),
            new Primitive("sound.mp3stop", 0, false, this::stopMP3),
            new Primitive("sound.play", 0, false, this::play),
            new Primitive("sound.sequence", 1, false, this::sequence),
            new Primitive("sound.setindexsequence", 1, false, this::setSequenceIndex),
            new Primitive("sound.setinstrument", 1, false, this::setInstrument)
        );
    }

    void stopMP3(Stack<String> param) {
        setReturnValue(false);
        if (null != kernel.getMp3Player()) kernel.getMp3Player().getPlayer().close();
    }

    void playMP3(Stack<String> param) {
        setReturnValue(false);
        if (kernel.getMp3Player() != null) kernel.getMp3Player().getPlayer().close();
        String word = getWord(param.get(0));
        try {
            if (null == word) throw new LogoException(app, Logo.getString("interpreter.error.wordRequired"));
            MP3Player player = new MP3Player(app, word);
            kernel.setMp3Player(player);
            kernel.getMp3Player().start();
        } catch (LogoException ignored) {
        }
    }

    void setSequenceIndex(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            app.getSoundPlayer().setTicks(i * 64);
        } catch (LogoException ignored) {
        }
    }

    void getSequenceIndex(Stack<String> param) {
        setReturnValue(true);
        double d = (double) app.getSoundPlayer().getTicks() / 64;
        pushResult(Calculator.formatDouble(d));
    }

    void setInstrument(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            app.getSoundPlayer().setInstrument(i);
        } catch (LogoException ignored) {
        }
    }

    void deleteSequence(Stack<String> param) {
        app.getSoundPlayer().clearSequence();
    }

    void play(Stack<String> param) {
        app.getSoundPlayer().play();
    }

    void getInstrument(Stack<String> param) {
        setReturnValue(true);
        pushResult(String.valueOf(app.getSoundPlayer().getInstrument()));
    }

    void sequence(Stack<String> param) {
        try {
            String list = getFinalList(param.get(0));
            app.getSoundPlayer().createSequence(Utils.formatCode(list).toString());
        } catch (LogoException ignored) {
        }
    }
}
