# Perspective Mode

To switch to 3D mode, use the primitive **Perspective** . To return to 2D mode use **Window** , **Wrap** or **Fence** (see [Drawing](drawing.md) ).

| Primitive                       | Alt      | Action                                           |
| ------------------------------- | -------- | ------------------------------------------------ |
| **Perspective**                 | **3D**   | Switch to 3D mode.                               |
| **SetXYZ** x y z                |          | Move turtle to position x y z.                   |
| **Set** **Z** z                 |          | Move turtle to depth position z.                 |
| **Set** **Orientation** [r p h] |          | Set turtle's orientation (roll, pitch, heading). |
| **LeftRoll** n                  | **LR**   | Roll turtle left by n degrees.                   |
| **RightRoll** n                 | **RR**   | Roll turtle right by n degrees.                  |
| **Set** **Roll** n              |          | Set turtle's roll angle to n degrees.            |
| **DownPitch** n                 | **Down** | Pitch turtle down by n degrees.                  |
| **UpPitch** n                   | **Up**   | Pitch turtle up by n degrees.                    |
| **Set** **Pitch** n             |          | Set turtle's pitch angle to n degrees.           |

Underlined **Set** can be removed from a primitive name in order to return information.   
 eg. **SetHeading** sets the heading of the turtle. **Heading** returns the heading of the turtle.

3D viewer

Four pairs of primitives for polygons, lines, points and text. If not used, nothing will be drawn in the 3D Viewer window.

| Primitive      | Action                                                        |
| -------------- | ------------------------------------------------------------- |
| **View3D**     | Launch interactive 3D viewer, with fog and light controllers. |
| **PolyStart**  | Start recording following turtle moves as polygons.           |
| **PolyEnd**    | Stop polygon recording.                                       |
| **LineStart**  | Start recording following turtle moves as lines.              |
| **LineEnd**    | Stop line recording.                                          |
| **PointStart** | Start recording following turtle moves as points.             |
| **PointEnd**   | Stop point recording.                                         |
| **TextStart**  | Start recording following as text.                            |
| **TextEnd**    | Stop text recording.                                          |

Note

1. **View3d** is only available in 3D mode.
2. The following primitives can be used in both 2D and 3D modes: **Arc Circle ClearScreen Distance Dot Heading Home Label LabelLength Position SetHeading SetPosition SetX SetY Towards**
3. In perspective mode, you can switch to animation mode to animate a perspective drawing.
