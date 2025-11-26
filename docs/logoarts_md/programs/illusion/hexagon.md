# Hexagon Arrows

The straight line segments create a regular hexagon. The added arrow heads make it appear distorted.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Triangle
 PenDown Right 90 Forward 22
 Left 120 Forward 44
 Left 120 Forward 44
 Left 120 Forward 22
 Left 90
 PenUp Forward 4 FillZone Back 4
End
To Go
 New SetPC [153 204 204]
 SetX 120 Right 30 PenDown SetPW 2
 Repeat 6 [Left 60 Forward 120 Wait 20]
 SetPC [153 204 205] SetPW 1 Wait 60
 Repeat 6 [Triangle Left 60 Forward 120 Wait 20]
End
```
