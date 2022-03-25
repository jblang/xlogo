package xlogo.kernel.perspective;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import xlogo.Logo;
import xlogo.resources.ResourceLoader;
import xlogo.utils.ImageWriter;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * This Frame displays the 3D main Scene
 * <p>
 * BG scene
 * <p>
 * BG backBranchGroup | BG Mylight (4) |          BG
 * Background 		Light 1,2,3,4		Other objects
 */


public class Viewer3D extends JFrame {
    private static final long serialVersionUID = 1L;
    // To store the Light attributes
    private Light[] lights;
    // To store the Fog attributes
    private Fog fog;
    // Gui Components
    private World3D w3d;
    private Canvas3D canvas3D;
    /**
     * Main scene's Branchgroup
     */
    private BranchGroup scene;

    private BranchManager branchManager;
    private SimpleUniverse universe;
    private Color3f backgroundColor;

    private BranchGroup backBranchgroup;
    private Background back;


    public Viewer3D(World3D w3d, Color c) {
        this.w3d = w3d;
        this.backgroundColor = new Color3f(c);
        initGui();
    }

    void saveScreenshot() {
        ImageWriter wi = new ImageWriter(this, null);
        int id = wi.chooseFile();
        if (id == JFileChooser.APPROVE_OPTION) {
            GraphicsContext3D ctx = canvas3D.getGraphicsContext3D();
            java.awt.Dimension scrDim = canvas3D.getSize();

            // setting raster component
            Raster ras = new Raster(new Point3f(-1.0f, -1.0f, -1.0f), Raster.RASTER_COLOR, 0, 0, scrDim.width, scrDim.height, new ImageComponent2D(ImageComponent.FORMAT_RGB, new BufferedImage(scrDim.width, scrDim.height, java.awt.image.BufferedImage.TYPE_INT_RGB)), null);
            ctx.readRaster(ras);
            BufferedImage img = ras.getImage().getImage();
            wi.setImage(img);
            wi.start();
        }
    }

    public void setText() {
        setTitle(Logo.messages.getString("3d.viewer"));
    }

