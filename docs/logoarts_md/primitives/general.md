# General

| Primitive Alt Action   |
| ---------------------- |
| **GlobalMake** "var xx | **Make** | Assign variable "var value of xx. Always Global.             |
| **Local** "var         |          | Create variable named "var. Unassigned.                      |
| **LocalMake** "var :xx |          | Assign variable "var value of xx, in current procedure only. |
| **Thing** "var         |          | Return value of variable "var.                               |
| **Run [** list **]**   | **Def**  | **Run** (execute) instructions contained in list.            |
| **Define** "name list  |          | **Define** procedure "name with each line as a list item.    |
| **Text** "name         |          | Return defined procedure "name as a text list.               |
| **List** aa bb         | **Se**   | Return a list composed of aa and bb.                         |
| **Sentence** aa bb     |          | Return a sentence composed of aa and bb.                     |
| **Word** aa bb         |          | Return a word composed of aa and bb.                         |
| **Bye**                |          | Quit XLogo.                                                  |

Note

1. **List** , **Sentence** and **Word** can be contained in parenthesis.   
 eg (List "a "b "c) returns [a b c]
2. **Make** and **GlobalMake** are the same, as all variables in XLogo are global unless created with **LocalMake** . Use **GlobalMake** to emphasize the variable will be used in a global manner.
3. Procedures are defined in the Editor window. Each procedure starts with **'To'** and finishes with **'End'** . Any variables after **To** are passed to the procedure as arguments. If enclosed in brackets they are optional.

Conditionals

| Primitive                   | Action                                                                  |
| --------------------------- | ----------------------------------------------------------------------- |
| **If** cond [list]          | **If** condition is true, then do instructions in list.                 |
| **If** cond [list1] [list2] | **If** condition is true, then do instructions in list1, else do list2. |
| **IfElse** cond :a :b       | **If** condition is true, then do :a, else do :b.                       |

Editing

| Primitive              | Alt       | Action                                                  |
| ---------------------- | --------- | ------------------------------------------------------- |
| **Edit** "name or List | **Ed**    | Edit procedure called "name or procedure names in list. |
| **EditAll**            | **EdAll** | Edit all procedures.                                    |
| **To** "name           |           | Edit or create a procedure called "name.                |

A new procedure can be written by typing **'To** ProcedureName **'** in the command line. It will overwrite any previous procedures of the same name when you close the Editor window.

Erasing

| Primitive                   | Alt       | Action                                              |
| --------------------------- | --------- | --------------------------------------------------- |
| **EraseAll**                | **ErAll** | Delete all variables procedures and property lists. |
| **EraseProcedure** "Name    | **ErP**   | Delete procedure called "name.                      |
| **ErasePropertyList** "Name | **ErPL**  | Delete property list called "name.                  |
| **EraseTurtle** xx          | **ErT**   | Delete turtle number xx.                            |
| **EraseVariable** "Name     | **ErV**   | Delete variable called "name.                       |

eg. to delete all variables type in **ErV Variables** .
