package xlogo.kernel;

import xlogo.Application;
import xlogo.gui.Editor;

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
    // interprete the user command and launch primitive and procedure
    private Interprete interprete;
    private Workspace wp;
    private final Application app;
    private MP3Player mp3Player;
    private MyCalculator myCalculator;

    public Kernel(Application app) {
        this.app = app;
        this.wp = new Workspace(app);
        initCalculator(-1);
    }


    public Workspace getWorkspace() {
        return wp;
    }

    public void setWorkspace(Workspace workspace) {
        wp = workspace;
        app.editeur = new Editor(app);
        interprete.setWorkspace(wp);
    }

    protected String listSearch() throws xlogo.utils.myException {
        return interprete.chercheListe();
    }

    public void fcfg(Color color) {
        app.getArdoise().fcfg(color);
    }

    public Turtle getActiveTurtle() {
        return app.getArdoise().tortue;
    }

    public MyCalculator getCalculator() {
        return myCalculator;
    }

    public void fcc(Color color) {
        app.getArdoise().fcc(color);
    }

    public void vide_ecran() {
        app.getArdoise().videecran();
    }

    public void setNumberOfTurtles(int i) {
        app.getArdoise().setNumberOfTurtles(i);
    }

    public void setDrawingQuality(int id) {
        app.getArdoise().setQuality(id);
    }

    public Color getScreenBackground() {
        return app.getArdoise().getBackgroundColor();
    }

    public void change_image_tortue(String chemin) {
        app.getArdoise().change_image_tortue(app, chemin);
        app.editeur.generateTurtleImage();
    }

    public void initGraphics() {
        app.getArdoise().initGraphics();
    }

    public void buildPrimitiveTreemap(int id) {
        primitive.buildPrimitiveTreemap(id);
    }

    public String execute(StringBuffer st) throws xlogo.utils.myException {
        return interprete.execute(st);
    }

    protected void initCalculator(int s) {
        myCalculator = new MyCalculator(s, app);

    }

    public void initPrimitive() {
        primitive = new Primitive(app);
    }


    public void initInterprete() {
        interprete = new Interprete(app);
    }

    /**
     * Returns the InstructionBuffer containing all commands to execute
     */
    public InstructionBuffer getInstructionBuffer() {
        return interprete.getInstructionBuffer();
    }

    public MP3Player getMp3Player() {
        return mp3Player;
    }

    public void setMp3Player(MP3Player mp3Player) {
        this.mp3Player = mp3Player;
    }

    class ArrayFlow extends ArrayList<MyFlow> {
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
