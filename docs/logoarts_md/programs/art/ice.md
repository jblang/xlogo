# Ice Cracks
![Ice Cracks](../../art/400x400/ice.png)

Imagine a sheet of ice. Cracks appear 
at random as the ice is stressed by the water below. Cracks run 
in a straight line until they 'meet' a previous crack or the edge 
of the ice sheet.  

The result is a sheet of random lines. The procedure 'Colorize' 
fills in a few areas with color.

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
 If 0=(Item 3 FindColor Pos) [Make "OnLine :OnLine+1 Stop]
 SetH Random 360
 While [255=(Item 3 FindColor Pos)] [Back 1]
 Make "BackPos Pos SetPos :Point
 While [255=(Item 3 FindColor Pos)] [Forward 1]
 PenDown SetPos :BackPos PenUp
End
To RandomPoint
 Output List (Random 360)-180 (Random 360)-180
End
To Colorize
 Repeat 48 [
 SetPos RandomPoint
 If 255=(Item 3 FindColor Pos) [
 SetPC ( List 0 Random (Integer 127 + Random 100) 255 )
 Fill ] ]
End
To Go
 New SetPC [102 153 255] DrawTile
 Make "OnLine 0 SetPC Black SetPW 2 # SetPW 1 for thin ice
 While [:OnLine < 36] [Crack]
 Colorize
End
```
