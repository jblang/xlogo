# Golden Squares
![Golden Squares](../../art/400x400/square_gold.png)

This program produces a fractal of squares. Their sizes are determined 
by a list of Fibbonacci numbers. This ensures the fractal can be 
drawn right down to a single pixel or square of size 1.  

The drawing is not quite correct. It should be symmetrical. Some 
smaller squares should not be drawn, but allow larger sized squares 
to be drawn later.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Fibb :Order
 Make "Side Item :Order :FibbList
 Square :Side
 If :Order = 1 [Stop]
 Make "Side2 Item (:Order-1) :FibbList
 LocalMake "Dist (:Side+:Side2)/2
 Repeat 4 [
 Back :Dist Right 90 Back :Dist
 If (Last FindColor Pos) = 0 [Fibb :Order-1]
 Forward :Dist Left 90 Forward :Dist
 Right 90]
End
To Square :Side
 # square drawn from centre (TP) then filled
 Make "Side :Side-1 # subtract 1 for correct size
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [Forward :Side Left 90] PenUp
 Forward :Side/2 Left 90 Forward :Side/2
 If :Side > 1 [Fill]
End
To Go
 New SetPC Cyan
 Make "FibbList [1 1 2 3 5 8 13 21 34 55 89]
 # remove the first term (or first few terms) of FibbList
 Fibb Count :FibbList
End
```

Also see [Squares Corner](../../art/32g5/squaresv.gif)
