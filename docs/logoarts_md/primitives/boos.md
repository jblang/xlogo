# Booleans

These primitives (also called predicates) end in a '?' and return either 'True' or 'False'.

| Primitive            | Alt       | Action                                                                        |
| -------------------- | --------- | ----------------------------------------------------------------------------- |
| **Before?** aa bb    | **PD?**   | Return true if aa is **before** bb, else false.                               |
| **Empty?** "name     | **Prim?** | Return true if "name is an **empty** word or list, else false.                |
| **EndCountDown?**    | **Proc?** | Return true if the countdown has reached 0, else false. [T&D](time_date.md) . |
| **Equal?** aa bb     | **Var?**  | Return true if aa and bb are **equal** , else false.                          |
| **Integer?** nn      |           | Return true if nn is an **integer** (whole number), else false.               |
| **Key?**             |           | Return true if a **key** has been pressed, then resets to false.              |
| **List?** :var       |           | Return true if :var is a **list** , else false.                               |
| **Member?** "aa :bb  |           | Return true if "aa is a **member** of :bb, else false.                        |
| **Mouse?**           |           | Return true if mouse moved or pressed, then resets to false.                  |
| **Number?** nn       |           | Return true if nn is a **number** , else false.                               |
| **PenDown?**         |           | Return true if the **pen** is **down** , else false.                          |
| **Primitive?** "name |           | Return true if "name is an XLogo **primitive** , else false.                  |
| **Procedure?** "name |           | Return true if "name is a user defined **procedure** , else false.            |
| **Variable?** "name  |           | Return true if "name is defined as a **variable** , else false.               |
| **Visible?**         |           | Return true if the turtle is **visible** , else false.                        |
| **Word?** :var       |           | Return true if :var is a **word** , else false.                               |

Booleans can be operated on by:

| Primitive     | Action                                                                     |
| ------------- | -------------------------------------------------------------------------- |
| **And** aa bb | Return true if both aa **and** bb is true, else false.                     |
| **Or** aa bb  | Return true if either aa **or** bb is true, else false.                    |
| **Not** aa    | Return **not** aa. If aa is true return false. If aa is false return true. |
| **True**      | Return " **true** .                                                        |
| **False**     | Return " **false** .                                                       |

Note

1. **Before?** can test either numerical or alphabetical values.   
 **Equal?** can test either numerical or alphabetical values, or a list.
2. **And** and **Or** can be used within brackets to test more than two conditions. Eg. If (And 1=1 2=2 3=3) [Print "True]
