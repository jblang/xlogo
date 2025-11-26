# Tracker

This procedure can be added to any program. It will keep track of a program 
variable. The value is shown at the bottom of the graphics screen whenever 
the procedure is called.

```logo
To Track :X
 # write value of :X as footer
 Make "myPos Pos Make "myHeading Heading Make "myCol PenColor
 PenUp SetPos [-190 -186]
 SetPW 11 SetH 90 PenErase Forward 380
 PenUp SetPos [-190 -190] SetPC White
 SetPW 1 SetH 0 Label :X
 SetPos :myPos SetH :myHeading SetPC :myCol
End
```