    private void initGui() {
        //this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(ResourceLoader.getAppIcon().getImage());
        getContentPane().setLayout(new BorderLayout());

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int size = Math.min(d.width, d.height) * 2 / 3;
        setSize(size, size);
        setLocationRelativeTo(null);

        canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universe = new SimpleUniverse(canvas3D);

        // Install the camera at the valid position with correct orientation
        TransformGroup tg = universe.getViewingPlatform().getViewPlatformTransform();
        Transform3D trans = new Transform3D();
        if (null == w3d) {
            w3d = new World3D();
        }
        trans.setTranslation(new Vector3d(-w3d.xCamera / 1000, -w3d.yCamera / 1000, -w3d.zCamera / 1000));
        Transform3D rot = new Transform3D();
        rot.lookAt(new Point3d(-w3d.xCamera / 1000, -w3d.yCamera / 1000, -w3d.zCamera / 1000), new Point3d(0, 0, 0), new Vector3d(0, 1, 0));
        rot.invert();
        trans.mul(rot);
        tg.setTransform(trans);
        OrbitBehavior ob = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
        ob.setRotationCenter(new Point3d(0, 0, 0));
        ob.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 2 * w3d.r / 1000));
        universe.getViewingPlatform().setViewPlatformBehavior(ob);

        // Create the root of the branch graph
        scene = new BranchGroup();
        branchManager = new BranchManager();
        // We can add New BranchGroup dynamically in the main scene
        scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        // We can remove BranchGroup dynamically in the main scene
        scene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        // Configure and create background
        createBackground();

        // Configure Lights
        initLights();

        // Configure Fog
        fog = new Fog(Fog.FOG_OFF, backgroundColor);
        scene.addChild(fog);

        // Rattachement de la scène 3D à l'univers
        universe.addBranchGraph(scene);

        var toolBar = new JToolBar();
        getContentPane().add(toolBar, BorderLayout.NORTH);

        var screenshotButton = new JButton(ResourceLoader.getIcon("screenshot"));
        screenshotButton.addActionListener(e -> saveScreenshot());
        toolBar.add(screenshotButton);
        toolBar.addSeparator();

        var bulb = ResourceLoader.getIcon("bulb");
        for (var i = 0; i < 4; i++) {
            final var light = lights[i];
            final var label = Integer.toString(i + 1);
            var button = new JButton(label, bulb);
            button.addActionListener(e -> {
                String title = Logo.messages.getString("3d.light") + " " + label;
                new LightDialog(this, light, title);
            });
            toolBar.add(button);
        }
        toolBar.addSeparator();
        var fogButton = new JButton(ResourceLoader.getIcon("cloud"));
        fogButton.addActionListener(e -> {
            new FogDialog(this, fog, Logo.messages.getString("3d.fog"));
        });
        toolBar.add(fogButton);

        getContentPane().add(canvas3D, BorderLayout.CENTER);
        setText();
        getContentPane().validate();
    }

    /**
     * This method adds a shape3D to the main scene
     *
     * @param s The Shape to add
     */
    public void add3DObject(Shape3D s) {
        branchManager.add3DObject(s);
    }

    public void add2DText(TransformGroup tg) {
        branchManager.add2DText(tg);
    }

    public void insertBranch() {
        branchManager.insertBranch();
        branchManager = new BranchManager();
    }

    /**
     * This methods adds two default PointLight int the 3d Scene
     */
    private void initLights() {
        lights = new Light[4];
        // First Default Point Light
        Color3f color = new Color3f(1f, 1f, 1f);
        Point3f pos = new Point3f((float) w3d.xCamera / 1000, (float) w3d.yCamera / 1000, (float) w3d.zCamera / 1000);
        lights[0] = new Light(Light.LIGHT_POINT, color, pos);

        // Second default Point Light
        pos = new Point3f(-(float) w3d.xCamera / 1000, -(float) w3d.yCamera / 1000, -(float) w3d.zCamera / 1000);
        lights[1] = new Light(Light.LIGHT_POINT, color, pos);

        lights[2] = new Light(Light.LIGHT_OFF);
        lights[3] = new Light(Light.LIGHT_OFF);
        for (int i = 0; i < 4; i++) {
            lights[i].createLight();
        }
        addAllLights(scene);
    }

    /**
     * Add all lights in the main 3D scene
     */
    private void addAllLights(BranchGroup bg) {
        for (int i = 0; i < 4; i++) {
            bg.addChild(lights[i]);
        }
    }

    /**
     * add a light in the main scene
     */
    protected void addNode(Node l) {
        scene.addChild(l);
    }

    /**
     * This methods erase all drawings on the 3D Viewer Drawing Area
     */
    public void clearScreen() {
        scene.removeAllChildren();
        createBackground();
        initLights();
    }

    public void updateBackground(Color c) {
        backgroundColor = new Color3f(c);
        backBranchgroup.detach();
        createBackground();
    }


    /**
     * This method creates the Background with the defined color
     */
    public void createBackground() {
        backBranchgroup = new BranchGroup();
        backBranchgroup.setCapability(BranchGroup.ALLOW_DETACH);
        backBranchgroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        back = new Background(backgroundColor);
        back.setApplicationBounds(new BoundingSphere());
        back.setCapability(Background.ALLOW_COLOR_WRITE);
        backBranchgroup.addChild(back);
        scene.addChild(backBranchgroup);
    }

    class BranchManager {
        BranchGroup bg;

        BranchManager() {
            bg = new BranchGroup();
            bg.setCapability(BranchGroup.ALLOW_DETACH);
        }

        /**
         * This method adds a shape3D to the main scene
         *
         * @param s The Shape to add
         */
        void add3DObject(Shape3D s) {
            bg.addChild(s);
        }

        void add2DText(TransformGroup tg) {
            bg.addChild(tg);
        }

        void insertBranch() {
            bg.compile();
            scene.addChild(bg);
        }
    }
}
