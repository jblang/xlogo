# Zig Zag

The red horizontal lines appear distorted with the addition of the white zig zags.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To ZigZag
 # Draw a zig zag line
 PenDown Repeat 4 [
 Right 55 Forward 80 Left 110 Forward 80 Right 55]
End
To Go
 New SetPC White
 For [X -170 130 40] [
 SetXY :X Minus 183
 ZigZag PenUp]
 SetPC Red SetH 90
 For [Y -160 170 46] [
 SetXY Minus 180 :Y
 PenDown Forward 360 PenUp]
End
```
