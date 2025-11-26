# Intertwine

The four concentric circles appear to intersect and cross over each other. An illusion by Baingio Pinna.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Ring :Rep
 Make "Parity Minus :Parity
 Repeat :Rep [
 Make "PenC Abs :PenC -7 SetPC :PenC
 Forward 3*:Rep
 Left :Parity *18 Square 13 Right :Parity *18
 Back 3*:Rep Left 360/:Rep]
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
 SetSC LightGray SetPC White
 Make "Parity 1 Make "PenC 7
 ForEach "Rep [60 46 32 20] [Ring :Rep]
End
```
