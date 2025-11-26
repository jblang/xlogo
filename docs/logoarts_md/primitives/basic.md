# Basic Movement

| Primitive       | Alt    | Action                                                          |
| --------------- | ------ | --------------------------------------------------------------- |
| **Forward** nn  | **FD** | Move turtle **forward** nn steps **.**                          |
| **Back** nn     | **BK** | Move turtle **back** nn steps.                                  |
| **Right** nn    | **RT** | Right Turn. Turn turtle **right** (clockwise) nn degrees.       |
| **Left** nn     | **LT** | Left Turn. Turn turtle **left** (counter-clockwise) nn degrees. |
| **PenUp**       | **PU** | Raise turtle **pen** , (no drawing as turtle moves).            |
| **PenDown**     | **PD** | Lower turtle **pen** , (draw a line as turtle moves).           |
| **HideTurtle**  | **HT** | **Hide** the turtle (make invisible).                           |
| **ShowTurtle**  | **ST** | **Show** the turtle (make visible).                             |
| **ClearScreen** | **CS** | **Clear** the **screen** , and reset turtle to home position.   |

Note

1. The command **PenDown PenUp** doesn't draw a dot. A mechanical turtle would do so but in Logo programs this would create many unwanted dots. To create a dot (in the pen shape and width), use **PenDown Forward 0 PenUp** . Or **Dot Position** for a single pixel 'dot'.
2. Be careful using the **ClearScreen** command in programs. It clears the screen and pen colors to the users personal preferences. This may make your program work differently on another machine. Best to use **SetSC** and **SetPC** instead.
