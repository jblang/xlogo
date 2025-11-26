# Preferences

| Primitive                    | Alt          | Action                                                                                            |
| ---------------------------- | ------------ | ------------------------------------------------------------------------------------------------- |
| **Set** **ScreenSize** [w h] | **SetDQ**    | Set drawing **screen size** (width x height).                                                     |
| **Set** **Digits** nn        | **SetTM**    | Set number of significant digits.                                                                 |
| **Set** **DrawingQuality** n | **SetShape** | Set **drawing quality** 0 (normal) 1 (high) 2 (low).                                              |
| **Set** **TurtlesMax** nn    | **SetSep**   | Set **max** imum number of **turtles** .                                                          |
| **Set** **Shape** n          |              | Set **shape** of the turtle, (0 to 9).                                                            |
| **Set** **Separation** n     |              | Set **seperation** between draw and text zones, (0-1).                                            |
| **Set** **Zoom** nn          |              | Set **Zoom** magnification scale to nn.                                                           |
| **ResetAll**                 |              | **Reset all** preferences to default values.                                                      |
| **ZoneSize**                 |              | Retun upper-left and lower-right co-ordinates of the viewable drawing window. [ [x1 y1] [x2 y2] ] |

Underlined **Set** can be removed from a primitive name in order to return information.   
 eg. **SetHeading** sets the heading of the turtle. **Heading** returns the heading of the turtle.

Note

ZoneSize cannot be set.

ResetAll is useful when distributing your XLogo programs to others. It resets all preferences to known default values (and clears the screen). This ensures your program will run the same on another machines.

- ScreenSize = 1000 by 1000 pixels
- Screen mode = Window
- ScreenColor = White
- PenColor = Black
- PenWidth = 1
- PenShape = 0 (Square)
- Turtle Shape = 0
- Turtle Position = [0 0], Heading = 0, ShowTurtle, PenDown
- Drawing Quality = 0 (Normal)
- TextName and FontName = Dialog
- TextSize and FontSize = 12 point
- FontJustify = [0 0]
- Maximum Number of Turtles = 16
- Animation, Axis, Grid, Perspective and Trace modes off

You can find the number for FontName, TurtleShape and Instrument in the Options menu.
