# Clover Art
![Clover Art](../../art/400x400/clover_art.png)

A random combination of Clover Leaf and Clover Wave. Each resulting shape 
is placed in a grid with randomly chosen background and fill colors.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :Order
 # global values
 Make "Board 384
 GlobalMake "CellSize :Board /:Order
 GlobalMake "OffSet (:Board -:CellSize) /2
End
To GridPos :CellX :CellY
 # return X Y screen position
 Make "PosX (:CellX*:CellSize) -:OffSet
 Make "PosY (:CellY*:CellSize) -:OffSet
 Output List :PosX :PosY
End
To DrawGrid :Order
 # draw CellX x CellY array of squares
 For (List "CellY 0 :Order -1) [
 For (List "CellX 0 :Order -1) [
 SetPos GridPos :CellX :CellY SetH 0
 SetPC Shuffle [0 128 255]
 If Even? :CellX +:CellY [SetPC Dark PenColor] Square :CellSize Fill
 SetPC Shuffle [0 128 255]
 If Even? :CellX +:CellY [Left 180 /:Order] [SetPC Dark PenColor]
 Clover_Rand :Order 0.9*:CellSize Fill Wait 12] ]
End
To Clover_Rand :N :Size
 # draw random clover shape of total diameter Size
 Make "Dist (:Size/2)/(1+Sin (180/:N))
 PenUp Repeat :N [
 If Pick [True False]
 [Forward :Dist Arch 180*(1+2/:N) (:Size/2)-:Dist]
 [Forward :Dist Left 180 Arch 180*(1-2/:N) (:Size/2)-:Dist Left 180]
 Back :Dist Left 360/:N]
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
To Arch :Angle :Radius
 # symmetrical arc drawn relative to turtle heading
 Arc :Radius Heading-:Angle/2 
 Heading+:Angle/2
End
To Dark :Hue
 # output rgb list midway between :hue and black
 Repeat 3 [
 Make "Hue ButFirst LPut Int (First :Hue)/2 :Hue]
 Output :Hue
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
To Go :Order
 New Init :Order
 DrawGrid :Order
End
```
