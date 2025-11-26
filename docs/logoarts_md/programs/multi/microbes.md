# Microbes
![Microbes](../../art/400x400/cells.png)

32 free roaming microbes are created. 
Only their outlines show, like cell walls under a microscope.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Roam
 PenUp Forward 3 Left (6-Random 12) EdgeTest
End
To OC
 SetPC White PenDown SetPenWidth 42
 Forward 36 Back 72 Forward 36
End
To IC
 SetPC Black PenDown SetPenWidth 36
 Forward 36 Back 72 Forward 36
End
To EdgeTest
 If (First Pos) > 260 [SetX -260 Stop]
 If (First Pos) < -260 [SetX 260 Stop]
 If (Last Pos) > 260 [SetY -260 Stop]
 If (Last Pos) < -260 [SetY 260 Stop]
End
To Go
 New Animation SetTurtlesMax 32
 SetPC White
 For [N 0 31] [SetTurtle :N PenUp
 SetH 11.25*:N]
 Forever [
 For [N 0 31] [SetTurtle :N Roam OC]
 For [N 0 31] [SetTurtle :N IC]
 Refresh Wait 5 Wash]
End
```

EdgeTest keeps the turtles within a 400x400 window. If wrap is used, 
the turtles disappear and reappear abruptly at the screen edges.

Change the forward value in Roam to speed up or slow down the animation.
