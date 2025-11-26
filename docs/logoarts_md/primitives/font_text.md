# Font & Text

Use font to label in the draw zone.

| Primitive                     | Alt       | Action                                                           |
| ----------------------------- | --------- | ---------------------------------------------------------------- |
| **Label "** abcd              | **SetFN** | Turtle **labels** 'abcd' at current position and heading **. *** |
| **Set** **FontName** n        | **SetFS** | Set **font name** to number n.                                   |
| **Set** **FontSize** s        |           | Set **font size** to s pixels.                                   |
| **Set** **FontJustify** [h v] |           | Set font justification. (h and v can be 0, 1 or 2).              |
| **LabelLength "** abcd        |           | Return length of label "abcd, (pixels).                          |

Use text to print in the text zone.

| Primitive                      | Alt        | Action                                             |
| ------------------------------ | ---------- | -------------------------------------------------- |
| **Print "** abcd               | **Pr**     | **Print** 'abcd' in text window on new line.       |
| **Write "** abcd               |            | **Write** 'abcd' in text window on same line.      |
| **Set** **TextSize** s         | **SetTS**  | Set **text size** to s pixels.                     |
| **Set** **TextName** n         | **SetTN**  | Set **text name** to number n.                     |
| **Set** **TextColor** c        | **SetTC**  | Set **text color** to c. (See [Colors](color.md) ) |
| **Set** **Style [** list **]** | **SetSty** | Set text to **style** (s) defined in list.         |
| **ClearText**                  | **CT**     | **Clear** the **text** area.                       |

Underlined **Set** can be removed from a primitive name in order to return information.   
 eg. **SetHeading** sets the heading of the turtle. **Heading** returns the heading of the turtle.

* turtle position and heading remain unchanged.

SetFontJustify [h v]   
 [2 0] [1 0] [0 0]   
 [2 1] [1 1] [0 1]   
 [2 2] [1 2] [0 2]   
 So for instance use [1 1] for text horizontally and vertically centred on the turtles position.

See [perspective](pers.md) for label text in 3d.

Note

1. Setting the text size, name or color will only affect text that is printed or written in the text window. Error messages and command line history are unaffected.
2. Styles available are: none, bold, italic, strike, underline, superscript, and subscript.
3. **Print** and **Label** can be contained in parenthesis. Eg **(Print "a "b "c)** prints a b c.
4. **Label** , **Print** and **Write** remove outer list brackets and initial quotes.   
 Eg **Print [hello]** prints hello.
5. Spaces are ignored. Use \ to print a space. eg **Label "Hello\ World** prints Hello World.   
 Use **\n** to print on a new line. eg **Print "Hello\nWorld**
6. **Print First .123** prints 0. The decimal point can never be printed on it's own.
7. **Print "** prints an empty line.
