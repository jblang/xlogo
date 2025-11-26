# Random Dots

This fills the 400x400 drawing area with 
4000 random dots. You can see how good the Random primitive is by the evenly spread dots.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Jump :Side
 # set turtle to random position within square of size side
 SetXY (Random :Side) - :Side/2 (Random :Side) - :Side/2
End
To Go
 New
 Repeat 4000 [
 SetPC Pick [1 2 3 6]
 Jump 390 Dot Pos]
End
```
