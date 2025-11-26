# Chequer Grid
![Chequer Grid](../../art/400x400/chequer100.png)

This uses a nested loop to draw an chequered grid of any horiz and vertical value.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To GridRect :OrderH :OrderV :SqSize
 # draw tile at each column x row grid position
 LocalMake "OffsetH (1+ :OrderH) /2
 LocalMake "OffsetV (1+ :OrderV) /2
 LocalMake "SideH :SqSize /:OrderH
 LocalMake "SideV :SqSize /:OrderV
 For (List "Col 1 :OrderH) [
 For (List "Row 1 :OrderV) [
 SetXY :SideH*(:Col-:OffsetH) :SideV*(:Row-:OffsetV)
 Tile :Col :Row :SideH :SideV] ]
End
To Tile :Column :Row :SideH :SideV
 # draw black or white filled rectangle and label (col row)
 If Even? (:Column+:Row) [SetPC Black] [SetPC White]
 Rectangle :SideH-1 :SideV-1 Fill
 SetPC Gray Label Word :Column :Row Wait 3
End
To Even? :Num
 # return 'true' if Num even, else return 'false'
 If (Mod :Num 2)=0 [Output "True] [Output "False]
End
To Rectangle :Wide :High
 # rectangle (wide x high) drawn from centre (tp)
 Back :High/2 Right 90 Back :Wide/2 PenDown
 Repeat 2 [
 Forward :Wide Left 90 Forward :High Left 90]
 PenUp Forward :Wide/2 Left 90 Forward :High/2 # r2c
End
To Go
 New SetFontJustify [1 1]
 Read [Enter horiz & vert order. Default (no entry) is 5 9] "Order
 If :Order =" [Make "Order [5 9]] # default value if no entry
 Make "BoardSize 320
 SetPC Gray Rectangle :BoardSize+12 :BoardSize+12 Fill
 GridRect First :Order Last :Order :BoardSize
End
```
