# Spiral

A series of concentric circles appear as a spiral with the added black and white markers.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Go
 New SetSC LightGray
 For [X 5 175 15] [
 Make "NewRadius 10+90*(1+Cos :X)
 SetPC White SetPW 1 Circle :NewRadius FillZone
 SetPC Black Right 6*PenWidth SetPW (:NewRadius/7)-2
 Repeat 18 [
 PenDown Forward :NewRadius-(PenWidth/2)
 PenUp Back :NewRadius-(PenWidth/2) Left 20]
 SetPC Gray SetPW 1 Circle :NewRadius ]
End
```
