# Tiles
![Tiles](../../art/400x400/tiles/tile_32_9.png)

A geometric tile pattern is formed when individual square tiles are fitted together without gaps or overlaps. Each tile has 4 rotations (90, 180 or 270 degrees).

The resulting pattern is a regular array of randomly orientated tiles. Some random gray grout lines are drawn.

The screen is then filled from the top right corner to highlight any enclosed areas within the grid of tiles. 
Each tile is filled with random dark red, green or purple color.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To GridSq :Order :Side
 # draw tile at each column x row position
 LocalMake "Offset (1+:Order)/2
 LocalMake "Ps [] LocalMake "Total :Order*:Order
 Repeat :Total [Make "Ps LPut RepCount :Ps] 
 Repeat :Total [
 LocalMake "P Pick :Ps
 LocalMake "Ps Remove :P :Ps
 LocalMake "Col 1+Mod :P-1 :Order
 LocalMake "Row 1+Int (:P-1)/:Order
 SetXY :Side*(:Col-:Offset) :Side*(:Row-:Offset)
 Tile :Col :Row :Side]
End
To Tile :Col :Row :Side
 # use global value of :TileNo
 SetH Pick [0 90 180 270]
 Make "Parity Pick [1 -1]
 # Make "Parity 1 # comment in for no reflections
 Run Se Word "Tile :TileNo :Side PenUp
 If Number? :TileNo [Wait 4]
End
To Tile1 :Side
 Repeat 2
 [Back :Side Arch 90 0.7*:Side Forward :Side Left 180]
End
To Tile2 :Side
 Left 45 Repeat 2
 [Back :Side/1.414 Arch 90 :Side/2 Forward :Side/1.414 Left 180]
End
To Tile3 :Side
 Back :Side/2 Left 45 PenDown Forward 1.414*:Side/2
 PenUp Back 1.414*:Side/2 Right 135 Back :Side/2
 Left 26.57 Forward 0.375*:Side PenDown Forward 0.745*:Side
 PenUp Back 1.12*:Side Left 36.87 Forward 0.375*:Side
 PenDown Forward 0.745*:Side
End
To Tile4 :Side
 Repeat 2 [Back :Side/2 Arch 180 :Side/4
 Back :Side/2 Arch 68 0.9*:Side
 Forward :Side Left 180]
End
To Tile5 :Side
 Back :Side/2 Left 45 PenDown Forward 1.414*:Side/2
 PenUp Back 1.414*:Side/2
 Right 135 Forward :Side/2 Left 90 Forward :Side/2
 Left 109 PenDown Forward 0.78*:Side PenUp Back 0.78*:Side
 Right 109 Forward :Side/2 Left 90 Forward :Side/2
 Left 71 PenDown Forward 0.78*:Side
End
To Tile6 :Side
 Back :Side/2 Right :Parity*90 Back :Side/2
 Repeat 2 [
 Left :Parity*26.6 PenDown Forward 1.12*:Side 
 PenUp Back 1.12*:Side
 Right :Parity*26.6 Forward :Side Left :Parity*90
 Left :Parity*26.6 PenDown Forward 0.445*:Side 
 PenUp Forward 0.455*:Side
 PenDown Forward 0.22*:Side PenUp Back 1.12*:Side
 Right :Parity*26.6 Forward :Side Left :Parity*90]
End
To Tile7 :Side
 Back :Side/2 Right :Parity*90 Back :Side/2
 Repeat 2 [
 Left :Parity*26.6 PenDown Forward 1.12*:Side PenUp Back 1.12*:Side
 Right :Parity*26.6 Forward :Side/2 Left :Parity*63.4
 PenDown Forward 0.37*:Side PenUp Back 0.37*:Side
 Right :Parity*63.4 Forward :Side/2 Left :Parity*90 
 Forward :Side Left :Parity*90]
End
To Tile8 :Side
 Back :Side/2
 Right 26.6 PenDown Forward 0.37*:Side PenUp Back 0.37*:Side
 Right 63.4 Back :Side/2
 Left 26.6 PenDown Forward 1.12*:Side PenUp Back 1.12*:Side
 Left 36.8 PenDown Forward 1.12*:Side PenUp Back 1.12*:Side
 Left 26.6 Forward :Side/2 Right 63.4 PenDown Forward 0.37*:Side
End
To Tile9 :Side
 Repeat 4 [
 Back :Side/2 PenDown Forward :Side/4
 Left :Parity*116.565 Forward 0.56*:Side
 PenUp Back 0.56*:Side Left :Parity*63.435
 Back :Side/4 Left 90]
End
To Tile10 :Side
 Back :Side/2 Right :Parity*90 Back :Side/2
 Repeat 2 [
 Left :Parity*26.6 PenDown Forward 0.895*:Side
 Right :Parity*90 Forward 0.445*:Side PenUp
 Left :Parity*153.4 Forward :Side Left :Parity*90]
End
To Tile11 :Side
 Back :Side/2
 Repeat 2 [
 Left :Parity*26.6 PenDown Forward 0.67*:Side
 Left :Parity*90 Forward 0.22*:Side PenUp
 Right :Parity*116.6 Forward :Side/2 Right :Parity*90
 Forward :Side/2 Right :Parity*90]
End
To Tile12 :Side
 Back :Side/2 Right 90 Back :Side/2
 Left 63.45 PenDown Forward 1.12*:Side
 Right 126.9 Forward 1.12*:Side PenUp
 Left 153.4 Forward :Side Left 90
 Left 63.45 PenDown Forward 0.56*:Side PenUp Forward 0.56*:Side
 Right 126.9 Forward 0.56*:Side PenDown Forward 0.56*:Side
End
To Tile13 :Side
 Arch 180 :Side/2
 Left :Parity*45 Back 0.7*:Side
 Arch 90 :Side/2
End
To Tile14 :Side
 Left 45 Repeat 3 [
 Back :Side/1.414 Arch 90 :Side/2 Forward :Side/1.414 Left 90]
End
To Tile15 :Side
 Back :Side/2 Right 90 Back :Side/2
 Left 26.6 PenDown Forward 0.895*:Side
 Right 90 Forward 0.445*:Side PenUp
 Left 153.4 Forward :Side Left 90
 Left 63.4 PenDown Forward 0.445*:Side
 Right 90 Forward 0.895*:Side
End
To Tile16 :Side
 Back :Side/2
 Left 26.6 PenDown Forward 0.67*:Side
 Left 90 Forward 0.22*:Side PenUp
 Right 26.6 Back :Side
 Right 26.6 PenDown Forward 0.67*:Side
 Right 90 Forward 0.22*:Side
End
To Tile17 :Side
 Back :Side/2 Right :Parity*90 Back :Side/2
 Repeat 4 [
 PenUp Forward :Side/2 PenDown Left :Parity*45
 Forward 0.71*:Side/2 Right :Parity*90
 Forward 0.71*:Side/2 Left :Parity*135]
 Left :Parity*45 Forward 1.1*:Side
End
To Tile18 :Side
 Back :Side/2 Right 90 Back :Side/2
 Repeat 2 [
 Forward :Side/3 Left 71.6 PenDown Forward 0.53*:Side
 PenUp Back 0.53*:Side Right 71.6 Forward :Side/3
 Left 71.6 PenDown Forward 0.26*:Side PenUp Back 0.26*:Side
 Right 71.6 Forward :Side/3 Left 90
 Forward :Side/3 Left 108.4 PenDown Forward 0.26*:Side
 PenUp Back 0.26*:Side Right 108.4 Forward :Side/3 Left 108.4
 PenDown Forward 0.53*:Side PenUp Back 0.53*:Side
 Right 108.4 Forward :Side/3 Left 90 ]
End
To Tile19 :Side
 Forward 0.42*:Side/2 PenDown Back 0.84*:Side/2 PenUp Back 0.58*:Side/2
 Right 90 Back :Side/2
 PenDown Left 30 Forward 1.155*:Side/2 Right 60 Forward 1.155*:Side/2
 PenUp Left 120 Forward :Side Left 120
 PenDown Forward 1.155*:Side/2 Right 60 Forward 1.155*:Side/2
End
To Tile20 :Side
 Back :Side/2 Arch 180 :Side/2 Forward :Side/2
 Left :Parity*45 Back 0.71*:Side Arch 90 :Side Forward 0.71*:Side
 Right :Parity*90 Back 0.71*:Side Left :Parity*30 Arch 30 :Side
End
To Tile21 :Side
 Back :Side/2 Arch 180 :Side/2 Forward :Side/2
 Left 90 Back :Side/2 Right 45
 Arch 90 :Side/2 Left 45 Forward :Side
 Right 135 Arch 90 :Side/2
End
To Tile22 :Side
 Back :Side/2 Left :Parity*45 PenDown
 Forward 1.42*:Side/2 Right :Parity*135
 Forward :Side Left :Parity*135
 Forward 1.42*:Side/2
End
To Arch :Angle :Radius
 # symmetrical arc drawn relative to turtle heading
 Arc :Radius Heading-:Angle/2 
 Heading+:Angle/2
End
To TileFill :Side
 # fill with random color
 SetH 0 Forward 0.2*:Side/2
 If 64=Item 2 (FindColor Pos) [ # only fill dark green areas
 SetPC Pick [10 12 [64 0 127]] Fill Wait 4]
End
To TileGrout :Side
 # add random grout lines
 If 0=Random 2 [
 Back :Side/2 Right 90 Back :Side/2
 PenDown Repeat 4 [Forward :Side Left 90] ]
End
To GoX
 Forever [Go (1+Random 22) (4+Random 5) Wait 240]
End
To Go :Tile :Order
 New SetSC [0 64 0]
 Make "Side Int 380/:Order # screen width 380
 GlobalMake "TileNo :Tile SetPC Yellow
 If :Order<6 [SetPW Abs (7-:Order)] # fat pen for low orders
 GridSq :Order :Side Wait 30
 SetPos [195 195] SetPC Black Fill Wait 30 # fill screen edge
 GlobalMake "TileNo "Fill # fill tile
 GridSq :Order :Side Wait 30 # comment out for no fills
 GlobalMake "TileNo "Grout SetPW 1 SetPC Gray # grout tile
 GridSq :Order :Side # comment out for no grout
End
```

For a specific pattern, type **go tilepattern arraysize** eg **go 2 7** to run.

Tiles 11-21 are filled from the centre  

Tiles 31-36 have a line in their centre, so are filled off centre  

Tiles 6, 7, 9, 10, 11, 13, 15, 18 and 20 have mirror image forms and use the global value of Parity of 1 or -1.
