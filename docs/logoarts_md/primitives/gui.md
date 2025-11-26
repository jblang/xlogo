# GUI

Button and pull-down menu graphical components can be added to the drawing area.

| Primitive                               | Action                                                  |
| --------------------------------------- | ------------------------------------------------------- |
| **guiAction** "id [action1 action2 ...] | Define **action** or **actions** for GUI component "id. |
| **guiButton** "id "name                 | Create a **button** with label "name.                   |
| **guiDraw** "id                         | **Draw** GUI component "id (top left if not defined).   |
| **guiMenu** "id [name1 name2 ...]       | Create a **menu** with listed drop down names.          |
| **guiPosition** "id [xx yy]             | Define GUI component **position** [xx yy].              |
| **guiRemove** "id                       | **Remove** and delete GUI component "id.                |

Note

1. GUI id's are case sensitive.
2. Graphical components cannot be drawn at an angle.
