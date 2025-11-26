# Fields
![Fields](../../art/400x400/field.png)

Imagine a field of mud or a clay tablet 
drying in the sun. Cracks appear at random as the mud shrinks. This 
program limits cracks to either horizontal or vertical. The crack 
however must choose the shortest distance and must not run parrallel 
too close to a previous crack. Longer cracks are also wider 
than shorter cracks.  

The result is a field of random vertical and horizontal lines. The 
procedure 'Colorize' fills in a few areas with color.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To DrawTile
 SetPos [-190 -190]
 PenDown Repeat 4 [Forward 380 Right 90] PenUp
 Home Fill
End
To Crack
 Make "Point RandomPoint SetPos :Point
 If 0=(Item 2 FindColor Pos) [Make "OnLine :OnLine+1 Stop]
 Make "PosList NESW
 Make "DistV (Item 1 :PosList) + (Item 3 :PosList)
 Make "DistH (Item 2 :PosList) + (Item 4 :PosList)
 If (:DistV < :DistH) [
 If And ((Item 2 :PosList) > 6) ((Item 4 :PosList) > 6) [LineV]] [
 If And ((Item 1 :PosList) > 6) ((Item 3 :PosList) > 6) [LineH]]
End
To NESW
 #Return North East South and West distances
 Make "PosList []
 For [N 0 3] [
 Make "Dist 0
 SetPos :Point SetH :N*90
 While [255=(Item 1 FindColor Pos)] [
 Make "Dist :Dist + 1 Forward 1]
 Make "PosList LPut :Dist :PosList]
 Output :PosList
End
To RandomPoint
 Output List (Random 360)-180 (Random 360)-180
End
To LineH
 SetPos :Point SetH 90 Forward (Item 2 :PosList)-1
 SetPW Integer :DistH/32 
 PenDown Back :DistH-2 PenUp
End
To LineV
 SetPos :Point SetH 0 Forward (Item 1 :PosList)-1
 SetPW Integer :DistV/32 PenDown Back :DistV-2 PenUp
End
To Colorize
 Repeat 64 [
 SetPos RandomPoint
 If 255=(Item 1 FindColor Pos) [
 SetPC (List (Integer 100 + Random 127) 127 0)
 Fill] ]
End
To Go
 New SetPC Orange DrawTile
 Make "OnLine 0 SetPC Black
 While [:OnLine < 64] [Crack]
 Colorize
End
```
