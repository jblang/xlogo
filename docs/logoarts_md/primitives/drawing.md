# Drawing

| Primitive                  | Alt        | Action                                                                                                            |
| -------------------------- | ---------- | ----------------------------------------------------------------------------------------------------------------- |
| **Set** **X** x            | **SetPos** | Move turtle to horizontal position x.                                                                             |
| **Set** **Y** y            |            | Move turtle to vertical position y.                                                                               |
| **SetXY** x y              |            | Move turtle to position (x y).                                                                                    |
| **Set** **Position** [x y] |            | Move turtle to position (x y).                                                                                    |
| **Home**                   |            | Move turtle to **home** position (0 0), heading 0.                                                                |
| **Set** **Heading** h      | **SetH**   | **Set** turtle **heading** to h degrees, (cwise from North).                                                      |
| **Set** **PenWidth** w     | **SetPW**  | **Set width** of **pen** nib to w pixels.                                                                         |
| **Set** **PenShape** n     | **SetPS**  | **Set shape** of **pen** nib to 0 (square) or 1 (round).                                                          |
| **Window**                 |            | Turtle can move beyond the screen **window** edges.                                                               |
| **Wrap**                   |            | Turtle **wraps** from screen edge to the opposite side.                                                           |
| **Fence**                  |            | Error message if turtle will move beyond the screen edge.                                                         |
| **Perspective**            |            | Turtle moves in 3D space. (see [Perspective](pers.md) )                                                           |
| **Circle** r               |            | Draw **circle** of radius r steps. *                                                                              |
| **Arc** r h1 h2            |            | Draw **arc** of radius r, from heading h1 cwise to h2. *                                                          |
| **Dot** [x y]              |            | Draw **dot** at position (x y) in pen color, shape and size. *                                                    |
| **Fill**                   |            | Fill all neighbouring pixels of same color with pen color.                                                        |
| **FillZone**               |            | As Fill, but until pen colored pixels are reached.                                                                |
| **FillPolygon** [list]     |            | Fill shape defined by instructions in list.                                                                       |
| **LoadImage** [list]       | **LI**     | **Load** image file specified in list and place upper left corner at turtles position. (file format .png or .jpg) |
| **SaveImage** "name        |            | **Save** drawing area as name.png file.                                                                           |
| **Distance** [x y]         |            | Return **distance** of turtle from point (x y), steps. *                                                          |
| **Towards** [x y]          |            | Return turtle heading if turned **towards** (x y), degrees. *                                                     |

Underlined **Set** can be removed from a primitive name in order to return information.   
 eg. **SetHeading** sets the heading of the turtle. **Heading** returns the heading of the turtle.

* turtle position and heading remain unchanged.

Note

1. You use Towards like this:   
 **SetHeading Towards** [0 0] -this will turn the turtle so that it is facing the origin.
2. The following primitives can be used to draw without lowering the pen. They are transparent, as the turtles position and heading remain unchanged.   
 **Arc** , **Circle** , **Dot** , **Fill** , **FillPolygon, FillZone** , **FindColor** , **Label** .
3. **Arcs** are always drawn clockwise from Angle1 to Angle2. The radius must be a positive value. If negative, no arc is drawn. The whole primitive is ignored, without producing any error message.
4. **SetPW** to a negative value is the same as a positive value. There is no upper limit.
5. **SetPS 0** The pen shape is always 'square' regardless of turtle orientation.
6. Cannot use **FillPolygon** to fill shapes drawn with **Circle** or **Arc** .
7. **Arc :Rad 0 360** produces nothing. **Arc :Rad 0 370** produces a 10 degree arc.
