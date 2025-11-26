# Chiral Star
![Chiral Star](../../art/400x400/g7_star.png)

Similar to [Edge](../recur/edge.md) code using a square shape. Lower left quadrant is removed. The turtle then turns and visits the other three quadrants recursivly. The untouched areas remain to form this shape.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Chiral :Order :Side
 If :Order < 0 [Stop]
 Back :Side/2 Left 90 Forward :Side/2 Right 90
 # SetPC (List :Side :Side :Side) # comment in for shaded squares
 Square :Side
 Forward :Side Chiral :Order-1 :Side/2
 Right 90 Forward :Side Chiral :Order-1 :Side/2
 Right 90 Forward :Side Chiral :Order-1 :Side/2
 Right 90 Forward :Side/2 Right 90 Forward :Side/2
End
To Go :Order
 New Make "Side 384
 Square :Side
 SetPC Black Chiral :Order :Side/2
End
To Square :Side
 # square drawn from centre
 Make "Side :Side-1
 Make "Half :Side/2
 Back :Half Right 90 Back :Half PenDown
 Repeat 4 [Forward :Side Left 90] PenUp
 Forward :Half Left 90 Forward :Half
 Fill Wait Integer :Side/6
End
```

Type **Go order** for example **Go 6** to run.
