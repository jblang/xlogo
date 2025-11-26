# Sine Wave

The line segments are all the same length. They appear longer at the top and bottom of the sine wave.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Go
 New
 For [X 180 540 6] [
 SetXY (:X - 360) (150* Sine :X) - 16
 PenDown Forward 32 PenUp]
End
```
