# Loops

| Primitive                       | Action                                                                |
| ------------------------------- | --------------------------------------------------------------------- |
| **Forever** [list]              | **Repeat** instructions in list forever. STOP button to cancel.       |
| **Repeat** n [list]             | **Repeat** instructions in list n times.                              |
| **DoUntil** [list] [cond]       | **Do** instructions in list **until** condition is true.              |
| **DoWhile** [list] [cond]       | **Do** instructions in list **while** condition is true.              |
| **While** [cond] [list]         | **While** condition is true, do instructions in list.                 |
| **For** [x a b s] [list]        | **For** x from a to b, with optional step s, do instructions in list. |
| **ForEach** var [list1] [list2] | **For each** element of list 1, do instructions in list 2.            |
| **RepCount**                    | Return the number of times a repeat loop has executed.                |
| **Stop**                        | **Stop** and break out of procedure or loop.                          |
| **StopAll**                     | **Stop** and break out of **all** procedures or loops, end program.   |
| **Output** xx                   | Break out of procedure or loop with **output** value xx.              |

Note

1. **RepCount** must be within a repeat loop. It returns the number of times the loop has executed, starting with 1.   
 So this prints 7 times table. **Repeat 12 [Print 7 *RepCount]**
2. In For loops, the step value is optional. If no value is given, a default value of 1 is used.
3. If you need to evaluate start, end or step values when creating a For loop, use: **List (For "A :Start :End :Step)**
4. Repeat loops must be repeated a positive integer number of times. This includes 0, in which case the loop is skipped.
5. Use **ForEach** like this: **ForEach "i [a b c d e] [Print :i]**
6. Alternative form for **DoUntil** is **RepeatUntil**
7. Alternative form for **DoWhile** is **RepeatWhile**
