# Oct Tiles
![Oct Tiles](../../art/400x400/octtile.png)

An octile has 8 points around its edge. These are randomly joined as four pairs. This results in 80 different tiles including rotations and reflections.  

For orders below 8, grout lines are added.  

For an order of 1, the 8 points are labelled and the resulting tile list is shown.  

Comment out the line in Tiles to produce 'all-over' patterns.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :Order
 # initialise parameters
 Make "Width 380
 Make "CellSize :Width/:Order
 Make "Half :CellSize/2
 Make "Offset (:Width-:CellSize)/2
 Make "Dist :Half/Cos 22.5
 Make "Points Shuffle [0 1 2 3 4 5 6 7]
End
To Gridd :M :N
 # return X Y screen position
 Make "X (:N*:CellSize) - :OffSet
 Make "Y (:M*:CellSize) - :OffSet
 Output List :X :Y
End
To DrawGrid :Order
 # draw M x N array of cubes
 For (List "M 0 :Order-1) [
 For (List "N 0 :Order-1) [
 SetPos Gridd :M :N
 Tile] ]
End
To Tile
 # connect 4 pairs of 8 points
 Make "PosC Pos
 Make "Points Shuffle [0 1 2 3 4 5 6 7] # comment out for overall
 Repeat 4 [
 SetPos :PosC SetH 0
 Path Item ((RepCount*2)-1) :Points Item (RepCount*2) :Points]
End
To Grout :Size :Order
 # draw centred square grid with :order divisions
 SetPC [64 64 64] SetPW 1
 For (List "Point 0 :Size+1 :Size/:Order) [
 SetXY :Point-:Size/2 :Size/2 SetH 180
 PenDown Forward :Size PenUp
 SetXY :Size/2 :Point-:Size/2 SetH 270
 PenDown Forward :Size PenUp Wait 2]
End
To Path :A :B
 # draw line or curve between points a and b
 Make "Diff Abs (:A-:B)
 SetH 202.5 +45*((:A+:B)/2)
 If :Diff=4 [
 Right 90 Forward :Dist Left 180 PenDown Forward 2*:Dist PenUp Stop]
 If :Diff >4 [Make "Diff 8-:Diff Left 180]
 Back :Dist/Cos (:Diff*22.5) Arch 180-(:Diff*45) :Dist*Tan (:Diff*22.5)
End
To Arch :Angle :Radius
 # symmetrical arc drawn relative to turtle heading
 Arc :Radius Heading-:Angle/2 
 Heading+:Angle/2
End
To Shuffle :myList
 # randomly shuffle list order
 LocalMake "Shuffled [ ] 
 Repeat Count :myList [
 LocalMake "Select Pick :myList
 Make "myList Remove :Select :myList
 LocalMake "Shuffled LPut :Select :Shuffled]
 Output :Shuffled
End
To Info
 SetPC White SetFontJustify [1 1]
 Home 
 Label :Points 
 Repeat 8 [
 Home SetH (RepCount*45)-20
 Forward 190 SetH 0 Label RepCount-1]
End
To Go :Order
 New Init :Order
 DrawGrid :Order
 # SetPC DarkRed SetPos [198 198] Fill # comment in
 If :Order <7 [Grout :Width :Order]
 If :Order =1 [Info]
End
```

Type **Go :order** for example **Go 7** to run.  

Type **Grout :order** to overlay grid lines.

Replace the path procedure with the one below for no-curve tiles.

```logo
To Path :A :B
 SetPos :PosC SetH 22.5 Right 45*:A
 Forward :Dist Make 
 "Pos1 Pos
 SetPos :PosC SetH 22.5 Right 45*:B
 Forward :Dist PenDown SetPos :Pos1 PenUp
End
```
