# Drain
![Drain](../../art/400x400/drain.png)

This uses many of the 'grid' procedures 
to create a water drainage pattern. The order determines the number 
of horizontal / vertical channels. You can vary the channel widths, 
color or both.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :Order
 # global values
 Make "Total Power :Order 2
 Make "CellSize 380/:Order
 Make "OffSet (0.5*:CellSize)-190
 Make "Side 180/:Order
 Make "ColStep 255/:Total
 Make "Count 0
End
To GridPos :M :N
 # return X Y screen position
 Make "X (:N*:CellSize) + :OffSet
 Make "Y (:M*:CellSize) + :OffSet
 Output List :X :Y
End
To SetPen
 # set pen width and color
 SetPW :Side # + 1 - (:Side* (:Count/:Total))
 # Comment in the rest of the above line for variable pen widths
 Make "Col 255 - 0.75*(:Count * :ColStep)
 SetPC ( List 1 :Col 255 )
End
To CentRand
 # random position in central third of grid
 Make "ThOrder Round :Order/3
 Output :ThOrder + Random :ThOrder
End
To Go :Ord
 New Make "Order :Ord Init :Order
 SetPos GridPos CentRand CentRand
 SetPen Dot Pos
 While [:Count < (:Total-1)] [
 SetPos GridPos Random :Order Random :Order
 If ((First FindColor Pos) = 0) [
 SetH 90 * Random 4 Forward :CellSize
 If ((First FindColor Pos) = 1) [
 Back :CellSize SetPC [1 0 0] SetPW 4 Dot Pos
 SetPen PenDown Forward :CellSize PenUp
 Make "Count :Count+1]]]
End
```

Type **Go order** for example **Go 5** to run.

Also see [Grid Art](gridart.md)
