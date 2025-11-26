# User Input

| Primitive         | Action                                                                   |
| ----------------- | ------------------------------------------------------------------------ |
| **Key?**          | Return true if a **key** has been pressed, then resets to false.         |
| **ReadCharacter** | Return unicode value of last keyboard key pressed, else wait.            |
| **Mouse?**        | Return true if mouse moved or button pressed.                            |
| **ReadMouse**     | Return value of last mouse button pressed (0 1 2), else wait.            |
| **MousePos**      | Return position of mouse, [xx yy].                                       |
| **Character** n   | Return **character** whose unicode value is n.                           |
| **UniCode** "char | Return **unicode** value of character "char.                             |
| **Message** [abc] | Display **message** abc in a window. OK button to close.                 |
| **Read** [abc] "x | Display message abc. **Read** user input and assign to variable **:** x. |
| **Trace**         | Start **Trace** (debugging) mode.                                        |
| **StopTrace**     | Stop **Trace** mode.                                                     |

Note

1. **ReadMouse** returns 0 if the mouse has moved. Returns 1 on release of left mouse button. Returns 3 for release of right mouse button.
2. Shortform for **Message** is **Msg** .
