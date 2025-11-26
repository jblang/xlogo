# Spiral Rose
![Spiral Rose](../../art/400x400/rose.png)

A very early drawing of a simple spiral rose ... from repeated four sets of spirals.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Go
 New SetPC Red PenDown
 Repeat 4 [
 For [Angle 12 1 -0.1] [Forward 12 Left :Angle Wait 1]
 Forward 19 PenUp SetXY 0 0 PenDown
 For [Angle 12 1 -0.1] [Forward 12 Right :Angle Wait 1]
 Forward 19 PenUp SetXY 0 0 PenDown
 Left 90]
End
```
