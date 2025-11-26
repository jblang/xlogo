# Cafe Wall

This was originally observed as tiles on a cafe wall. The tiles appear distorted.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To DrawGrid
 For [Z 0 2] [
 For [X 0 5] [
 For [Y 0 3] [
 SetY (:Side*:Y)+(4*:Side*:Z)-176
 If :Y=3 [Make Y 1]
 SetX (12*:Y)+(2*:Side*:X)-172
 Square :Side Fill ] ] ]
End
To DrawLine
 For [Y 0 10] [
 SetXY Minus 188 (:Y*:Side) - 160
 PenDown Forward 376 PenUp]
End
To Square :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [
 Forward :Side Left 90]
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # r2c
End
To Go
 New
 SetSC [255 255 204] SetH 90
 Make "Side 32
 SetPC [0 51 0] DrawGrid
 SetPC Red DrawLine
End
```
