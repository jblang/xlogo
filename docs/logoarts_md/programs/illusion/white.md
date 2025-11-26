# White's Illusion

Both rectangles are the same color but appear diferent when the dark horizontal bars are added.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Rectangle :Wide :High
 # rectangle (wide x high) drawn from centre (tp)
 Back :High/2 Right 90 Back :Wide/2 PenDown
 Repeat 2 [
 Forward :Wide Left 90 Forward :High Left 90]
 PenUp Forward :Wide/2 Left 90 Forward :High/2 # r2c
End
To Go
 New Make "Col1 [0 0 64] Make "Col2 [255 255 96]
 SetSC :Col1 SetPC :Col2 SetPos [0 -125]
 Repeat 4 [Rectangle 399 49 Fill Forward 100]
 Wait 60 SetPC Gray
 SetPos [-90 0] Rectangle 61 361 FillZone
 SetPos [90 0] Rectangle 61 361 FillZone
 Wait 120 SetPC :Col1 SetPos [-90 -175]
 Repeat 4 [Rectangle 80 49 FillZone Forward 100]
 SetPC :Col2 SetPos [90 -125]
 Repeat 4 [Rectangle 80 49 FillZone Forward 100]
End
```
