# Symbols

| Symbol | Description |
| --- | --- |
| **+** | Plus / **Sum** |
| **-** | Minus / **Difference** |
| **\*** | Multiply / **Product** |
| **/** | Divide / **Quotient** |
| **&** | **And** |
| **\|** | **Or** (see [Booleans](boos.md) ) |
| **=** | **Equal?** Equal to |
| **<** | **Before?** Less than |
| **>** | More than |
| **<=** | Less than or equal to |
| **>=** | More than or equal to |
| **( )** | Parenthesis |
| **[ ]** | List delimiters |
| **#** | Comment (Mac Alt 3) |
| **:** | Colon |
| **"** | Quotes |
| **\\** | BackSlash - next character labels or prints without evaluation. |

Note

1. Use the **#** symbol to add comments to your programs. XLogo ignores all text after the '#' symbol on the same line. Comments can appear after XLogo code, or on their own line either within or outside a procedure.
2. **# Main Command:** is a special comment. It provides the code used when the green Main Command or Play button is pressed. It must appear on the first line of a .lgo file.
3. For 'less than or equal to' use <= or the following code:   
 Print Or (:A<:B) (:A=:B)
4. For 'not equal to' use the following code: Print Not (:A=:B)
5. The following primitives can be placed in parenthesis. They will then operate on any number of arguments:   
 **And Or Label List Print Product Sentence Sum Word**   
 Eg Make "Total (Sum 1 2 3 4)   
 You cannot write procedures with this feature (as far as I know).
6. Use backslash '\' character to add a space or a new line, or to include #[]()\ characters in a word.   
 Eg Print "Logo\ Logo   
 Eg Print "Logo\nLogo
