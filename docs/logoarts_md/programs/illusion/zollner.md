# Zollner Illusion

The horizontal lines are parallel but appear tilted. This apparently simple illusion was 
invented by Johann Zollner in 1860.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To GridSq :Order :Side
 # draw tile at each column x row position
 LocalMake "Offset (1+:Order)/2
 For (List "Col 1 :Order) [
 For (List "Row 1 :Order) [
 SetXY :Side*(:Col-:Offset) :Side*(:Row-:Offset)
 Tile :Col :Row :Side] ]
End
To Tile :Col :Row :Side
 SetH 90+180 *(Mod :Row 2)-90*(Mod :Col 2)
 Back :Side/2 Right 90 Back :Side/2
 PenDown Repeat 4 [Forward :Side Left 90]
 ForEach "P [1 -1] [
 Repeat 2 [
 Left :P*22.5 Forward 0.46*:Side Back 0.46*:Side
 Right :P*22.5 Forward 0.4*:Side
 Left :P*22.5 Forward 0.46*:Side Back 0.46*:Side
 Right :P*22.5 Forward 0.6*:Side Left :P*90]
 Right :P*90 Back :Side Right :P*90 Back :Side Left :P*90] PenUp
End
To Go :Order
 New
 GridSq :Order 384/:Order
End
```
