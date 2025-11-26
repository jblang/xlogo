# Bulge

The square grid distorts and appears to bulge outwards.  

Three sets of grid tiles are drawn, background light and dark squares, black or white dots and foreground gray outline squares.   

Try other even Order values from 2 to 12...

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
 # use global value of :Tile to draw selected tile 
 Run (Word "Tile :Tile) :Col :Row :Side
End
To TileBg :Col :Row :Side
 If Even? (:Col+:Row) [SetPC [0 102 153]] [SetPC [153 204 255]]
 Square :Side-1 Fill Wait 2
End
To TileDot :Col :Row :Side
 If X>0 [Make "Col :Col+1]
 If Y>0 [Make "Row :Row+1]
 If Even? (:Col+:Row) [SetPC Black] [SetPC White]
 SetPW :Side/2 Dot Pos Wait 3
End
To TileSq :Col :Row :Side
 SetPC Gray SetPW :Side/30 Square :Side
End
To Square :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [
 Forward :Side Left 90]
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # r2c
End
To Even? :Num
 # return 'true' if Num even, else return 'false'
 If (Mod :Num 2)=0 [Output "True] [Output "False]
End
To Go :Order
 New
 Make "Side Int (400/(:Order+1))
 GlobalMake "Tile "Bg GridSq :Order+1 :Side Wait 20
 GlobalMake "Tile "Dot GridSq :Order :Side Wait 20
 GlobalMake "Tile "Sq GridSq :Order-1 :Side
End
```
