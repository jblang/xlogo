package xlogo.kernel;

import xlogo.gui.GraphFrame;
import xlogo.gui.EditorFrame;
import xlogo.utils.LogoException;

import java.awt.*;
import java.util.ArrayList;

public class Kernel {
    protected static long chrono = 0;
    protected static boolean mode_trace = false; // true si le mode trace est enclenchée (permet de suivre les procédures)
    protected ArrayFlow flows = new ArrayFlow(); // Contient les flux de lecture ou d'écriture
    // For all drawing operation
//	protected DrawPanel dg;
    // For primitive
    protected Primitive primitive = null;
    // interpreter the user command and launch primitive and procedure
    private Interpreter interpreter;
    private Workspace wp;
    private final GraphFrame graphFrame;
    private MP3Player mp3Player;
    private Calculator calculator;

    public Kernel(GraphFrame graphFrame) {
        this.graphFrame = graphFrame;
        this.wp = new Workspace(graphFrame);
        initCalculator(-1);
    }


    public Workspace getWorkspace() {
        return wp;
    }

    public void setWorkspace(Workspace workspace) {
        wp = workspace;
        //graphFrame.editor = new EditorFrame();
        interpreter.setWorkspace(wp);
    }

    protected String listSearch() throws LogoException {
        return interpreter.chercheListe();
    }

    public void fcfg(Color color) {
        graphFrame.getDrawPanel().setScreenColor(color);
    }

    public Turtle getActiveTurtle() {
        return graphFrame.getDrawPanel().turtle;
    }

    public Calculator getCalculator() {
        return calculator;
    }

    public void fcc(Color color) {
        graphFrame.getDrawPanel().setPenColor(color);
    }

    public void vide_ecran() {
        graphFrame.getDrawPanel().clearScreen();
    }

    public void setNumberOfTurtles(int i) {
        graphFrame.getDrawPanel().setNumberOfTurtles(i);
    }

    public void setDrawingQuality(int id) {
        graphFrame.getDrawPanel().setQuality(id);
    }

    public Color getScreenBackground() {
        return graphFrame.getDrawPanel().getScreenColor();
    }

    public void change_image_tortue(int i) {
        graphFrame.getDrawPanel().changeTurtleImage(i);
    }

    public void initGraphics() {
        graphFrame.getDrawPanel().initGraphics();
    }

    public void buildPrimitiveTreemap(int id) {
        primitive.buildPrimitiveTreemap(id);
    }

    public String execute(StringBuffer st) throws LogoException {
        return interpreter.execute(st);
    }

    protected void initCalculator(int s) {
        calculator = new Calculator(s, graphFrame);

    }

    public void initPrimitive() {
        primitive = new Primitive(graphFrame);
    }


    public void initInterpreter() {
        interpreter = new Interpreter(graphFrame);
    }

    /**
     * Returns the InstructionBuffer containing all commands to execute
     */
    public InstructionBuffer getInstructionBuffer() {
        return interpreter.getInstructionBuffer();
    }

    public MP3Player getMp3Player() {
        return mp3Player;
    }

    public void setMp3Player(MP3Player mp3Player) {
        this.mp3Player = mp3Player;
    }

    class ArrayFlow extends ArrayList<Flow> {
        private static final long serialVersionUID = 1L;

        ArrayFlow() {
            super();
        }

        protected int search(int id) {
            for (int i = 0; i < size(); i++) {
                if (get(i).getId() == id) return i;
            }
            return -1;
        }


    }
}
