# Multiple Turtles

| Primitive             | Alt         | Action                                           |
| --------------------- | ----------- | ------------------------------------------------ |
| **Set** **Turtle** xx | **STurtle** | **Set** the active **turtle** to number xx.      |
| **Turtles**           |             | Return a list of all **turtles** on screen.      |
| **EraseTurtle** xx    | **ErT**     | **Erase turtle** number xx.                      |
| **Set** **TurtleMax** | **SetTM**   | **Set max** imum number of **turtle** s allowed. |

Underlined **Set** can be removed from a primitive name in order to return information.   
 eg. **SetHeading** sets the heading of the turtle. **Heading** returns the heading of the turtle.

Note

1. XLogo can use more than one turtle. You can switch from turtle to turtle to control each one individually. You cannot control more than one turtle simultaneously. The turtle currently under control is called the active turtle.
2. **SetTurtle XX** will create a new turtle if the number XX doesn't already exist. Otherwise makes turtle XX the active turtle.
3. New turtles always start in the home position with their pen down and hidden.
4. Turtle number 0 is always on screen and cannot be erased.
5. Multiple turtles can be used in Perspective mode too.
