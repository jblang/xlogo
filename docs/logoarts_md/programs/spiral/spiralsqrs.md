# Spiral Squares
![Spiral Squares](../../art/400x400/spiral2.png)

A series of squares drawn in a spiral formation. A couple of 'wait' 
times slow the drawing and allow the progress of the turtle to be 
seen.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Square :Size
 If :Size < 1 [Stop] # stop when size too small
 Repeat 4 [Forward :Size Left 90 Wait 10] # draw a square
 Forward :Size Right 30 Wait 40
 Square :Size*0.9 # do again with smaller size
End
To Go
 New SetPC White
 SetPos [-46 -210] SetH -30 PenDown
 Square 92 # begin with a square of size 92
End
```
