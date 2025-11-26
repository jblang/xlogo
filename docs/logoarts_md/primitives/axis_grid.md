# Axis & Grid

These primitives effect the axis and grid, drawn in the Draw zone.

| Primitive               | Alt     | Action                                                            |
| ----------------------- | ------- | ----------------------------------------------------------------- |
| **Axis** nn             | **SAC** | Draw X and Y **axis** with division size of nn pixels.            |
| **XAxis** nn            |         | Draw **X** (horizontal) **axis** with division size of nn pixels. |
| **YAxis** nn            |         | Draw **Y** (vertical) **axis** with division size of nn pixels.   |
| **Set** **AxisColor** n |         | Set **axis color** to n. (See [colors](color.md) )                |
| **XAxis?**              |         | Return true if **X axis** is drawn, else false.                   |
| **YAxis?**              |         | Return true if **Y axis** is drawn, else false.                   |
| **StopAxis**            |         | Delete axis.                                                      |
| **Grid** mm nn          | **SGC** | Draw a **grid** with division sizes of mm x nn pixels.            |
| **Set** **GridColor** n |         | Set **grid color** to n. (See [colors](color.md) )                |
| **Grid?**               |         | Return true if **grid** is drawn, else false.                     |
| **StopGrid**            |         | Delete grid.                                                      |

Underlined **Set** can be removed from a primitive name in order to return information.   
 eg. **SetHeading** sets the heading of the turtle. **Heading** returns the heading of the turtle.

Note

A 'ClearScreen' is performed after drawing any axis or grid. This results in ShowTurtle and screen and pen color as as set up in Preferences.
