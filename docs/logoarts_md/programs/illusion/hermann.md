# Hermann Grid

First noted by Hermann in 1870. The yellow dots at each junction appear to flash or scintillate when you move your eyes over the grid.  

Two sets of grid tiles are drawn, background dark squares, and yellow dots.   

Try other Order values from 1 to 14...

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
 SetPC [33 0 58] SetPW 1 Square 0.8*:Side Fill Wait 1
 If And :Col>1 :Row>1 [
 Back :Side/2 Right 90 Back :Side/2 Left 90
 SetPC Yellow SetPW 0.2*:Side Dot Pos Wait 1]
End
To Square :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [
 Forward :Side Left 90]
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # r2c
End
To Go :Order
 New SetSC [121 87 39]
 Make "Side Int (418/(:Order+1))
 GridSq :Order+1 :Side
End
```

Here is a Hermann spiral illusion. Stare at the centre and you will see concentric dark rings where the radiating lines cross.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Curve :Parity
 # draw curved line
 PenDown Repeat 11 [
 SetPW RepCount/1.8
 Forward 20 Right :Parity *(RepCount+5) ]
 Forward 3 PenUp
End
To Go
 New SetSC [0 0 32] SetPC [196 255 255]
 Repeat 36 [
 Home Right 10*RepCount Curve 1
 Home Left 10*RepCount Curve Minus 1]
End
```
