# Grids
![Grids](../../art/400x400/grid.png)

Grids are a useful starting point in many Logo programs.  

Here they've been extended into a kind of art, all of thier 
own.

```logo
To New
 # set default screen, pen and turtle values
 ResetAll SetScreenSize [400 400] HideTurtle
 SetSC Black SetPC Green SetPS 1 PenUp
End
To Init :Order
 # global values
 Make "Size 378
 Make "CellSize :Size/:Order
 Make "OffSet :CellSize/2 - :Size/2
 Make "Side :CellSize/1.4
 Make "Total Power :Order 2
End
To Gridd :M :N
 # return X Y screen position
 Make "X (:N*:CellSize) + :OffSet
 Make "Y (:M*:CellSize) + :OffSet
 Output List :X :Y
End
To Square :Side
 # square (side x side) drawn from centre (tp)
 Back :Side/2 Right 90 Back :Side/2 PenDown
 Repeat 4 [
 Forward :Side Left 90]
 PenUp Forward :Side/2 Left 90 Forward :Side/2 # r2c
End
To Hue2 :Theta
 # Output RGB hue list from angle :Theta
 Make "Red Abs 255*Sin :Theta
 Make "Green Abs 255*Sin (:Theta+120)
 Make "Blue Abs 255*Sin (:Theta+240)
 Output (List :Red :Green :Blue)
End
To RandGrid :Order
 # go to a random grid position
 SetPos Gridd Random :Order Random :Order
End
To DrawGrid :Order
 # draw M x N array of squares
 For (List "M 0 :Order-1) [
 For (List "N 0 :Order-1) [
 SetPos Gridd :M :N
 # SetH 0 Left 270*((:M+:N-2)/((2*:Order)-2))
 Square :Side] ]
End
To Go :Order
 # draw grid of squares
 New Init :Order
 DrawGrid :Order
End
```

Comment out the comment line in DrawGrid procedure for angled squares.

Using the above procedures, it is easy to generate more artwork.

```logo
To Art :Order
 # draw random array of colored squares
 New Init :Order
 Repeat :Total [
 RandGrid :Order
 SetPC Hue2 Random 360 Square :Side Fill]
End
To Art :Order
 # draw random lines
 New Init :Order
 SetPC Purple SetPW 3 * Round :CellSize/20
 For (List "M 1 :Order-2) [
 For (List "N 1 :Order-2) [
 SetPos Gridd :N :M
 SetH 90*Random 4
 PenDown Forward :CellSize PenUp] ]
End
To Art :Order
 # draw grid, random filled squares
 New Init :Order
 Drawgrid :Order
 SetPC DarkRed
 Repeat :Total [
 RandGrid :Order Fill]
End
To Art :Order
 # draw colour swatches
 New Init :Order
 Make "ColStep 255/(:Order-1)
 For (List "M 0 :Order-1) [
 For (List "N 0 :Order-1) [
 SetPos Gridd :M :N
 SetPC PenCol :M :N
 Square :Side Fill] ]
End
To PenCol :M :N
 Make "Red Round :N*:ColStep
 Make "Gre Round :M*:ColStep
 Output (List :Red :Gre 0)
End
```


Pasteoneof the above into XLogo.

Here are two methods of generating the same image. One uses lists 
and the other **FindCol** to fill in squares that are surrounded 
by other squares on all four sides.

```logo
To Art :Order
 New Init :Order
 Make "CellList []
 Repeat :Total [Make 
 "RanSq Random :Total
 If Not Member? :RanSq :CellList [
 SetPos Gridd (Modulo :RanSq :Order) (Quotient :RanSq :Order)
 Square :Side SetPC DarkRed Fill SetPC Green
 Make "CellList Lput :RanSq :CellList]]
 SetPC Red
 For (List "YY 1 :Order-2) [
 For (List "XX 1 :Order-2) [
 Make "Count :XX + :Order*:YY
 If And And And And
 (Member? :Count :CellList)
 (Member? :Count+1 :CellList)
 (Member? :Count-1 :CellList)
 (Member? :Count+:Order :CellList)
 (Member? :Count-:Order :CellList)
 [SetPos Gridd (Modulo :Count :Order) (Quotient :Count :Order) Fill] ] ]
End
To Art :Order
 New Init :Order
 Repeat :Total [
 RandGrid :Order
 Square :Side SetPC DarkRed Fill SetPC Green]
 SetPC Red
 For (List "M 1 :Order-2) [
 For (List "N 1 :Order-2) [
 SetPos Gridd :M :N
 If Check4 [PenDown Fill 
 PenUp] ] ]
End
To Check4
 # return true if square surrounded by 4 squares
 Make "C "True
 If ((First FindColor Pos)=0) [Output "False]
 Repeat 4 [
 Forward :CellSize
 If ((First FindColor Pos)=0) [Make "C "False Stop]
 Back :CellSize Right 90]
 Output :C
End
```


Pasteoneof the above into XLogo.

Enter **Art 7** or **Art 13** etc to run.  

For the first program, you can enter **Pr :CellList** to see a numeric list of every occupied cell.
