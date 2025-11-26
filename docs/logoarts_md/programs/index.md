# XLogo Programs

A collection of XLogo programs demonstrating various techniques and concepts.

To run any of these programs:

1. Hi-light **all** the code in the yellow box (click and drag with your mouse).
2. **Copy** the code (select Copy from your browser Edit menu).
3. Switch to **XLogo** and open the **Editor** window (click the Editor button).
4. **Paste** in the code (click the Paste button).
5. Save and close the Editor (click the **disk** button).
6. Type ' **go** ' in the command line and press <Enter>. XLogo will run the program.

## Animations

To create animations, XLogo must first be put into animation mode by the command **Animation** .

Drawing no longer updates the screen but instead changes are stored.

The **Refresh** command updates the graphics screen, as the next 'frame' in the animation. Usually done once per main loop of the program. All the stored changes appear immediately.

A camera icon in the Text Area will remind you animation is on. To return to normal drawing mode, enter **StopAnimation** , or click the camera icon.

| Thumbnail                                                              | Name                                   | Description                                                               |
| ---------------------------------------------------------------------- | -------------------------------------- | ------------------------------------------------------------------------- |
| [![LED Display](../art/32x32/led.gif)](grid/led.md)                    | [LED Display](grid/led.md)             | Scrolling dot matrix text.                                                |
| [![Microbes](../art/32x32/microbes.gif)](multi/microbes.md)            | [Microbes](multi/microbes.md)          | Animated single cell pond life.                                           |
| [![Pi Island](../art/32x32/pie.gif)](dotplot/pie.md)                   | [Pi Island](dotplot/pie.md)            | A random calculation of pi.                                               |
| [![Polygon Weaver](../art/32x32/polyw.gif)](walk/weaver.md)            | [Polygon Weaver](walk/weaver.md)       | Two random polygons are woven together.                                   |
| [![TreeAnim](../art/32x32/tree.gif)](tree/treeanim.md)                 | [TreeAnim](tree/treeanim.md)           | Recursive tree animation, branch angle from 0 to 180 degrees.             |
| [![Chaos Theory](../art/32x32/chaos.gif)](anim/chaos.md)               | [Chaos Theory](anim/chaos.md)          | Graphic representation of the chaotic behaviour of a simple equation.     |
| [![Cube](../art/32x32/cube.gif)](anim/cube.md)                         | [Cube](anim/cube.md)                   | Tumbling cube corners. Shows the kind of maths needed for 3D programming. |
| [![Dudeney's Disection](../art/32x32/dud.gif)](anim/dudeney.md)        | [Dudeney's Disection](anim/dudeney.md) | Old puzzle of an oscillating square to triangle disection.                |
| [![Linkages](../art/32x32/cheb32.gif)](anim/linkages.md)               | [Linkages](anim/linkages.md)           | Mechanical straight line motion.                                          |
| [![Oscilloscope](../art/32x32/osc.gif)](anim/oscilloscope.md)          | [Oscilloscope](anim/oscilloscope.md)   | Display of rotating sine waves from the days of old oscilloscopes.        |
| [![Peano Cesaro](../art/32x32/peanocesaro32.gif)](anim/peanocesaro.md) | [Peano Cesaro](anim/peanocesaro.md)    | Variable angle Peano curve.                                               |
| [![SpiroCircle](../art/32x32/spirocirc.gif)](anim/spirocircle.md)      | [SpiroCircle](anim/spirocircle.md)     | Morphing display of outer circle to different spiro / cycloid patterns.   |
| [![StarMorph](../art/32x32/starmorph.gif)](anim/starmorph.md)          | [StarMorph](anim/starmorph.md)         | Evolving red and green star shape, moving and rotating.                   |
| [![Tangram](../art/32x32/tangram.gif)](anim/tang.md)                   | [Tangram](anim/tang.md)                | Tangram pieces sliding between random pictures.                           |

## Art Works

A set of general art programs which, simply, do not fit into any of the other sections.

Some images are generated randomly, and so will produce a different work of art each time.

| Thumbnail                                                        | Name                              | Description                                              |
| ---------------------------------------------------------------- | --------------------------------- | -------------------------------------------------------- |
| [![Border](../art/32x32/border32.gif)](art/border.md)            | [Border](art/border.md)           | Double border frame.                                     |
| [![Cardioid Curve](../art/32x32/cardioid.gif)](art/cardioid.md)  | [Cardioid Curve](art/cardioid.md) | Circles of touching radii.                               |
| [![Clover Art](../art/32x32/clover_art32.gif)](art/cloverart.md) | [Clover Art](art/cloverart.md)    | Random clover leaf/wave shapes.                          |
| [![Condensation](../art/32x32/cond.gif)](art/cond.md)            | [Condensation](art/cond.md)       | Expanding water droplets.                                |
| [![Eye](../art/32x32/eye32.gif)](art/eye.md)                     | [Eye](art/eye.md)                 | Random filled sector shapes.                             |
| [![Fields](../art/32x32/field.gif)](art/fields.md)               | [Fields](art/fields.md)           | Fields of random stress lines.                           |
| [![Flowers](../art/32x32/flowers.gif)](art/flowers.md)           | [Flowers](art/flowers.md)         | A bunch of flowers demonstrating good use of procedures. |
| [![Ice](../art/32x32/ice.gif)](art/ice.md)                       | [Ice](art/ice.md)                 | Randomly cracked ice sheet.                              |
| [![Petals](../art/32x32/petal.gif)](art/petals.md)               | [Petals](art/petals.md)           | A series of 7 flowers.                                   |
| [![Shapes Confetti](../art/32x32/stars.gif)](art/shapes.md)      | [Shapes Confetti](art/shapes.md)  | Random pile of colored shapes.                           |
| [![Wire Shapes](../art/32x32/wire.gif)](art/wire.md)             | [Wire Shapes](art/wire.md)        | A random collection of wires.                            |

## Cellular Automata

In any cellular automata (CA) program, the screen is divided up into a number of square cells.

The color of each cell represents its state. At the start, all cells are black (state 0).

The turtle starts in one cell. To determine what to do next, the turtle finds the color (state) of its current and neighbouring cells.

This information tells the turtle which cells to recolor (alter the state of) and which neighbouring cell to move to next.

The result is a pattern of different colored cells.

| Thumbnail                                                   | Name                           | Description               |
| ----------------------------------------------------------- | ------------------------------ | ------------------------- |
| [![Langton's Ant](../art/32x32/langant.gif)](ca/langant.md) | [Langton's Ant](ca/langant.md) | Simple 2D Turing machine. |
| [![SingleLine CA](../art/32x32/ca.gif)](ca/caline.md)       | [SingleLine CA](ca/caline.md)  | Line by line.             |

## Coding

These programs help in some aspects of coding and the creation of more programs. They are kind of XLogo utilities. Some are also shown in the galleries.

| Thumbnail                                                   | Name                           | Description                                           |
| ----------------------------------------------------------- | ------------------------------ | ----------------------------------------------------- |
| [![Characters](../art/32x32/char32.gif)](coding/char.md)    | [Characters](coding/char.md)   | Draw font characters.                                 |
| [![Chequer](../art/32x32/chequer32.gif)](coding/chequer.md) | [Chequer](coding/chequer.md)   | Demo column by row grid.                              |
| [![Dots](../art/logo32/dots32.gif)](coding/dots.md)         | [Dots](coding/dots.md)         | Fill screen with random dots.                         |
| [![RGB Colors](../art/32x32/rgb.gif)](coding/rgb.md)        | [RGB Colors](coding/rgb.md)    | See the color effects of different AngCol procedures. |
| [![RGB Swatches](../art/32x32/rgb32.gif)](coding/rgbs.md)   | [RGB Swatches](coding/rgbs.md) | Array of RGB color values.                            |
| [![Shapes](../art/32x32/h_shoe32.gif)](coding/shapes.md)    | [Shapes](coding/shapes.md)     | Outline shapes with labelled arch and arcs.           |
| [![Swatches](../art/32x32/swatch.gif)](coding/colors.md)    | [Swatches](coding/colors.md)   | XLogo colors 0 to 16.                                 |
| [![Tracker](../art/logo32/track.gif)](coding/tracker.md)    | [Tracker](coding/tracker.md)   | On screen tracking of any program variable.           |
| [![X Hair](../art/logo32/xhair.gif)](coding/xhair.md)       | [X Hair](coding/xhair.md)      | Add a X-Hair to any XLogo program.                    |

## XLogo Demos


| Thumbnail                                                          | Name                               | Description                                     |
| ------------------------------------------------------------------ | ---------------------------------- | ----------------------------------------------- |
| [![Chessboard](../art/32x32/chessboard32.gif)](demo/chessboard.md) | [Chessboard](demo/chessboard.md)   | Draw a chessboard.                              |
| [![Clover Leaf/Wave](../art/32x32/clover32.gif)](demo/clover.md)   | [Clover Leaf/Wave](demo/clover.md) | Draw a clover leaf or wave shape.               |
| [![Five Star](../art/32x32/star32.gif)](demo/star.md)              | [Five Star](demo/star.md)          | Nested series of 5 pointed stars.               |
| [![Lens](../art/32x32/lens32.gif)](demo/lens.md)                   | [Lens](demo/lens.md)               | Lens shapes.                                    |
| [![Pebbles](../art/32x32/pebble32.gif)](demo/pebble.md)            | [Pebbles](demo/pebble.md)          | Randoml pebble shapes.                          |
| [![Please Wait](../art/32x32/pwait.gif)](demo/pwait.md)            | [Please Wait](demo/pwait.md)       | Randomly generated computer excuses.            |
| [![Polygons](../art/32x32/polygons.gif)](demo/polygon.md)          | [Polygons](demo/polygon.md)        | A set of nested polygons.                       |
| [![Rainbow](../art/32x32/rainbow.gif)](demo/rainbow.md)            | [Rainbow](demo/rainbow.md)         | Six colored arcs.                               |
| [![Random Lines](../art/32x32/lines.gif)](demo/randlines.md)       | [Random Lines](demo/randlines.md)  | A dozen random lines with co-ordinate labels.   |
| [![Screen Saver](../art/32x32/saver.gif)](demo/saver1.md)          | [Screen Saver](demo/saver1.md)     | A couple of large graphic stars in a night sky. |
| [![Star](../art/32x32/demo.gif)](demo/demostar.md)                 | [Star](demo/demostar.md)           | A couple of large graphic stars in a night sky. |
| [![Star Shapes](../art/32x32/stars32.gif)](demo/stars.md)          | [Star Shapes](demo/stars.md)       | Star outline shape.                             |

## Dot Plots

These images are generated by plotting a (usually large) number of discreet dots. No lines are drawn between dots.

Some of the images are created from mathematical equations, and can take a while to complete.

If a mapping function is used then they are called 'Iterative Function Systems' (IFS).

In XLogo, the dot command plots a single pixel in the pen color at the specified XY co-ordinates. The turtle does not move to the new position to draw the dot.

| Thumbnail                                                            | Name                                    | Description                                       |
| -------------------------------------------------------------------- | --------------------------------------- | ------------------------------------------------- |
| [![Dots](../art/logo32/dots32.gif)](coding/dots.md)                  | [Dots](coding/dots.md)                  | Fill screen with random dots.                     |
| [![Bifurcation Diagram](../art/32x32/bifur.gif)](dotplot/bifur.md)   | [Bifurcation Diagram](dotplot/bifur.md) | Plot of f(x)=kx(1-x)                              |
| [![Dust](../art/32x32/dust.gif)](dotplot/dust.md)                    | [Dust](dotplot/dust.md)                 | Floating specks of dust gather together.          |
| [![Hopalong](../art/32x32/hop32.gif)](dotplot/hop.md)                | [Hopalong](dotplot/hop.md)              | Random hopalong function plots.                   |
| [![Fractal Fern](../art/32x32/fern.gif)](dotplot/fern.md)            | [Fractal Fern](dotplot/fern.md)         | Chaos Game rendition of a fern. (IFS)             |
| [![Ginger Bread Man](../art/32x32/gbman.gif)](dotplot/gbman.md)      | [Ginger Bread Man](dotplot/gbman.md)    | A linear map in the shape of a gingerbread man.   |
| [![Hénon Curves](../art/32x32/henon.gif)](dotplot/henon.md)          | [Hénon Curves](dotplot/henon.md)        | Two dimensional quadratic maps.                   |
| [![Kam Torus](../art/32x32/kam.gif)](dotplot/kam.md)                 | [Kam Torus](dotplot/kam.md)             | A quick dot plot image.                           |
| [![Mandelbrot](../art/32x32/mand.gif)](dotplot/mand.md)              | [Mandelbrot](dotplot/mand.md)           | Classic Mandelbrot image.                         |
| [![Martin](../art/32x32/martin.gif)](dotplot/martin.md)              | [Martin](dotplot/martin.md)             | A patch work quilt of extended squares.           |
| [![Pi Island](../art/32x32/pie.gif)](dotplot/pie.md)                 | [Pi Island](dotplot/pie.md)             | A random calculation of pi.                       |
| [![Popcorn](../art/32x32/popcorn32.gif)](dotplot/popcorn.md)         | [Popcorn](dotplot/popcorn.md)           | Strange quilted popcorn fractal.                  |
| [![Sierpinski Gasket](../art/32x32/sierpinski.gif)](dotplot/sier.md) | [Sierpinski Gasket](dotplot/sier.md)    | Recursive generation of sierpinski gaskets. (IFS) |

## Fractals

Fractal curves are self similar. They are constructed from many similar shapes scaled up (made bigger) or down (smaller).

Moutains, clouds and trees are examples of natural fractals.

| Thumbnail                                                           | Name                                    | Description                            |
| ------------------------------------------------------------------- | --------------------------------------- | -------------------------------------- |
| [![Blancmange Curve](../art/32x32/blam.gif)](fractal/blanc.md)      | [Blancmange Curve](fractal/blanc.md)    | Blancmange curve.                      |
| [![C_Curve](../art/32x32/c_curve.gif)](fractal/c_curve.md)          | [C_Curve](fractal/c_curve.md)           | Fractal C_Curves, orders 0-9.          |
| [![Chiral Star](../art/32x32/star32.gif)](fractal/chiral.md)        | [Chiral Star](fractal/chiral.md)        | Recursive star gasket.                 |
| [![Gasket](../art/32x32/gasket.gif)](fractal/gasket.md)             | [Gasket](fractal/gasket.md)             | Recursive hole removal.                |
| [![Koch Curve](../art/32x32/koch.gif)](fractal/koch.md)             | [Koch Curve](fractal/koch.md)           | Both inside and outside Koch curves.   |
| [![Koch (Square) Curve](../art/32x32/kochsq.gif)](fractal/koch2.md) | [Koch (Square) Curve](fractal/koch2.md) | Inside and outside Koch square curves. |
| [![Lace Curve](../art/32x32/lace32.gif)](fractal/lace.md)           | [Lace Curve](fractal/lace.md)           | Lace plane filling curve.              |

## Grids

These programs are all based on a strong grid structure of vertical and horizontal lines.

A grid is created by using a pair of nested loops. These generate the XY co-ordinates of each cell within the grid.

| Thumbnail                                                      | Name                              | Description                             |
| -------------------------------------------------------------- | --------------------------------- | --------------------------------------- |
| [![Cubes](../art/32x32/cubeart32.gif)](grid/cubes.md)          | [Cubes](grid/cubes.md)            | Cubic art sculpture.                    |
| [![Drain](../art/32x32/drain.gif)](grid/drain.md)              | [Drain](grid/drain.md)            | Water drainage systems.                 |
| [![Elastic Grids](../art/32x32/elastic.gif)](grid/elasgrid.md) | [Elastic Grids](grid/elasgrid.md) | Mathematically distorted grids.         |
| [![Grid Art](../art/32x32/grid.gif)](grid/gridart.md)          | [Grid Art](grid/gridart.md)       | A regular array of assembled squares.   |
| [![Hexagrams](../art/32x32/hexagram.gif)](grid/hexagram.md)    | [Hexagrams](grid/hexagram.md)     | Ancient chinese binary fortune telling. |
| [![LED Display](../art/32x32/led.gif)](grid/led.md)            | [LED Display](grid/led.md)        | Scrolling dot matrix text.              |
| [![Maze](../art/32x32/maze32.gif)](grid/maze.md)               | [Maze](grid/maze.md)              | Self creating random mazes.             |
| [![Oct Tiles](../art/32x32/octtile32.gif)](grid/octtiles.md)   | [Oct Tiles](grid/octtiles.md)     | Random pattern edge matching tiles.     |
| [![Surface](../art/32x32/surface32.gif)](grid/surface.md)      | [Surface](grid/surface.md)        | 3D surface grids.                       |
| [![Tile Array](../art/32x32/tiles32/32.gif)](grid/tile_a.md)   | [Tile Array](grid/tile_a.md)      | Various tile arrays.                    |
| [![Tile Pattern](../art/32x32/tiles32/06.gif)](grid/tile_p.md) | [Tile Pattern](grid/tile_p.md)    | Various tile patterns.                  |

## Illusions

These programs create visual optical illusions. Some are famous, others I've just come across by experimenting.

You can of course use XLogo to alter the features of each illusion, and discover even stranger and stronger distortions.

| Thumbnail                                                                | Name                                   | Description                                          |
| ------------------------------------------------------------------------ | -------------------------------------- | ---------------------------------------------------- |
| [![After Image](../art/32i2/afterim32.gif)](illusion/afterim.md)         | [After Image](illusion/afterim.md)     | Negative color appearance.                           |
| [![Assimilation](../art/32i2/assim32.gif)](illusion/assim.md)            | [Assimilation](illusion/assim.md)      | Color blending.                                      |
| [![Bulge](../art/32i1/bulge32.gif)](illusion/bulge.md)                   | [Bulge](illusion/bulge.md)             | Bulging grid.                                        |
| [![Cafe Wall](../art/32i1/cafewall32.gif)](illusion/cafewall.md)         | [Cafe Wall](illusion/cafewall.md)      | Distorted tile pattern.                              |
| [![Circles](../art/32i1/circle32.gif)](illusion/circle.md)               | [Circles](illusion/circle.md)          | Relative shape sizes.                                |
| [![Color Names](../art/32i2/colorname32.gif)](illusion/colors.md)        | [Color Names](illusion/colors.md)      | Left right brain confusion.                          |
| [![Fraser Illusion](../art/32i2/fraser32.gif)](illusion/fraser.md)       | [Fraser Illusion](illusion/fraser.md)  | Twisted vertical lines.                              |
| [![Grid](../art/32i1/grid32.gif)](illusion/grid.md)                      | [Grid](illusion/grid.md)               | Distorted square grid.                               |
| [![Hermann Grid](../art/32i1/hermann32.gif)](illusion/hermann.md)        | [Hermann Grid](illusion/hermann.md)    | Intersecting dark spots.                             |
| [![Hexagon](../art/32i1/hexagon32.gif)](illusion/hexagon.md)             | [Hexagon](illusion/hexagon.md)         | Distorted figure.                                    |
| [![Intertwine](../art/32i1/intertwine32.gif)](illusion/intertwine.md)    | [Intertwine](illusion/intertwine.md)   | Intertwined concentric circles.                      |
| [![Kanizsa](../art/32i2/kanizsa32.gif)](illusion/kanizsa.md)             | [Kanizsa](illusion/kanizsa.md)         | Illusory floating triangle shape.                    |
| [![Marbles](../art/32i2/marbles32.gif)](illusion/marbles.md)             | [Marbles](illusion/marbles.md)         | Op art distortion.                                   |
| [![Negative Circles](../art/32i2/n_circ32.gif)](illusion/n_circ.md)      | [Negative Circles](illusion/n_circ.md) | Floating shapes.                                     |
| [![Neon](../art/32i1/neon32.gif)](illusion/neon.md)                      | [Neon](illusion/neon.md)               | Square patch of color.                               |
| [![Orbinson](../art/32i1/orb32.gif)](illusion/orb.md)                    | [Orbinson](illusion/orb.md)            | Distorted square.                                    |
| [![Revolve](../art/32i1/revolve32.gif)](illusion/revolve.md)             | [Revolve](illusion/revolve.md)         | Two revolving rings.                                 |
| [![Scintillate](../art/32i1/scintillate32.gif)](illusion/scintillate.md) | [Scintillate](illusion/scintillate.md) | Scintillating tile pattern.                          |
| [![Seaweed](../art/32i2/seaweed32.gif)](illusion/seaweed.md)             | [Seaweed](illusion/seaweed.md)         | Waving tile pattern illusion.                        |
| [![Segments](../art/32i2/segments32.gif)](illusion/segments.md)          | [Segments](illusion/segments.md)       | Similar segment shapes.                              |
| [![Sine](../art/32i1/sine32.gif)](illusion/sine.md)                      | [Sine](illusion/sine.md)               | Sine wave illusion.                                  |
| [![Spiral](../art/32i1/spiral32.gif)](illusion/spiral.md)                | [Spiral](illusion/spiral.md)           | Concentric circle spirals.                           |
| [![Tube](../art/32i2/tube32.gif)](illusion/tube.md)                      | [Tube](illusion/tube.md)               | Continuous swaying motion.                           |
| [![Twisted Cord](../art/32i2/twist32.gif)](illusion/twist.md)            | [Twisted Cord](illusion/twist.md)      | Vertical cords appear to bend.                       |
| [![Wave](../art/32i1/wave32.gif)](illusion/wave.md)                      | [Wave](illusion/wave.md)               | Continuous wave motion.                              |
| [![White's Illusion](../art/32i2/white32.gif)](illusion/white.md)        | [White's Illusion](illusion/white.md)  | Gray rectangles appear different colors behind bars. |
| [![ZigZag](../art/32i2/zigzag32.gif)](illusion/zigzag.md)                | [ZigZag](illusion/zigzag.md)           | Distorted lines.                                     |
| [![Zollner](../art/32i1/zollner32.gif)](illusion/zollner.md)             | [Zollner](illusion/zollner.md)         | Distorted horizontal lines in tiled grid.            |
| [![Zoom](../art/32i1/zoom32.gif)](illusion/zoom.md)                      | [Zoom](illusion/zoom.md)               | Continuous zoom motion.                              |

## Lindenmayer Systems

L-Systems were invented in 1968 by Aristid Lindenmayer to model biological growth. They provide a flexible method for drawing many curves and fractals.

They start with an axiom list, and then use a set of production rules to modify the elements in this list. This process of string substitutions is then repeated at each iteration. It can create very long lists.

A second procedure then renders the list, by translating each element into a turtle command which draws the final curve.

Also see .

| Thumbnail                                                              | Name                                      | Description                                     |
| ---------------------------------------------------------------------- | ----------------------------------------- | ----------------------------------------------- |
| [![Dragon Curve](../art/32x32/dragon.gif)](l-system/dragon.md)         | [Dragon Curve](l-system/dragon.md)        | Curve of Dragon like form.                      |
| [![L-System Single Rule](../art/32x32/lsys132.gif)](l-system/lsys1.md) | [L-System Single Rule](l-system/lsys1.md) | Seven L-Systems with single F replacement rule. |
| [![L-System Double Rule](../art/32x32/lsys232.gif)](l-system/lsys2.md) | [L-System Double Rule](l-system/lsys2.md) | Five L-Systems with single F replacement rule.  |
| [![Weeds](../art/32x32/weed.gif)](l-system/weeds.md)                   | [Weeds](l-system/weeds.md)                | Natural forms weeds, leaves and trees.          |

## Multi Turtle

XLogo allows a number of turtles to be on screen at the same time. The maximum number is defined in the options menu.

Use the command **SetTurtle x** to control tutle number x. If the turtle does not already exist, it will be created. Newly created turtles are hidden and start at the home position.

| Thumbnail                                                      | Name                              | Description                                                          |
| -------------------------------------------------------------- | --------------------------------- | -------------------------------------------------------------------- |
| [![Elastic Grids](../art/32x32/elastic.gif)](grid/elasgrid.md) | [Elastic Grids](grid/elasgrid.md) | A second turtle is used to convert rectangular to polar coordinates. |
| [![Microbes](../art/32x32/microbes.gif)](multi/microbes.md)    | [Microbes](multi/microbes.md)     | Animated single cell pond life.                                      |
| [![Orbits](../art/32x32/orbit.gif)](multi/orbit.md)            | [Orbits](multi/orbit.md)          | Spirograph type patterns generated by orbiting turtles.              |
| [![Pursuit Curve](../art/32x32/pursuit.gif)](multi/pursuit.md) | [Pursuit Curve](multi/pursuit.md) | Turtle following turtle following turtle...                          |
| [![Snowflakes](../art/32x32/sflake.gif)](multi/snowflake.md)   | [Snowflakes](multi/snowflake.md)  | Random ice crystal  patterns.                                        |

## One Liners

Copy and paste any **one** line of code into the XLogo command line and press <Enter> key to run. Widen the XLogo window to easily see and edit a long instruction.

You may need to first type in **CS** to clear the screen. You can change the default pen and screen color by selecting Tools / Preferences / Options from the XLogo menu.

To slow down the drawing speed add in your own **Wait** commands.

Try and work out the image each code will produce before you run it...

## View 3D

These programs use perspective mode to allow the turtle to move in 3D space. This adds a depth Z value to the normal 2D X and Y values.

Building shapes in 3D is not as easy as 2D. The turtle can roll and pitch, so there are often several different ways of drawing a given shape.

A 2D square has 4 edges and 4 corners. In 3D a cube has 6 faces, 8 corners and 12 edges.

Entering **View 3D** displays the lines, points and surfaces in fully rotateable 3D. Lights and fog can be added.

Note: Type in the command CS View3D to clear the View3D window and prevent too many shapes building up.

| Thumbnail                                                         | Name                               | Description                                  |
| ----------------------------------------------------------------- | ---------------------------------- | -------------------------------------------- |
| [![Cube Octahedron](../art/32p1/cubeo32.gif)](pers/cubeoct.md)    | [Cube Octahedron](pers/cubeoct.md) | Wire frame and balls cube octahedon.         |
| [![Dice](../art/32p1/dice32.gif)](pers/dice.md)                   | [Dice](pers/dice.md)               | Colored dice, correctly labelled.            |
| [![Face Cube](../art/32p1/fcube32.gif)](pers/fcube.md)            | [Face Cube](pers/fcube.md)         | Cube face 'parasols' recursion.              |
| [![Knight's Tour](../art/32p1/array32.gif)](pers/ktour.md)        | [Knight's Tour](pers/ktour.md)     | Cube 4x4x4 array knight's tour.              |
| [![Mobius Band](../art/32p1/mobius32.gif)](pers/mobius.md)        | [Mobius Band](pers/mobius.md)      | Twisted paper loop.                          |
| [![Platonic Solid](../art/32p1/platonic32.gif)](pers/platonic.md) | [Platonic Solid](pers/platonic.md) | All 5 regular platonic solids.               |
| [![XYZ Axis](../art/32p1/xyz32.gif)](pers/xyz.md)                 | [XYZ Axis](pers/xyz.md)            | Demo of XYZ relative and absolute positions. |

## Plane Filling Curves

Plane filling cuvres are a continueous line through a flat (2D) space.

By using a recursive procedure, the curve becomes ever more detailed. It increases in length and at its limit will visit every point in the space.

Hence, the enclosed 2D space has been filled with a curve of infinite length.

We cannot iterate an infinite number of times. Hence we can only draw an approximate curve by iterating just a few times.

Also see .

| Thumbnail                                                         | Name                                | Description                                         |
| ----------------------------------------------------------------- | ----------------------------------- | --------------------------------------------------- |
| [![Dragon Curve](../art/32x32/dragon.gif)](plane/dragon.md)       | [Dragon Curve](plane/dragon.md)     | Folded paper strip Dragon curve, orders 0-11.       |
| [![Grate Curve](../art/32x32/grate.gif)](plane/grate.md)          | [Grate Curve](plane/grate.md)       | Recursive plane filling Grate curve.                |
| [![Hilbert Curve](../art/32x32/hilbert.gif)](plane/hilbert.md)    | [Hilbert Curve](plane/hilbert.md)   | Recursive Hilbert curves.                           |
| [![Hilbert2 Curve](../art/32x32/hilbert2.gif)](plane/hilbert2.md) | [Hilbert2 Curve](plane/hilbert2.md) | Recursive Hilbert2 curves.                          |
| [![Knuth Curve](../art/32x32/knuth.gif)](plane/knuth.md)          | [Knuth Curve](plane/knuth.md)       | Recursive plane filling Knuth curve.                |
| [![Peano Curve](../art/32x32/filler.gif)](plane/peano.md)         | [Peano Curve](plane/peano.md)       | Recursive plane filling Peano curve.                |
| [![Peano2 Curve](../art/32x32/peano2.gif)](plane/peano2.md)       | [Peano2 Curve](plane/peano2.md)     | Recursive plane filling second type of Peano curve. |
| [![Peano Gosper Curve](../art/32x32/pg32.gif)](plane/pg.md)       | [Peano Gosper Curve](plane/pg.md)   | Recursive hexagonal plane filling curve.            |
| [![Sierpinski Curve](../art/32x32/sier.gif)](plane/sier.md)       | [Sierpinski Curve](plane/sier.md)   | Recursive plane filling Sierpinski curve.           |
| [![Sierpinski Triangle](../art/32x32/sier32.gif)](plane/stc.md)   | [Sierpinski Triangle](plane/stc.md) | Recursive triangular plane filling curve.           |
| [![Wirth Curve](../art/32x32/wirth.gif)](plane/wirth.md)          | [Wirth Curve](plane/wirth.md)       | Recursive plane filling Wirth curve.                |

## Polar Curves

All these curves are generated using equations in polar (Radius and Theta) form.

Usually, we plot points using X Y co-ordinates, (or Rectangular notation). A pair of numbers, define the horizontal and vertical distance from the origin. In XLogo, the command **SetXY** moves the turtle to any XY point on the screen.

Alternatively, we can use polar co-ordinates. These again use a pair of numbers. The first (called Radius) gives the distance from the origin. The second (called Theta) gives the angle or bearing (measured from North).

Again, we can move the turtle to any point on the screen. Note that using polar notation, points can be expressed in more than one way. We can turn in a positive or negative direction. Also the Radius can be positive (forward) or negative (back).

Sometimes, they are then converted into X Y co-ordinates for drawing by the turtle using the **SetXY** command by the procedure **P2R** (polar to rectangular). It requires two varibles, Radius and Theta and outputs the X and Y co-ordinates as a list.

| Thumbnail                                                               | Name                                      | Description                                       |
| ----------------------------------------------------------------------- | ----------------------------------------- | ------------------------------------------------- |
| [![Butterfly Curve](../art/32x32/bfly.gif)](polar/bfly.md)              | [Butterfly Curve](polar/bfly.md)          | The butterfly curve.                              |
| [![Flora](../art/32x32/flora.gif)](polar/flora.md)                      | [Flora](polar/flora.md)                   | Scattering of colored flower petals.              |
| [![Rose Curves](../art/32x32/rosec.gif)](polar/polarrose.md)            | [Rose Curves](polar/polarrose.md)         | Various rose curves.                              |
| [![Spirals](../art/32x32/archrose.gif)](polar/polarspirals.md)          | [Spirals](polar/polarspirals.md)          | Archimedies, Fermat and logarithmic spirals.      |
| [![Sunflower](../art/32x32/sunflower.gif)](polar/sunflower.md)          | [Sunflower](polar/sunflower.md)           | Entwined logarithmic spirals of a sunflower head. |
| [![Twisted Rose Curve](../art/32x32/trose.gif)](polar/polartwist.md)    | [Twisted Rose Curve](polar/polartwist.md) | Various twisted rose curves.                      |
| [![Twisted R C Anim](../art/32x32/rosecurves.gif)](polar/rosecurves.md) | [Twisted R C Anim](polar/rosecurves.md)   | A series of 20 twisted curves.                    |

## Puzzles

A selection of puzzles and simple games. Some are self solving, others require user input from the mouse or keyboard.

ReadChar and ReadMouse primitives are used to read keyboard key presses and mouse movement.

| Thumbnail                                                                | Name                                    | Description                               |
| ------------------------------------------------------------------------ | --------------------------------------- | ----------------------------------------- |
| [![15 Puzzle](../art/32x32/15.gif)](puzzle/15.md)                        | [15 Puzzle](puzzle/15.md)               | Sam Lloyds classsic sliding block puzzle. |
| [![Knight's Tour](../art/32x32/ktour32.gif)](puzzle/ktour.md)            | [Knight's Tour](puzzle/ktour.md)        | Classic knights tour of a chessboard.     |
| [![Partridge Puzzle](../art/32x32/partridge32.gif)](puzzle/partridge.md) | [Partridge Puzzle](puzzle/partridge.md) | Multiple square disection.                |
| [![Rings](../art/32x32/rings.gif)](puzzle/rings.md)                      | [Rings](puzzle/rings.md)                | Interlocking rotating rings puzzle.       |

## Recursion

Recursion occours when a procedure calls itself. This leads to some efficient programming techniques.

To prevent an infinite loop, the first line is usually a breakout test. If a variable eg size or line length is too small, the command **stop** breaks out of the procedure.

| Thumbnail                                                                  | Name                                      | Description                                         |
| -------------------------------------------------------------------------- | ----------------------------------------- | --------------------------------------------------- |
| [![Antenna Tree](../art/32x32/anttree.gif)](recur/anttree.md)              | [Antenna Tree](recur/anttree.md)          | Antenna Tree curve.                                 |
| [![Binary Switch](../art/32x32/switch.gif)](recur/switch.md)               | [Binary Switch](recur/switch.md)          | A divide by two selector switch.                    |
| [![Circles](../art/32x32/circles32.gif)](recur/circles.md)                 | [Circles](recur/circles.md)               | Recursive circles.                                  |
| [![Cross](../art/32x32/cross.gif)](recur/cross.md)                         | [Cross](recur/cross.md)                   | A series of recursive crosses.                      |
| [![Edge](../art/32x32/edge32.gif)](recur/edge.md)                          | [Edge](recur/edge.md)                     | A recursive edge of tiles.                          |
| [![GoldSqrs](../art/32x32/goldsqrs.gif)](recur/goldsqrs.md)                | [GoldSqrs](recur/goldsqrs.md)             | Recursive Fibbonacci sized squares fractal.         |
| [![Hexagons](../art/32x32/hexagons.gif)](recur/hexagons.md)                | [Hexagons](recur/hexagons.md)             | A series of recursive hexagons.                     |
| [![Mesh](../art/32x32/mesh32.gif)](recur/mesh.md)                          | [Mesh](recur/mesh.md)                     | Regular lattice.                                    |
| [![Pentagons](../art/32x32/pentagon.gif)](recur/pentagons.md)              | [Pentagons](recur/pentagons.md)           | Series of recursive pentagons.                      |
| [![Sphinx](../art/32x32/sphinx32.gif)](recur/sphinx.md)                    | [Sphinx](recur/sphinx.md)                 | Repeating sphinx shape.                             |
| [![Spinning Squares](../art/32x32/spinningsqs.gif)](recur/spinningsqrs.md) | [Spinning Squares](recur/spinningsqrs.md) | A series of recursive squares one inside the other. |
| [![Squares Center](../art/32x32/squares.gif)](recur/squares.md)            | [Squares Center](recur/squares.md)        | A series of recursive centered squares.             |
| [![Squares Corner](../art/32x32/squaresv.gif)](recur/squaresv.md)          | [Squares Corner](recur/squaresv.md)       | A series of recursive corner squares.               |
| [![YinYang](../art/32x32/yinyang32.gif)](recur/yinyang.md)                 | [YinYang](recur/yinyang.md)               | YinYang symbol.                                     |

## Sound

A selection of programs that use the sound primitives to create audio.

## Spirals

The following programs exhibit spiral forms. Spirals are common structures in mathematics and nature.

Also see:

| Thumbnail                                                                | Name                                      | Description                                            |
| ------------------------------------------------------------------------ | ----------------------------------------- | ------------------------------------------------------ |
| [![Pursuit Curve](../art/32x32/pursuit.gif)](multi/pursuit.md)           | [Pursuit Curve](multi/pursuit.md)         | Turtle following turtle.                               |
| [![Spirals](../art/32x32/archrose.gif)](polar/polarspirals.md)           | [Spirals](polar/polarspirals.md)          | Archimedies, Fermat and logarithmic spirals.           |
| [![Sunflower](../art/32x32/sunflower.gif)](polar/sunflower.md)           | [Sunflower](polar/sunflower.md)           | Entwined logarithmic spirals of a sunflower head.      |
| [![Arc Wave](../art/32x32/arcwave32.gif)](spiral/arcwave.md)             | [Arc Wave](spiral/arcwave.md)             | Series of decreasing sectors.                          |
| [![Cycloid Curves](../art/32x32/cycloid32.gif)](spiral/cycloid.md)       | [Cycloid Curves](spiral/cycloid.md)       | A series of various Cycloid curves.                    |
| [![Golden Spiral](../art/32x32/golden.gif)](spiral/goldenspiral.md)      | [Golden Spiral](spiral/goldenspiral.md)   | A rectangle divided into a series of smaller squares.  |
| [![Plastic Number](../art/32x32/plastic32.gif)](spiral/plastic.md)       | [Plastic Number](spiral/plastic.md)       | A logarithmic spiral of increasing triangles.          |
| [![Rose](../art/32x32/rose.gif)](spiral/spiralrose.md)                   | [Rose](spiral/spiralrose.md)              | Eight logo spirals.                                    |
| [![Spiral of Squares](../art/32x32/spiralsqs.gif)](spiral/spiralsqrs.md) | [Spiral of Squares](spiral/spiralsqrs.md) | A series of descending squares.                        |
| [![Spiral Lines](../art/32x32/spiral.gif)](spiral/spirallines.md)        | [Spiral Lines](spiral/spirallines.md)     | A variety of spiral forms from recursive line lengths. |
| [![Spirals (Curvature)](../art/32x32/spirallog.gif)](spiral/spirals.md)  | [Spirals (Curvature)](spiral/spirals.md)  | Archimedies and logarithmic spirals.                   |
| [![Ulam Spiral](../art/32x32/ulam32.gif)](spiral/ulamsp.md)              | [Ulam Spiral](spiral/ulamsp.md)           | Prime number spiral pattern.                           |

## Spirographs

These programs follow the techniques of the old spirograph toy, without the leaky pens.

Essentially, a rotating wheel orbits a fixed circle. The resulting curve can be defined in terms of sines and cosines (trigonometry).

| Thumbnail                                                           | Name                                 | Description                                          |
| ------------------------------------------------------------------- | ------------------------------------ | ---------------------------------------------------- |
| [![Ferris Wheels](../art/32x32/ferris.gif)](spiro/ferris.md)        | [Ferris Wheels](spiro/ferris.md)     | Random three term spirograph patterns.               |
| [![Harmonograph](../art/32x32/harm.gif)](spiro/harm.md)             | [Harmonograph](spiro/harm.md)        | Random multi-colored harmonograph patterns.          |
| [![Mystic Rose](../art/32x32/mrose.gif)](spiro/spiromr.md)          | [Mystic Rose](spiro/spiromr.md)      | Linking evenly spaced points on a circle.            |
| [![Spiro](../art/32x32/spiro.gif)](spiro/spiro.md)                  | [Spiro](spiro/spiro.md)              | Random generation of second order Spirograph curves. |
| [![SpiroExplorer](../art/32x32/spirotest.gif)](spiro/spiroexplo.md) | [SpiroExplorer](spiro/spiroexplo.md) | Experiment and test out spirograph parameters.       |
| [![Spirographs](../art/32x32/spirograph.gif)](spiro/spirog.md)      | [Spirographs](spiro/spirog.md)       | Traditional Spirograph patterns.                     |
| [![SpiroLaterals](../art/32x32/lateral.gif)](spiro/laterals.md)     | [SpiroLaterals](spiro/laterals.md)   | Traditional repeating move and turn patterns.        |
| [![String](../art/32x32/loom.gif)](spiro/string.md)                 | [String](spiro/string.md)            | String pictures.                                     |

## Trees

These trees are grown using recursive techniques. Each branch splits into two smaller branches, until the branch length is too small to continue.

By varying the branch lengths and angles, more natural looking trees are made.

| Thumbnail                                                         | Name                               | Description                                                   |
| ----------------------------------------------------------------- | ---------------------------------- | ------------------------------------------------------------- |
| [![Antenna Tree](../art/32x32/anttree.gif)](recur/anttree.md)     | [Antenna Tree](recur/anttree.md)   | Antenna Tree curve.                                           |
| [![Binary Switch](../art/32x32/switch.gif)](recur/switch.md)      | [Binary Switch](recur/switch.md)   | A divide by two selector switch.                              |
| [![TreeAnim](../art/32x32/tree.gif)](tree/treeanim.md)            | [TreeAnim](tree/treeanim.md)       | Recursive tree animation, branch angle from 0 to 180 degrees. |
| [![Kite Tree](../art/32x32/kitetree32.gif)](tree/kitetree.md)     | [Kite Tree](tree/kitetree.md)      | Tree using kite shape branches.                               |
| [![Tree Basic](../art/32x32/tree.gif)](tree/tree1.md)             | [Tree Basic](tree/tree1.md)        | Simple recursive trees.                                       |
| [![Tree Leaning](../art/32x32/lean.gif)](tree/leaning.md)         | [Tree Leaning](tree/leaning.md)    | Lop-sided leaning tree.                                       |
| [![Trees Random](../art/32x32/treerl.gif)](tree/tree2.md)         | [Trees Random](tree/tree2.md)      | More natural tree growth.                                     |
| [![Tree Generator](../art/32x32/tree.gif)](tree/tree3.md)         | [Tree Generator](tree/tree3.md)    | Succession of randomly grown trees.                           |
| [![Triangular Tree](../art/32x32/tritree32.gif)](tree/tritree.md) | [Triangular Tree](tree/tritree.md) | Right angled triangle tree.                                   |

## Turtle Walks

These programs allow the turtle to roam or wander across the drawing screen.

Sometimes freely with a random walk. Other times, following a simple mathematical procedure, which can create surprisingly complex images.

| Thumbnail                                                          | Name                                   | Description                                        |
| ------------------------------------------------------------------ | -------------------------------------- | -------------------------------------------------- |
| [![Arc Step](../art/32x32/arcstep32.gif)](walk/arcstep.md)         | [Arc Step](walk/arcstep.md)            | Random path created from series of 90 degree arcs. |
| [![Friendship](../art/32x32/friendship32.gif)](walk/friendship.md) | [Friendship](walk/friendship.md)       | Path created from friendship number series.        |
| [![Polygon Weaver](../art/32x32/polyw.gif)](walk/weaver.md)        | [Polygon Weaver](walk/weaver.md)       | Two random polygons are woven together.            |
| [![Ring Wave](../art/32x32/ringwave32.gif)](walk/ringwave.md)      | [Ring Wave](walk/ringwave.md)          | Standing sine wave on a circular ring.             |
| [![Tangle Curves](../art/32x32/tangle.gif)](walk/wandtangle.md)    | [Tangle Curves](walk/wandtangle.md)    | Random neurotic knotted curves, optimised.         |
| [![Tango](../art/32x32/tango32.gif)](walk/tango.md)                | [Tango](walk/tango.md)                 | Random self circling red and green curves.         |
| [![Wanderer](../art/32x32/wander.gif)](walk/wandhappy.md)          | [Wanderer](walk/wandhappy.md)          | Complex spikey trails from simple initial values.  |
| [![Wanderer Opt](../art/32x32/wander.gif)](walk/wandhappyopt.md)   | [Wanderer Opt](walk/wandhappyopt.md)   | Optimised to scale patterns to screen.             |
| [![Wandering Trails](../art/32x32/trails.gif)](walk/wandtrails.md) | [Wandering Trails](walk/wandtrails.md) | Randomly roaming turtle.                           |

