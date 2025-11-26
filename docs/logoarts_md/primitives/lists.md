# Lists

| Primitive           | Alt    | Action                                                      |
| ------------------- | ------ | ----------------------------------------------------------- |
| **First** [list]    | **BF** | Return **first** item of list.                              |
| **Last** [list]     | **BL** | Return **last** item of list.                               |
| **ButFirst** [list] |        | Return all items except ( **but** ) **first** item of list. |
| **ButLast** [list]  |        | Return all items except ( **but** ) **last** item of list.  |

| Primitive                          | Action                                                                                |
| ---------------------------------- | ------------------------------------------------------------------------------------- |
| **Count** [list]                   | Return **count** of number of items in list.                                          |
| **Pick** [list]                    | Return one item **pick** ed at random from list.                                      |
| **Reverse** [list]                 | Return list with items in **reverse** order.                                          |
| **Item** **:** n [list]            | Return **item** in position n of list.                                                |
| **FPut** **:** n [list]            | Return list with item n **put** (added) into **f** irst position.                     |
| **LPut** **:** n [list]            | Return list with item n **put** (added) into **l** ast position.                      |
| **AddItem** [list] **:** n **:** m | Return list with **item** m **add** ed in position n.                                 |
| **SetItem** [list] **:** n **:** m | Return list with item in position n **set** to m.                                     |
| **Remove** **:** n [list]          | Return list with every item n **remove** d.                                           |
| **Member** **:** n [list]          | Return sublist from first instance of **member** n to end of list, else return false. |

Property Lists

| Primitive                      | Alt       | Action                                              |
| ------------------------------ | --------- | --------------------------------------------------- |
| **PutProperty** name key value | **PProp** | Add property (key-value pair) to the property list. |
| **GetProperty** name key       | **GProp** | Return the value associated with the key.           |
| **RemoveProperty** name key    | **RProp** | Remove the key-value pair from the property list.   |
| **PropertyList** name          | **PList** | Display all key-value pairs in the property list.   |

Workspace

| Primitive         | Alt       | Action                                                         |
| ----------------- | --------- | -------------------------------------------------------------- |
| **Primitives**    | **Procs** | Return a list of all primitives in current language.           |
| **Procedures**    | **PLs**   | Return a list of all procedure names.                          |
| **PropertyLists** |           | Return a list of all property list names currently defined.    |
| **Turtles**       | **Vars**  | Return a list of all turtle numbers on screen. (See [Multi     |
| T](multip.md) )   |
| **Variables**     |           | Return a list of currently defined variable names.             |
| **Contents**      |           | Return a list of all procedures, variables and property lists. |

Note

1. **Replace** is an alternative to **SetItem** .
2. Property lists names are case sensitive. A property list can have the same name as another list or variable.
